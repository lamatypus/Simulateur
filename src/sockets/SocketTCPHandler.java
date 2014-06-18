package sockets;

import paquets.PaquetTCP;

/**********************************************************************
 * <p>
 * But:<br>
 * Liaison TCP � �l�ment
 * </p><p>
 * Description:<br>
 * Permet de faire le lien entre un socket TCP et un �l�ment r�seau ip.
 * </p>
 *
 * @author		Rapha�l Buache
 * @author     	Magali Fr�lich
 * @author     	C�dric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public interface SocketTCPHandler {

	/**Envoie le paquet au protocole de l'�l�ment r�seau.
	 * M�thode appel� par le socket.
	 * 
	 * @param p le paquet � envoyer � l'�l�ment.
	 */
	public void sendToInterface(PaquetTCP p);
	
	/**Envoie le paquet au socket.
	 * M�thode appel� par l'�l�ment.
	 * 
	 * @param p le paquet � envoyer au socket.
	 */
	public void sendToSocket(PaquetTCP p);
}
