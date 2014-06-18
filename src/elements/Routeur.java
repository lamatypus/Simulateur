package elements;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import controleur.Simulateur;
import application.PortUDP;
import elements.config.ConfigRouteur;
import exception.ProtocoleNonValide;
import elements.util.TableRoutage.Methode;
import elements.util.TableRoutage.Route;
import paquets.Paquet;
import paquets.PaquetARP;
import paquets.PaquetEthernet;
import paquets.PaquetIP;
import paquets.PaquetRIP;
import paquets.PaquetUDP;
import protocoles.ProtocoleARP;
import protocoles.ProtocoleEthernet;
import protocoles.ProtocoleIP;
import protocoles.ProtocoleRIP;
import protocoles.ProtocoleUDP;
import sockets.SocketUDPHandler;
import standards.IPv4;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe d'élément réseau routeur.
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de gérer le comportement des routeurs.
 * </p>
 * 
 * @author Raphaël Buache
 * @author Magali Frölich
 * @author Cédric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
@SuppressWarnings("serial")
public class Routeur extends ElementReseauIP implements Runnable {
	private static int globalId = 1;
	public int id;
	private boolean routeurRIP;
	private boolean transmetRouteDefaut;
	private boolean[] ripActive;
	LinkedList<IPv4> reseauxRIP;
	private String hostname;
	private String description;
	private boolean running;

	/**
	 * Créer un routeur avec un nombre d'interface IP
	 * 
	 * @param nbInterface: nombre d'interface
	 */
	public Routeur(int nbInterface) {
		super(nbInterface, "Fa 0/");

		ripActive = new boolean[nbInterface];
		for (int i = 0; i < ripActive.length; i++) {
			ripActive[i] = false;
		}
		
		// Les interfaces sont désactivée par défaut
		for (InterfaceIP i : getInterfacesIP()){
			i.setActive(false);
		}
		reseauxRIP = new LinkedList<>();
		id = globalId++;

		hostname = "Routeur-" + String.valueOf(id);
		
		// Start le gestionnaire RIP
		new Thread(this).start();

		// Défini la gestion de socket UDP pour le port de RIP
		getSocketUDP()[PortUDP.RIP.port()] = new SocketUDPHandler() {

			@Override
			public void sendToSocket(PaquetUDP p) {
				if (ProtocoleRIP.monPaquet(p)) {
					ProtocoleRIP.recoit(Routeur.this, (PaquetRIP) p);
				}
			}

			@Override
			public void sendToInterface(PaquetUDP p) {
				ProtocoleUDP.envoie(Routeur.this, p);
			}
		};
	}

	/**
	 * Créer un routeur utilisé pour l'interface graphique
	 * 
	 * @param nbInterface: nombre d'interface
	 * @param ghost
	 */
	public Routeur(int nbInterface, boolean ghost) {
		super(nbInterface, ghost);
	}
	
	/**
	 * Permet de stopper le thread s'occupant de RIP
	 */
	@Override
	public void destroy(){
		super.destroy();
		running = false;
	}

	/**
	 * Permet de définir les interfaces IP de sortie en fonction de l'adresse IP
	 * de destination
	 */
	@Override
	public InterfaceIP[] getInterfaceSortie(IPv4 dest) {
		InterfaceIP[] inf;
		if (dest.compare(IPv4.getGeneralBroadcast())) {
			inf = getInterfacesIP();
		} else {
			inf = new InterfaceIP[1];
			inf[0] = tableRoutage.getInterfaceIP(dest);
		}
		
		return inf;
	}

	/**
	 * Permet d'obtenir le nom du routeur et son numéro
	 */
	@Override
	public String getInfo() {
		return hostname;
	}

	/**
	 * Recoit le paquet sur son interface (i) en appellant le protocole Ethernet
	 * 
	 * @param p: le paquet
	 * @param i: l'interface
	 */
	@Override
	public void recoitCoucheLiaison(Paquet p, Interface i) {
		LOGGER.info(getInfo() + " = Recoit couche liaison");
		ProtocoleEthernet.recoit(i, this, (PaquetEthernet) p);
	}

