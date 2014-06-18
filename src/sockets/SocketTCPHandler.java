package sockets;

import paquets.PaquetTCP;

/**********************************************************************
 * <p>
 * But:<br>
 * Liaison TCP à élément
 * </p><p>
 * Description:<br>
 * Permet de faire le lien entre un socket TCP et un élément réseau ip.
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public interface SocketTCPHandler {

	/**Envoie le paquet au protocole de l'élément réseau.
	 * Méthode appelé par le socket.
	 * 
	 * @param p le paquet à envoyer à l'élément.
	 */
	public void sendToInterface(PaquetTCP p);
	
	/**Envoie le paquet au socket.
	 * Méthode appelé par l'élément.
	 * 
	 * @param p le paquet à envoyer au socket.
	 */
	public void sendToSocket(PaquetTCP p);
}
