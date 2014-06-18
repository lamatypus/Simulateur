package application;

import paquets.PaquetICMP;
import paquets.PaquetIP.Protocole;
import controleur.Simulateur;
import sockets.SocketRaw;
import standards.CurrentTime;
import standards.IPv4;
import elements.ElementReseauIP;
import elements.config.ShellPC;
import exception.IPNonValide;
import exception.PortOccupied;

/**********************************************************************
 * <p>
 * But:<br>
 * Application ping standard
 * </p><p>
 * Description:<br>
 * Permet de vérifier la connexion entre deux élément réseau de la couche ip.
 * On utilise la commande de cette façon : ping ip_dest. Remplacer
 * ip_dest par l'ip de destination.
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public class PingApp extends ExecutablePC {

	private IPv4 ip_destination;
	private SocketRaw socket;
	private int nbSend = 0;
	private int nbReceive = 0;
	private boolean stop = false;
	private Thread send,receive;
	private ElementReseauIP elem;
	
	/**Lance l'application dans un terminal.
	 * Les paquets commancent directement à être envoyé.
	 * 
	 * @param pc	le terminal
	 * @param ip	l'ip de destination
	 */
	public PingApp(ShellPC pc, String ip){
		super(pc);
		
		elem = pc.getPC();
		
		try {
			ip_destination = new IPv4(ip);
			
			socket = new SocketRaw(pc.getPC(), Protocole.ICMP);
			
			sortieConsole("Envoie d'une requete ping sur : " + ip_destination + "\n");
			send = new Thread(new Send());
			receive = new Thread(new Receive());
			receive.start();
			send.start();
			
		} catch (IPNonValide e) {
			sortieConsole("Error : IP not valid\n");
			exit();	
			Simulateur.LOGGER.info("Invalid ip");
		}
		catch (PortOccupied e) {
			sortieConsole("Raw ICMP occupied\n");
			Simulateur.LOGGER.info("Raw ICMP occupied");
			exit();	
		}
	}

	@Override
	public void entreeConsole(String entree) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void controleC() {
		//Kill thread
		stop = true;

		send.stop();
		receive.stop();

		socket.close();
		
		sortieConsole("\n\nStatistiques Ping pour " + ip_destination + "\n");
		sortieConsole("   Paquets : envoyé = " + nbSend + ", recus = " + nbReceive + ", perdus = " + (nbSend - nbReceive) + " (perte " + (nbSend - nbReceive)/nbSend*100 + "%)\n");
		exit();	
	}
	
	/**
	 * Envoie des paquets icmp toutes les secondes au destinataire.
	 */
	private class Send implements Runnable{

		@Override
		public void run() {
			while(!stop){
				socket.write(new PaquetICMP(elem, "request".getBytes(), ip_destination));
				nbSend++;
				try {
					Thread.sleep(1000);
					
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * 
	 * Ecoute continue des messages reçu.
	 * Mise à jour des statistiques.
	 *
	 */
	private class Receive implements Runnable{
		@Override
		public void run() {
			while(!stop){
				PaquetICMP p = (PaquetICMP) socket.read();
				Simulateur.LOGGER.info("Ping receive");
				nbReceive++;
				sortieConsole("\nRéponse de " + p.getIpSource() + " : octets=" + p.getDonnee().length + " temps=" + CurrentTime.getInstance().getTime());
			}
		}		
	}


}
