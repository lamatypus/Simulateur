package sockets;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import controleur.Simulateur;
import elements.ElementReseauIP;
import exception.PortOccupied;
import paquets.Paquet;
import paquets.PaquetIP.Protocole;

/**********************************************************************
 * <p>
 * But:<br>
 * Socket RAW.
 * </p><p>
 * Description:<br>
 * Le socket raw permet d'envoyer et recevoire des trames icmp (ou autre).
 * </p>
 *
 * @author		Rapha�l Buache
 * @author     	Magali Fr�lich
 * @author     	C�dric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public class SocketRaw {
	
	private LinkedList<Paquet> fifo = new LinkedList<Paquet>();
	private ElementReseauIP elem;
	private SocketRawHandler handler;
	
	/**Initialisation du socket.
	 * 
	 * @param e		�l�ment o� le socket doit s'ouvrir
	 * @param prot  protocole observ�.
	 * @throws PortOccupied exception si le protocole est d�j� observ�.
	 */
	public SocketRaw(ElementReseauIP e,Protocole prot) throws PortOccupied{
		this.elem = e;
		
		if(elem.getSocketRaw() != null)
			throw new PortOccupied();
		
		handler = new SocketRawHandler(prot) {
			
			@Override
			public synchronized void sendToSocket(Paquet p) {
				fifo.addLast(p);
				SocketRaw.this.wakeUp();
				Simulateur.LOGGER.info("Send To Socket");
			}
			
			@Override
			public synchronized void sendToInterface(Paquet p) {
				Simulateur.LOGGER.info("Send To Interface");
				p.envoie();
			}
		};
		
		elem.setSocketRaw(handler);
	}

	/**
	 * R�veil l'application qui attend sur une r�ception.
	 */
	private synchronized void wakeUp(){
		this.notify();
	}
	
	/**
	 * Ferme le socket
	 */
	public void close(){
		if(elem.getSocketRaw() == handler){
			elem.setSocketRaw(null);
		}
	}
	
	/**Attend un paquet depuis le socket. Si diponible dans le fifo, retour direct.
	 * Sinon, mise en attente du thread.
	 * 
	 * @return Le paquet re�u.
	 */
	public synchronized Paquet read() {
		Paquet p =null;
		try{
			p = fifo.getFirst();
		}catch(NoSuchElementException e){}
		while(p == null){
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Simulateur.LOGGER.info("Read fifo");
			p = fifo.getFirst();
			fifo.removeFirst();
		}
		return p;
	}

	/**
	 * Envoie un paquet sur le socket.
	 * @param p Le paquet � envoyer.
	 */
	public void write(Paquet p) {
		handler.sendToInterface(p);
	}
}
