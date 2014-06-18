package sockets;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import elements.ElementReseauIP;
import exception.PortOccupied;
import paquets.PaquetUDP;

/**********************************************************************
 * <p>
 * But:<br>
 * Socket TCP.
 * </p><p>
 * Description:<br>
 * Le socket TCP peut être initialisé soit par un serveur socket côté serveur
 * soit par la méthode connect() côté client.
 * Lors de l'établisement de connexion, on effectue le handshake.
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public class SocketUDP implements Serializable{

	private static final long serialVersionUID = 1L;
	private LinkedList<PaquetUDP> fifo = new LinkedList<PaquetUDP>();
	private ElementReseauIP elem;
	private int port;
	private SocketUDPHandler handler;
	
	public SocketUDP(ElementReseauIP e,int port) throws PortOccupied{
		this.elem = e;
		this.port = port;
		
		if(e.getSocketUDP()[port] != null)
			throw new PortOccupied();
		
		handler = new SocketUDPHandler() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void sendToInterface(PaquetUDP p) {
				p.envoie();
				
			}
			
			@Override
			public synchronized void sendToSocket(PaquetUDP p) {
				fifo.addLast(p);
				SocketUDP.this.wakeUp();
			}
		};
		
		elem.getSocketUDP()[port] = handler;
	}

	/**
	 * Réveil l'application qui attend sur une réception.
	 */
	private synchronized void wakeUp(){
		this.notify();
	}
	
	/**
	 * Ferme la connexion
	 */
	public void close(){
		if(elem.getSocketUDP()[port] == handler){
			elem.getSocketUDP()[port] = null;
		}
	}
	
	/**Attend un paquet depuis le socket. Si diponible dans le fifo, retour direct.
	 * Sinon, mise en attente du thread.
	 * 
	 * @return Le paquet reçu.
	 */
	public synchronized PaquetUDP read() {
		PaquetUDP p =null;
		try{
			p = fifo.getFirst();
		}catch(NoSuchElementException e){}
		while(p == null){
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			p = fifo.getFirst();
			fifo.removeFirst();
		}
		return p;
	}

	/**Permet d'envoyer un paquet UDP.
	 * 
	 * @param p le paquet à envoyer.
	 */
	public void write(PaquetUDP p) {
		p.setPortSource(port);
		handler.sendToInterface(p);
	}
	
}
