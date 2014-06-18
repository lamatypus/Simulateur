package sockets;

import java.io.Serializable;

import paquets.PaquetUDP;

/**********************************************************************
 * <p>
 * But:<br>
 * Liaison UDP � �l�ment
 * </p><p>
 * Description:<br>
 * Permet de faire le lien entre un socket UDP et un �l�ment r�seau ip.
 * </p>
 *
 * @author		Rapha�l Buache
 * @author     	Magali Fr�lich
 * @author     	C�dric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public interface SocketUDPHandler extends Serializable{

	/**Envoie le paquet au protocole de l'�l�ment r�seau.
	 * M�thode appel� par le socket.
	 * 
	 * @param p le paquet � envoyer � l'�l�ment.
	 */
	public void sendToInterface(PaquetUDP p);
	
	/**Envoie le paquet au socket.
	 * M�thode appel� par l'�l�ment.
	 * 
	 * @param p le paquet � envoyer au socket.
	 */
	public void sendToSocket(PaquetUDP p);
}
