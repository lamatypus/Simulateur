package sockets;

import java.io.Serializable;

import paquets.PaquetUDP;

/**********************************************************************
 * <p>
 * But:<br>
 * Liaison UDP à élément
 * </p><p>
 * Description:<br>
 * Permet de faire le lien entre un socket UDP et un élément réseau ip.
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public interface SocketUDPHandler extends Serializable{

	/**Envoie le paquet au protocole de l'élément réseau.
	 * Méthode appelé par le socket.
	 * 
	 * @param p le paquet à envoyer à l'élément.
	 */
	public void sendToInterface(PaquetUDP p);
	
	/**Envoie le paquet au socket.
	 * Méthode appelé par l'élément.
	 * 
	 * @param p le paquet à envoyer au socket.
	 */
	public void sendToSocket(PaquetUDP p);
}
