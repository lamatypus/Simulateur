package protocoles;

import paquets.Paquet;
import paquets.PaquetUDP;
import elements.ElementReseauIP;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe représentant le protocole UDP
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler le comportement du protocole UDP permettant
 * d'établir une communication sans connexion entre des éléments réseau IP
 * </p>
 * 
 * @author Raphaël Buache
 * @author Magali Frölich
 * @author Cédric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public class ProtocoleUDP {

	/**
	 * Permet de savoir si le paquet est pour ce protocole
	 * 
	 * @param p: le paquet
	 * @return true si le paquet est pour ce protocole
	 */
	public static boolean monPaquet(Paquet p){
		return PaquetUDP.class.equals(p.getClass());
	}
	
	/**
	 * Permet de recevoir un paquet UDP sur un élément réseau IP et envoie le
	 * résultat à la socket UDP correspondante
	 * 
	 * @param e: l'élément réseau IP
	 * @param p: le paquet UDP
	 */
	public static void recoit(ElementReseauIP e, PaquetUDP p) {
		if(e.getSocketUDP()[p.getPortDest()] != null){
			e.getSocketUDP()[p.getPortDest()].sendToSocket(p);
		}
	}

	/**
	 * Permet d'envoyer un paquet UDP depuis un élément réseau IP en passant par
	 * le protocole IP
	 * 
	 * @param e: l'élément réseau IP
	 * @param p: le paquet UDP
	 */
	public static void envoie(ElementReseauIP e, PaquetUDP p) {
		//Appel couche du dessous
		ProtocoleIP.envoie(e, p);
	}

}
