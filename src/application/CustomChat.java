package application;

import java.util.LinkedList;

import sockets.SocketServeurTCP;
import sockets.SocketTCP;
import standards.IPv4;
import elements.ElementReseauIP;
import elements.config.ShellPC;
import exception.IPNonValide;
import exception.PortOccupied;

/**********************************************************************
 * <p>
 * But:<br>
 * Application de chat exécuté en temps que client ou serveur.
 * </p><p>
 * Description:<br>
 * Cette application peremet de chatter entre plusieurs éléments réseau
 * PC. Pour exécuter l'application en temps que serveur le constructeur attend
 * un tableau de string "chat server port". Le port est remplacé par le numéro
 * voulu. Pour exécuté en client le constructeur attend "chat client ip_dest port",
 * où ip_dest et port sont remplacé par ceux voulu.
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public class CustomChat extends ExecutablePC {
	private static final String ERROR_USAGE = "Usage : chat [server,client] [port,ipDest port]\n";
	
	private boolean client;
	private Thread threadClient,threadServer;
	private Client clientHandler;
	private Serveur serveurHandler;
	private ElementReseauIP element;
	
	/**Lancement de l'application via le constructeur.
	 * 
	 * @param s		terminal d'interaction de l'application
	 * @param args	argument de commande
	 */
	public CustomChat(ShellPC s, String[] args) {
		super(s);
		element = s.getPC();
		if(args.length == 1){
			sortieConsole(ERROR_USAGE);
			exit();
		}
		else{
			if(args.length == 3 && args[1].equals("server")){
				try{
					int port = Integer.parseInt(args[2]);
					client = false;
					serveurHandler = new Serveur((short)port);
					threadServer = new Thread(serveurHandler);
					threadServer.start();
				}catch(NumberFormatException e){
					sortieConsole(ERROR_USAGE);
					exit();
				}
			}
			else if(args.length == 4 && args[1].equals("client")){
				try{
					IPv4 dest = new IPv4(args[2]);
					int port = Integer.parseInt(args[3]);
					client = true;
					clientHandler = new Client(dest,(short)port);
					threadClient = new Thread(clientHandler);
					threadClient.start();
				}catch(NumberFormatException e){
					sortieConsole(ERROR_USAGE);
					exit();
				} catch (IPNonValide e) {
					sortieConsole(ERROR_USAGE);
					exit();
				}
			}
			else{
				sortieConsole(ERROR_USAGE);
				exit();
			}
		}
		
	}
	
	@Override
	public void entreeConsole(String entree) {
		clientHandler.sock.write(entree);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void controleC() {
		if(client){
			clientHandler.sock.close();
			sortieConsole("Connection closed...\n");
		}
		else{
			for(Worker w: serveurHandler.client){
				w.sock.close();
				while(w.sock.isConnected());
			}
			sortieConsole("Server halt...\n");
			exit();
			threadServer.stop();
			serveurHandler.sock.close();
		}
	}
	

	/**
	 * 
	 * Classe implemente le serveur de l'application.
	 * 
	 * Le serveur écoute sur un port les nouvelles connexions et
	 * lance un nouveau worker pour chaque client.
	 *
	 */
	private class Serveur implements Runnable{
		SocketServeurTCP sock;
		LinkedList<Worker> client = new LinkedList<Worker>();
		
		/**Sélection du port d'écoute du serveur
		 * 
		 * @param port 	Le port d'écoute
		 */
		public Serveur(int port){
			try {
				sock = new SocketServeurTCP(CustomChat.this.element, port);
			} catch (PortOccupied e) {
				sortieConsole("erreur bind port");
				exit();
			}
			
		}
		@Override
		public void run() {
			while(true){
				SocketTCP tmp = sock.accept();
				
				if(tmp != null){
					Worker w = new Worker(tmp);
					new Thread(w).start();
					client.add(w);
				}
				else{
					break;
				}
			}
			
		}
		
	}
	
	/**
	 * 
	 * Le worker tiend la connexion avec un client pour le serveur.
	 *
	 */
	private class Worker implements Runnable{
		SocketTCP sock;
		
		/**Tiend la connexion sur un socket
		 * 
		 * @param sock	Le socket qui s'occupe du client.
		 */
		public Worker(SocketTCP sock){
			this.sock = sock;
		}
		@Override
		public void run() {
			while(true){
				String s = sock.read();
				if(s != null){
					sortieConsole(s + "\n");
					for(Worker tmp : CustomChat.this.serveurHandler.client){
						if(tmp != this){
							tmp.sock.write(s);
						}
					}
				}
				else{
					CustomChat.this.serveurHandler.client.remove(this);
					break;
				}
			}
		}
		
	}
	
	/**
	 * 
	 * Classe qui implémente le côté client de l'application.
	 * Il se connecte à un serveur et le chat commence.
	 *
	 */
	private class Client implements Runnable{
		short port;
		IPv4 dest;
		
		SocketTCP sock;
		
		/**Création de la connexion avec le serveur.
		 * 
		 * @param dest	ip de destination
		 * @param port	port de destination
		 */
		public Client(IPv4 dest, short port){
			this.port = port;
			this.dest = dest;
			
			sock = new SocketTCP(CustomChat.this.element);
			
		}
		@Override
		public void run() {
			sock.connect(dest, port);
			
			while(true){
				String s = sock.read();
				if(s == null){
					break;
				}
				else{
					sortieConsole(s + "\n");
				}
				
			}
			exit();
		}
		
	}
		
}
