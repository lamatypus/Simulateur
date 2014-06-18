package sockets;

import paquets.Paquet;

import paquets.PaquetIP;
/**********************************************************************
 * <p>
 * But:<br>
 * Liaison socket-pc
 * </p><p>
 * Description:<br>
 * Permet de faire la liaison entre le socket et les protocoles d'envoie
 * et de r�c�ption du type de paquet d�sir�.
 * </p>
 *
 * @author		Rapha�l Buache
 * @author     	Magali Fr�lich
 * @author     	C�dric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public abstract class SocketRawHandler {
	
	PaquetIP.Protocole proto;
	
	/**Initialise le type de paquet observ�
	 * 
	 * @param proto
	 */
	public SocketRawHandler(PaquetIP.Protocole proto){
		this.proto = proto;
	}
	
	/**Envoie le paquet au protocole de l'�l�ment r�seau.
	 * M�thode appel� par le socket.
	 * 
	 * @param p le paquet � envoyer � l'�l�ment.
	 */
	public abstract void sendToInterface(Paquet p);
	
	/**Envoie le paquet au socket.
	 * M�thode appel� par l'�l�ment.
	 * 
	 * @param p le paquet � envoyer au socket.
	 */
	public abstract void sendToSocket(Paquet p);
	
	/**Permet de connaitre le protocole courament observ�.
	 * 
	 * @return le protocole observ�.
	 */
	public PaquetIP.Protocole getProtocole(){
		return proto;
	}
}
