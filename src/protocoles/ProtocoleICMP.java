package protocoles;

import controleur.Simulateur;
import paquets.Paquet;
import paquets.PaquetICMP;
import paquets.PaquetIP;
import paquets.PaquetIP.Protocole;
import elements.ElementReseauIP;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe représentant le protocole ICMP
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler le comportement du protocole ICMP
 * permettant d'envoyer une requête "ping" sur un élément et de 
 * recevoir des réponses
 * </p>
 * 
 * @author Raphaël Buache
 * @author Magali Frölich
 * @author Cédric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public class ProtocoleICMP {

	/**
	 * Permet de savoir si le paquet est pour ce protocole
	 * 
	 * @param p: le paquet
	 * @return true si le paquet est pour ce protocole
	 */
	public static boolean monPaquet(Paquet p) {
		return PaquetICMP.class.equals(p.getClass());
	}

	/**
	 * Permet de recevoir un paquet ICMP sur un élément réseau IP et 
	 * d'y répondre en cas de requête
	 * 
	 * @param e: l'élément réseau IP
	 * @param p: le paquet ICMP
	 */
	public static void recoit(ElementReseauIP e, PaquetICMP p) {
		// Si réponse
		if (new String(p.getDonnee()).equals("reply")) {
			Simulateur.LOGGER.info("Protocole ICMP = Recoit <Reply> sur "
					+ e.getInfo());
			// Envoie la réponse à la socket
			if (e.getSocketRaw() != null
					&& e.getSocketRaw().getProtocole() == Protocole.ICMP) {
				e.getSocketRaw().sendToSocket(p);
			}
		} 
		// Si requête
		else if (new String(p.getDonnee()).equals("request")) {
			Simulateur.LOGGER.info("Protocole ICMP = Recoit <Request> sur "
					+ e.getInfo());
			// Envoie une réponse
			new PaquetICMP(e, "reply".getBytes(),
					((PaquetIP) p).getIpSource()).envoie();
		}
	}

	/**
	 * Perme d'envoyer une paquet ICMP depuis un élément réseau IP en
	 * passant par le protcole IP
	 * 
	 * @param e: l'élément réseau IP
	 * @param p: le paquet ICMP
	 */
	public static void envoie(ElementReseauIP e, PaquetICMP p) {
		Simulateur.LOGGER.info("Protocole ICMP = Envoie de " + e.getInfo());
		ProtocoleIP.envoie(e, p);
	}
}
