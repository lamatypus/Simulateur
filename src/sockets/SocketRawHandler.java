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
 * et de récéption du type de paquet désiré.
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public abstract class SocketRawHandler {
	
	PaquetIP.Protocole proto;
	
	/**Initialise le type de paquet observé
	 * 
	 * @param proto
	 */
	public SocketRawHandler(PaquetIP.Protocole proto){
		this.proto = proto;
	}
	
	/**Envoie le paquet au protocole de l'élément réseau.
	 * Méthode appelé par le socket.
	 * 
	 * @param p le paquet à envoyer à l'élément.
	 */
	public abstract void sendToInterface(Paquet p);
	
	/**Envoie le paquet au socket.
	 * Méthode appelé par l'élément.
	 * 
	 * @param p le paquet à envoyer au socket.
	 */
	public abstract void sendToSocket(Paquet p);
	
	/**Permet de connaitre le protocole courament observé.
	 * 
	 * @return le protocole observé.
	 */
	public PaquetIP.Protocole getProtocole(){
		return proto;
	}
}
