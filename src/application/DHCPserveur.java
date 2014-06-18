package application;

import java.io.Serializable;
import java.util.LinkedList;

import elements.Serveur;
import exception.IPNonValide;
import exception.MasqueNonValide;
import exception.PortOccupied;
import paquets.PaquetUDP;
import sockets.SocketUDP;
import standards.IPv4;
import standards.MAC;
import standards.MasqueIPv4;

/**********************************************************************
 * <p>
 * But:<br>
 * Service DHCP serveur
 * </p><p>
 * Description:<br>
 * Le serveur DHCP distribue des configurations ip au différent PC du sous-
 * réseau qui les demandes.
 * 
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public class DHCPserveur implements Services,Serializable{

	private static final long serialVersionUID = 1L;
	private Serveur e;
	private SocketUDP sock;
	private transient Thread receive;
	private String config = "";
	private boolean running =false;
	private LinkedList<Range> range = new LinkedList<Range>();
	
	/**
	 * Initialisation du service sur le serveur
	 * 
	 * @param e le serveur où le service doit être initialisé.
	 */
	public DHCPserveur(Serveur e) {
		this.e = e;

		try {
			sock = new SocketUDP(e, PortUDP.DHCPSERVEUR.port());
		} catch (PortOccupied el) {
			// TODO Auto-generated catch block
			el.printStackTrace();
		}
		
		//Config de base
		config =
				"#Default config file\n" +
				"\n" +
				"subnet 192.168.1.0 netmask 255.255.255.0 {\n" +
				"	range 192.168.1.10 192.168.1.100;\n" +
				"	range 192.168.1.150 192.168.1.200;\n" +
				"}";	
	}

	/**
	 * Traite les demandes discover d'un pc.
	 * 
	 * @param m adresse MAC du pc demandeur.
	 */
	private void discover(MAC m){
		IPv4 ip = null;
		MasqueIPv4 masque = null;
		for(Range r: range){
			if((ip = r.getNewIp()) != null){
				masque = r.masque;
				break;
			}
		}
		
		if(ip != null){
			try {
				IPv4 gateway = null;
				if(e.getDefaultGateway() != null){
					gateway = e.getDefaultGateway().getProchainSaut();
				}
				PaquetUDP p = new PaquetUDP(e, ("DHCP OFFER " + ip + " " + masque + " " + gateway).getBytes()
						,new IPv4("0.0.0.0"), PortUDP.DHCPCLIENT.port());
				p.setMacDest(m);
				sock.write(p);
			} catch (IPNonValide e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**Traite les demandes de confirmation d'un pc.
	 * 
	 * @param ip	ip voulu
	 * @param m		mac du client
	 * @param masque	masque de sous-réseau
	 */
	private void request(String ip,MAC m,String masque){
		PaquetUDP p = null;
		try {
			IPv4 gateway = null;
			if(e.getDefaultGateway() != null){
				gateway = e.getDefaultGateway().getProchainSaut();
			}
			p = new PaquetUDP(e, ("DHCP ACK " + ip + " " + masque + " " + gateway).getBytes()
					,new IPv4("0.0.0.0"), PortUDP.DHCPCLIENT.port());
		} catch (IPNonValide e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		p.setMacDest(m);
		if(p != null)
			sock.write(p);
	}
	
	@Override
	public void demarreService() {
		receive = new Thread(new Receive());
		running = true;
		parseConfig(config);
		receive.start();
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void stopService() {
		receive.stop();
		running = false;
	}


	@Override
	public String getName() {
		return "Serveur DHCP";
	}
	
	/**
	 * 
	 * Ecoute continue des messages entrant.
	 *
	 */
	private class Receive implements Runnable{

		@Override
		public void run() {
			
			while(true){
				PaquetUDP p = sock.read();
				
				String data = new String(p.getDonnee());
				
				if(data.startsWith("DHCP DISCOVER")){
					DHCPserveur.this.discover(p.getMacSource());
				}
				else if(data.startsWith("DHCP REQUEST")){
					DHCPserveur.this.request(data.split(" ")[2],p.getMacSource(),data.split(" ")[3]);
				}
				
			}
		}
		
	}
	
	/**
	 * Parse la configuration textuelle pour générer les classes 
	 * nécessaire au serveur
 	 * 
	 * @param config configuration
	 */
	private void parseConfig(String config){
		range.clear();
		
		String[] lines = config.split("\n");
		LinkedList<String> linesValide = new LinkedList<String>();
		MasqueIPv4 masque = null;
		for(String l : lines){
			if(!l.isEmpty() && !l.startsWith("#")){
				linesValide.add(l);
			}
		}
		for(String s : linesValide){
			String[] split = s.split(" ");
			if(split.length > 4 && split[0].equals("subnet")){
				try {
					masque = new MasqueIPv4(split[3]);
				} catch (MasqueNonValide e) {
					return;
				}
			}
			else if(split.length == 3 && split[0].endsWith("\trange") && masque != null){
				
				try {
					range.add(new Range(new IPv4(split[1]),
							new IPv4(split[2].substring(0, split[2].length()-1)),
							masque));
				} catch (IPNonValide e) {
					return;
				}
			}
		}
	}
	
	/**
	 * 
	 * Représente un "range" d'adresse que le serveur peut distribuer.
	 *
	 */
	private class Range implements Serializable{

		private static final long serialVersionUID = 1L;
		private int min,max,current;
		private MasqueIPv4 masque;
		
		
		public Range(IPv4 min,IPv4 max, MasqueIPv4 m){
			this.min = IPv4.getBinaire(min.getIPv4());
			this.max = IPv4.getBinaire(max.getIPv4());
			current = this.min;
			masque = m;
		}
		
		public IPv4 getNewIp(){
			IPv4 tmp = null;
			if(current <= max){
				
				try {
					tmp = new IPv4(IPv4.getIPv4(current));
				} catch (IPNonValide e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				current++;
			}
			return tmp;
		}
		
		@Override
		public String toString(){
			return "min:"+min+" max:"+max+" mask:"+masque;
		}
	}

	@Override
	public boolean configurable() {
		return true;
	}


	@Override
	public String getConfig() {
		return config;
	}


	@Override
	public void setConfig(String s) {
		if(!running)
			config = s;
	}
}
