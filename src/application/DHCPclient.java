package application;

import java.io.Serializable;

import controleur.Simulateur;
import elements.PC;
import exception.IPNonValide;
import exception.MasqueNonValide;
import exception.PortOccupied;
import paquets.PaquetUDP;
import sockets.SocketUDP;
import standards.IPv4;
import standards.MasqueIPv4;
import elements.ElementReseauIP.InterfaceIP;
import elements.ElementReseauIP.MethodeApprentissageIP;

/**********************************************************************
 * <p>
 * But:<br>
 * Service DHCP client.
 * 
 * </p><p>
 * Description:<br>
 * Le client DHCP permet de configurer dynamiquement l'adresse ip du pc
 * en demandant celle-ci auprès d'un serveur DHCP. Celui-ci doit être dans
 * le même sous-réseau.
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public class DHCPclient implements Services,Serializable{

	private static final long serialVersionUID = 1L;
	private SocketUDP sock;
	private PC e;
	private IPv4 dhcpServeurCourant = null;
	private transient Thread send,receive;
	private boolean ipFind = false;
	
	/**
	 * Initialise le service sur un pc
	 * 
	 * @param e	Le pc où le service doit être initialisé.
	 */
	public DHCPclient(PC e) {
		this.e = e;
		
		try {
			sock = new SocketUDP(e, PortUDP.DHCPCLIENT.port());
			
		} catch (PortOccupied e1) {
			Simulateur.LOGGER.info("Port Occupied");
		}
		
		
	}

	/**Cette méthode recoit une offre d'ip depuis un serveur.
	 * Elle renvoie un message de demande au serveur.
	 * 
	 * @param ip		l'ip proposée
	 * @param ipServeur	le serveur qui l'envoie
	 * @param masque	le masque de sous réseau
	 */
	private void offer(String ip,IPv4 ipServeur,String masque,String gateway){
		if(dhcpServeurCourant == null){
			dhcpServeurCourant = ipServeur;
		
			sock.write(new PaquetUDP(e, ("DHCP REQUEST " + ip + " " + masque + " " + gateway).getBytes(), IPv4.getGeneralBroadcast(),
					PortUDP.DHCPSERVEUR.port()));
		}
		
	}
	
	/**Cette méthode enregistre la nouvelle ip et comfirme au serveur 
	 * que l'ip a été prise.
	 * 
	 * @param ip		l'ip proposée
	 * @param ipServeur	le serveur qui l'envoie
	 * @param masque	le masque de sous réseau
	 */
	private void ack(String ip,IPv4 ipServer,String masque,String gateway){
		if(ipServer.compare(dhcpServeurCourant)){
			try {
				IPv4 tmp = new IPv4(ip,new MasqueIPv4(masque).getCIDR());

				((InterfaceIP[])(e.getInterface()))[0].setIp(tmp, MethodeApprentissageIP.DHCP);
				
				if(!gateway.equals("null"))
					e.setDefaultGateway(new IPv4(gateway));
			
			} catch (IPNonValide e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (MasqueNonValide e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			ipFind = true;
		}
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public void stopService() {
		
		send.stop();
		receive.stop();
		
		sock.close();

	}

	@Override
	public String getName() {
		return "Client DHCP";
	}	

	/**
	 * Ecoute continue sur le socket des messages entrant.
	 * Suivant la correspondance, les messages sont envoyé au 
	 * différentes méthode.
	 *
	 */
	private class Receive implements Runnable{

		@Override
		public void run() {
			
			while(true){
				
				PaquetUDP p = sock.read();
				IPv4 dhcpServer = p.getIpSource();
				
				String data = new String(p.getDonnee());
				String[] decoupe = data.split(" ");
				
				if(data.startsWith("DHCP OFFER")){
					DHCPclient.this.offer(decoupe[2],dhcpServer,decoupe[3],decoupe[4]);
				}
				else if(data.startsWith("DHCP ACK")){
					
					DHCPclient.this.ack(decoupe[2],dhcpServer,decoupe[3],decoupe[4]);
					
				}
			}
		}
		
	}
	
	/**
	 * Envoie continue de demande d'ip tant qu'elle n'a pas
	 * été configurée.
	 *
	 */
	private class Envoie implements Runnable{

		@Override
		public void run() {

			while(!ipFind){
				PaquetUDP p = new PaquetUDP(e, ("DHCP DISCOVER").getBytes(),IPv4.getGeneralBroadcast(), PortUDP.DHCPSERVEUR.port());
				try {
					p.setIpSource(new IPv4("0.0.0.0"));
				} catch (IPNonValide e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				sock.write(p);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
	}

	@Override
	public void demarreService() {
		receive = new Thread(new Receive());
		send = new Thread(new Envoie());
		receive.start();
		send.start();
		
	}

	@Override
	public boolean configurable() {
		return false;
	}

	@Override
	public String getConfig() {
		return null;
	}

	@Override
	public void setConfig(String s) {

	}

}