	/**
	 * Recoit le paquet sur son interface (i) en appellant le bon protocole de
	 * la couche réseau
	 * 
	 * @param p: le paquet
	 * @param i: l'interface IP
	 */
	@Override
	public void recoitCoucheReseau(Paquet p, InterfaceIP i) {
		LOGGER.info(getInfo() + " = Recoit couche réseau");
		// Si paquet IP
		if (p instanceof PaquetIP) {
			ajouteEntree(((PaquetEthernet) p).getMacSource(),
					((PaquetIP) p).getIpSource());
			Route route = tableRoutage.getRoute(((PaquetIP) p).getIpDest());
			boolean estBroadcast = ((PaquetIP) p).getIpDest().compare(
					IPv4.getGeneralBroadcast());

			// Paquet destiné à une connexion locale au routeur
			if (route != null || estBroadcast) {
				// Destiné au routeur lui-même
				if (estBroadcast || route.getProchainSaut() == null) {
					if (estBroadcast
							|| route.getInterfaceSortie().getIp()
									.compare(((PaquetIP) p).getIpDest())) {
						try {
							ProtocoleIP.recoit(this, (PaquetIP) p);
						} catch (ProtocoleNonValide e) {
							e.printStackTrace();
						}
					}
					// Redirigde dans le même sous-réseau que le routeur
					else {
						((PaquetIP) p).setMacDest(getMac(((PaquetIP) p)
								.getIpDest()));
						route.getInterfaceSortie().envoyer(p);
					}
				}
				// Paquet à envoyer à un autre routeur
				else {
					((PaquetIP) p).setMacDest(getMac(route.getProchainSaut()));
					route.getInterfaceSortie().envoyer(p);
				}
			}
		}

		// Si paquet ARP
		else if (p instanceof PaquetARP) {
			ProtocoleARP.recoit(i, this, (PaquetARP) p);
		}
	}

	/**
	 * Ne fait rien, le routeur ne traite pas la couche transport
	 */
	@Override
	public void recoitCoucheTransport(Paquet p) {
	}

	/**
	 * Retourne les informations du routeur
	 */
	@Override
	public String allInfo() {
		
		String infos = "<h3>" + getInfo() + "</h3>";
		if (description != null){
			infos += "<h4>Description: " + description + "</h4>";
		}
		return infos += "<hr>" + toHtml() + "<hr>" + tableRoutage.toHtml();
	}
	

	/**
	 * Permet de configurer un routeur avec un shell
	 */
	@Override
	public void config() {
		new ConfigRouteur(this);
	}

	/**
	 * Permet de créer un nouveau routeur identique à l'actuel
	 */
	@Override
	public ElementReseau newElem() {
		return new Routeur(nbInterfaces);
	}

	/**
	 * Permet d'activé ou non le mode de routage RIP du routeur
	 * 
	 * @param active: si active
	 */
	public void setRouteurRIP(boolean active) {
		if (!active) {
			viderRouteRIP();
		}
		routeurRIP = active;
	}

	/**
	 * Permet d'ajouter un réseau qui écoute RIP
	 * 
	 * @param ip: l'adresse IP
	 */
	public void ajoutReseauRIP(IPv4 ip) {
		reseauxRIP.add(ip);
	}

	/**
	 * Permet de supprimer un réseau qui écoute RIP
	 * 
	 * @param ip: l'adresse IP
	 */
	public void supprimeReseauRIP(IPv4 ip) {
		reseauxRIP.remove(ip);
	}

	/**
	 * Permet de transmettre ou non la route par défaut avec RIP
	 * 
	 * @param transmet: si transmet
	 */
	public void setTransmetRouteDefaut(boolean transmet) {
		transmetRouteDefaut = transmet;
	}
	
	/**
	 * Permet de savoir si la route par défaut doit être transmise
	 * par RIP
	 * 
	 * @return true si elle doit être transmise
	 */
	public boolean getTransmetRouteDefaut() {
		return transmetRouteDefaut;
	}

