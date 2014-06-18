package protocoles;

import paquets.Paquet;
import paquets.PaquetUDP;
import elements.ElementReseauIP;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe repr�sentant le protocole UDP
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler le comportement du protocole UDP permettant
 * d'�tablir une communication sans connexion entre des �l�ments r�seau IP
 * </p>
 * 
 * @author Rapha�l Buache
 * @author Magali Fr�lich
 * @author C�dric Rudareanu
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
	 * Permet de recevoir un paquet UDP sur un �l�ment r�seau IP et envoie le
	 * r�sultat � la socket UDP correspondante
	 * 
	 * @param e: l'�l�ment r�seau IP
	 * @param p: le paquet UDP
	 */
	public static void recoit(ElementReseauIP e, PaquetUDP p) {
		if(e.getSocketUDP()[p.getPortDest()] != null){
			e.getSocketUDP()[p.getPortDest()].sendToSocket(p);
		}
	}

	/**
	 * Permet d'envoyer un paquet UDP depuis un �l�ment r�seau IP en passant par
	 * le protocole IP
	 * 
	 * @param e: l'�l�ment r�seau IP
	 * @param p: le paquet UDP
	 */
	public static void envoie(ElementReseauIP e, PaquetUDP p) {
		//Appel couche du dessous
		ProtocoleIP.envoie(e, p);
	}

}
