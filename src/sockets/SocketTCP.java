package sockets;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;
import paquets.PaquetTCP;
import paquets.PaquetTCP.Sync;
import standards.IPv4;
import elements.ElementReseauIP;


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
public class SocketTCP implements Serializable{

	private static final long serialVersionUID = 1L;
	private LinkedList<PaquetTCP> fifo = new LinkedList<PaquetTCP>();
	private ElementReseauIP elem;
	private int portSource,portDest;
	private int portMachine;
	private SocketTCPHandler handler;
	private IPv4 ipDest;
	private boolean connected = false;
	private static Random rand = new Random();
	
	/**
	 * Initialisation du socket sur un élément réseau.
	 * Définition du port automatique.
	 * 
	 * @param e l'élément où l'on veut un socket
	 */
	public SocketTCP(ElementReseauIP e) {
		this.elem = e;
		
		portMachine = (rand.nextInt(60000) + 1024);
		while(elem.getSocketTCP()[portMachine] != null){
			portMachine = (short)(rand.nextInt(60000) + 1024);
		}
		portSource = portMachine;
		handler = new SocketTCPHandler() {
			
			@Override
			public void sendToInterface(PaquetTCP p) {
				p.envoie();
				
			}
			
			@Override
			public synchronized void sendToSocket(PaquetTCP p) {
				fifo.addLast(p);
				SocketTCP.this.wakeUp();

			}
		};
	}
	
	/**Configuration du socket par un socketServeur
	 * 
	 * @param e			élément réseau
	 * @param ipDest	ip de destination
	 * @param source	port source virtuelle (celui-du serveur socket)
	 * @param dest		port de destination
	 */
	SocketTCP(ElementReseauIP e,IPv4 ipDest, int source,int dest) {
		this(e);
		
		portSource = source;
		portDest = dest;
		this.ipDest = ipDest;
		elem.getTableTCP().nouvelleEntree(ipDest, source, portMachine);
		
		elem.getSocketTCP()[portMachine] = handler;
		
		connected = true;
	}

	/**
	 * Réveil l'application qui attend sur une réception.
	 */
	private synchronized void wakeUp(){
		this.notify();
	}
	
	/**Permet de savoir si le socket est connecté.
	 * 
	 * @return true, si oui.
	 */
	public boolean isConnected(){
		return connected;
	}
	
	/**Permet une connexion à un serveur socket.
	 * 
	 * @param ipDest	ip de destination
	 * @param portDest	port de destination
	 */
	public synchronized void connect(IPv4 ipDest,int portDest){
		elem.getSocketTCP()[portMachine] = handler;
		PaquetTCP tmp = new PaquetTCP(elem,"SYN".getBytes(),ipDest,portDest,portSource);
		tmp.setSync(Sync.SYN);
		handler.sendToInterface(tmp);
		
		try {
			this.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(tmp.getSync() != Sync.SYN_ACK){
			
			tmp = fifo.getFirst();
			fifo.removeFirst();
		}
		tmp = new PaquetTCP(elem,"ACK".getBytes(),ipDest,portDest,portSource);
		tmp.setSync(Sync.ACK);
		handler.sendToInterface(tmp);
		
		this.ipDest = ipDest;
		this.portDest = portDest;
		
		connected = true;
	}
	
	/**
	 * Ferme la connexion.
	 */
	public synchronized void close(){
		
		PaquetTCP tmp = new PaquetTCP(elem,"FIN".getBytes(),ipDest,portDest,portSource);
		tmp.setSync(Sync.FIN);
		handler.sendToInterface(tmp);
		
	}
	
	/**Attend un contenu depuis le socket. Si diponible dans le fifo, retour direct.
	 * Sinon, mise en attente du thread.
	 * 
	 * @return Le contenu.
	 */
	public synchronized String read() {
		PaquetTCP tmp = null;
		
		if(connected){
			try{
				tmp = fifo.getFirst();
			}catch(NoSuchElementException e){}
			while(tmp == null){
				try {
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tmp = fifo.getFirst();
				fifo.removeFirst();
				
				if(tmp.getSync() == Sync.FIN){
					connected = false;
					PaquetTCP close = new PaquetTCP(elem,"FIN_ACK".getBytes(), ipDest,portDest, portSource);
					close.setSync(Sync.FIN_ACK);
					handler.sendToInterface(close);
					
					while(close.getSync() != Sync.ACK){
						try {
							this.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						close = fifo.getFirst();
						fifo.removeFirst();
					}
					//Close port
					if(elem.getSocketTCP()[portMachine] == handler){
						elem.getSocketTCP()[portMachine] = null;
						elem.getTableTCP().supprimeEntree(ipDest, portSource);
					}
					connected = false;
					return null;
					
				}
				else if(tmp.getSync() == Sync.FIN_ACK){
					connected = false;
					PaquetTCP close = new PaquetTCP(elem,"ACK".getBytes(), ipDest,portDest, portSource);
					close.setSync(Sync.ACK);
					handler.sendToInterface(close);
					//Close port
					if(elem.getSocketTCP()[portMachine] == handler){
						elem.getSocketTCP()[portMachine] = null;
						elem.getTableTCP().supprimeEntree(ipDest, portSource);
					}
					connected = false;
				}
				else{
					return new String(tmp.getDonnee());
				}
			}
			
		}
		return null;
	}

	/**Envoie un contenu au serveur.
	 * 
	 * @param s le contenu à envoyer.
	 */
	public void write(String s) {
		if(connected){
			handler.sendToInterface(new PaquetTCP(elem, s.getBytes(), ipDest, portDest, portSource));
		}
	}
}