	/**
	 * Permet de vider toute les routes apprise par RIP
	 */
	private void viderRouteRIP() {
		for (Route route : tableRoutage.getRoutes()) {
			if (route.getMethode().equals(Methode.RIP)) {
				supprimeRoute(route);
			}
		}
	}

	/**
	 * Permet d'obtenir les protocoles actifs sur le routeur
	 * 
	 * @return les protocoles
	 */
	public String getIpProtocols() {
		String reponse = "";

		if (routeurRIP) {
			if (!reseauxRIP.isEmpty()) {
				reponse = "Routing Protocol is rip\n"
						+ "Sending updates every 30 seconds\n"
						+ "Routing for Networks:\n";
				for (IPv4 ip : reseauxRIP) {
					reponse += "\t" + ip.toString() + "\n";
				}
			}
		}
		return reponse;
	}

	/**
	 * Permet d'enregistrer les informations du routeur
	 * 
	 * @param out: le flux de sortie
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeInt(globalId);
		out.writeInt(id);
	}

	/**
	 * Permet de lire les informations du routeur
	 * 
	 * @param in: le flux d'entrée
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		globalId = in.readInt();
		id = in.readInt();
	}

	/**
	 * Permet de reseter les id
	 */
	public static void reset() {
		globalId = 1;
	}
	
	/**
	 * Permet de configurer le nom du routeur
	 * 
	 * @param hostname: le nom du routeur
	 */
	public void setHostname (String hostname){
		this.hostname = hostname;
	}
	
	/**
	 * Permet d'obtenir le nom du routeur
	 * 
	 * @return le nom du routeur
	 */
	public String getHostname (){
		return hostname;
	}
		
	/**
	 * Permet d'obtenir la description du routeur
	 * 
	 * @return la description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Permet de configurer la description du routeur
	 * 
	 * @param description: la description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Permet d'obtenir les informations des interfaces en format Html 
	 * pour pouvoir être affichée
	 * 
	 * @return les infos des interfaces
	 */
	public String toHtml(){
		String status, protocole, ip, methode;
		String infos = "<table>"
				+ "<caption><b>Show ip interface brief</b></caption>"
				+ "<tr><th>Interface</th>"
				+ "<th>IP-Address</th>"
				+ "<th>Method</th>"
				+ "<th>Status</th>"
				+ "<th>Protocol</th></tr>";

		for(InterfaceIP i : getInterfacesIP()){
			if (i.isActive() && i.getIp() != null){
				status = "up";
				if (i.getInterfaceDest() != null){
					protocole = "up";
				}
				else{
					protocole = "down";
				}
			}
			else{
				status = "administratively down";
				protocole = "down";
			}
			if (i.getIp() == null){
				ip = "unassigned";
			}
			else{
				ip = i.getIp().toString();
			}
			if (i.getMethodeApprentissageIP() == null){
				methode = "unset";
			}
			else{
				methode = i.getMethodeApprentissageIP().toString();
			}
			infos += "<tr><td>" + i.getNom()+"</td>"
					+ "<td>"+ip+"</td>"
					+ "<td>"+methode+"</td>"
					+ "<td>"+status+"</td>"
					+ "<td>"+protocole+"</td></tr>";
		}
			
			return infos += "</table>";
	}

	/**
	 * Méthode s'occupant d'envoyer les paquets RIP toute les 30 secondes
	 */
	@Override
	public void run() {
		while (running) {
			try {
				Thread.sleep(30000);

				if (routeurRIP && running) {
					for (InterfaceIP i : getInterfacesIP()) {
						for (IPv4 ip : reseauxRIP) {
							if (i.getIp() != null
									&& i.getIp().estDansSousReseau(ip) >= 0) {
								ripActive[i.getNumero()] = true;
								break;
							}
						}
						if (ripActive[i.getNumero()]) {
							Simulateur.LOGGER.info("Envoie RIP sur interface "
									+ i.getNumero());
							PaquetRIP paquetRIP = new PaquetRIP(this, i.getIp()
									.toString().getBytes(), tableRoutage);
							paquetRIP.envoie();
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
