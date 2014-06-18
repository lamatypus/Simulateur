package application;

import paquets.PaquetUDP;
import sockets.SocketUDP;
import standards.IPv4;
import elements.ElementReseauIP;
import elements.config.ShellPC;
import exception.IPNonValide;
import exception.PortOccupied;

/**********************************************************************
 * <p>
 * But:<br>
 * Application pour tester la couche transport
 * </p><p>
 * Description:<br>
 * Permet de forger un paquet UDP que l'on peut envoyer sur le réseau.
 * Par défaut, l'adresse de destination est 192.168.1.1
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
public class ForgeUDP extends ExecutablePC {
	
	private SocketUDP sock;
	
	/**Ouvre l'application sur un terminal
	 * Il n'y a pas d'argument. Le paquet est directement envoyé.
	 * 
	 * @param s		le terminal
	 * @param args	les arguments
	 */
	public ForgeUDP(ShellPC s, String[] args) {
		
		super(s);
		
		try {

			sock = new SocketUDP(s.getPC(), (short)56);
			PaquetUDP p= new PaquetUDP((ElementReseauIP)s.getPC(),"forge udp".getBytes(),new IPv4(),(short)43);
			try {
				p.setIpDest(new IPv4("192.168.1.1"));
			} catch (IPNonValide e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sock.write(p);
			
		} catch (PortOccupied e1) {
			sortieConsole("erreur port occupe");
			e1.printStackTrace();
		}
		
		exit();
	}

	@Override
	public void entreeConsole(String entree) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controleC() {
		// TODO Auto-generated method stub
		
	}

}
