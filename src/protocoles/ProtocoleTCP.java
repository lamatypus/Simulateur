package protocoles;

import paquets.Paquet;
import paquets.PaquetIP;
import paquets.PaquetTCP;
import elements.ElementReseauIP;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe représentant le protocole TCP
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler le comportement du protocole TCP permettant
 * d'établir une communication avec connexion entre des éléments réseau IP
 * </p>
 * 
 * @author Raphaël Buache
 * @author Magali Frölich
 * @author Cédric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public class ProtocoleTCP {

	/**
	 * Permet de savoir si le paquet est pour ce protocole
	 * 
	 * @param p: le paquet
	 * @return true si le paquet est pour ce protocole
	 */
	public static boolean monPaquet(Paquet p) {
		return PaquetTCP.class.equals(p.getClass());
	}

	/**
	 * Permet de recevoir un paquet TCP sur un élément réseau IP et envoie le
	 * résultat à la socket TCP correspondante
	 * 
	 * @param e: l'élément réseau IP
	 * @param p: le paquet TCP
	 */
	public static void recoit(ElementReseauIP e, PaquetTCP p) {
		int port = e.getTableTCP().getPortMachine(p.getIpSource(),
				p.getPortDest());
		if (e.getSocketTCP()[p.getPortDest()] != null) {
			e.getSocketTCP()[port].sendToSocket(p);
		}
	}

	/**
	 * Permet d'envoyer un paquet TCP depuis un élément réseau IP en passant par
	 * le protocole IP
	 * 
	 * @param e: l'élément réseau IP
	 * @param p: le paquet TCP
	 */
	public static void envoie(ElementReseauIP e, PaquetTCP p) {
		ProtocoleIP.envoie(e, (PaquetIP) p);
	}

}
