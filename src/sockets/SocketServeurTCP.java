package sockets;

import java.io.Serializable;

import paquets.PaquetTCP;
import paquets.PaquetTCP.Sync;
import standards.IPv4;
import elements.ElementReseauIP;
import exception.PortOccupied;

/**********************************************************************
 * <p>
 * But:<br>
 * Socket serveur TCP.
 * </p><p>
 * Description:<br>
 * Le serveur socket TCP permet d'écouter les connexions entrante et 
 * d'attribuer un nouveau socket à un worker avec la méthode accept().
 * Un NAT est mis en place pour chaque connexion.
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public class SocketServeurTCP implements Serializable{

	private static final long serialVersionUID = 1L;
	private IPv4 waitIP;
	private int waitPort;
	private ElementReseauIP elem;
	private int port;
	private SocketTCPHandler handler;
	private boolean close = false;
	
	/**Création du serverSocket
	 * 
	 * @param e			Elément réseau sur lequel on veut le socket
	 * @param portBind	le port d'écoute
	 * @throws PortOccupied	exception si le port est déjà occupé.
	 */
	public SocketServeurTCP(ElementReseauIP e,int portBind) throws PortOccupied{
		this.elem = e;
		this.port = portBind;
		
		if(e.getSocketUDP()[port] != null)
			throw new PortOccupied();
		
		handler = new SocketTCPHandler() {
			
			@Override
			public void sendToInterface(PaquetTCP p) {
				p.envoie();
			}
			
			@Override
			public synchronized void sendToSocket(PaquetTCP p) {
				recoit(p);
			}

		};
		
		elem.getSocketTCP()[port] = handler;
	}

	/**Méthode qui définit le mécanisme de handshake du TCP.
	 * Une fois terminé, un nouveau socket dans la méthode accept() est créé.
	 * 
	 * @param p le paquet reçu.
	 */
	@SuppressWarnings("incomplete-switch")
	private synchronized void recoit(PaquetTCP p){
		switch(p.getSync()){
		case SYN:
			PaquetTCP newPaquet = new PaquetTCP(elem, "SYN_ACK".getBytes(), p.getIpSource(), p.getPortSource(), port);
			newPaquet.setSync(Sync.SYN_ACK);
			handler.sendToInterface(newPaquet);
			break;
		case ACK:
			waitIP = p.getIpSource();
			waitPort = p.getPortSource();
			this.notify();
			break;
		}
	}
	
	/**Méthode bloquante. Attend d'une nouvelle connexion sur le serveur.
	 * 
	 * @return le nouveau socket de la connexion.
	 */
	public synchronized SocketTCP accept(){
		SocketTCP sock = null;
		
		try {
			this.wait();

			if(close == false)
				sock = new SocketTCP(elem,waitIP,port,waitPort);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sock;
	}
	
	/**
	 * Ferme le socket
	 */
	public synchronized void close(){
		close = true;
		this.notify();
		if(elem.getSocketTCP()[port] == handler){
			elem.getSocketTCP()[port] = null;
		}
	}
}
