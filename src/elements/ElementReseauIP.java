package elements;

import elements.util.TableARP;
import elements.util.TableNat;
import elements.util.TableRoutage;
import elements.util.TableRoutage.Methode;
import elements.util.TableRoutage.Route;
import exception.IPNonValide;
import paquets.Paquet;
import sockets.SocketRawHandler;
import sockets.SocketTCPHandler;
import sockets.SocketUDPHandler;
import standards.IPv4;
import standards.MAC;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe abtraite englobant les différents éléments réseaux IP héritant de la
 * classe d'élément réseau.
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de gérer des éléments réseaux IP, d'obtenir leurs
 * interfaces, de les connecter entre eux et d'obtenir des informations sur
 * ceux-ci.
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
public abstract class ElementReseauIP extends ElementReseau {
	private SocketUDPHandler[] sockUDP = new SocketUDPHandler[65536];
	private SocketTCPHandler[] sockTCP = new SocketTCPHandler[65536];
	private SocketRawHandler sockRaw;
	private TableNat tableNatTcp = new TableNat();
	private TableARP tableARP;
	protected TableRoutage tableRoutage = new TableRoutage(this);

	/**
	 * Créer un élément réseau IP avec un nombre d'interfaces IP et le nom
	 * utilisé pour le nom des interfaces IP
	 * 
	 * @param nbInterface: le nombre d'interface
	 * @param nom: le nom de l'interface
	 */
	public ElementReseauIP(int nbInterface, String nom) {
		super.nbInterfaces = nbInterface;
		Ilist = new InterfaceIP[nbInterface];
		for (int i = 0; i < nbInterface; i++) {
			Ilist[i] = new InterfaceIP(i, nom + i);
			Ilist[i].setActive(true);
		}
		tableARP = new TableARP(this);
	}

	/**
	 * Créer un élément réseau IP utilisé pour l'interface graphique
	 * 
	 * @param nbInterface: le nombre d'interface
	 * @param ghost
	 */
	public ElementReseauIP(int nbInterface, boolean ghost) {
		super(nbInterface, ghost);
	}

	/**
	 * Permet d'obtenir la un tableau contenant toute les interfaces IP de
	 * l'élément réseau IP
	 * 
	 * @return le tableau d'interfaces IP
	 */
	public InterfaceIP[] getInterfacesIP() {
		return (InterfaceIP[]) getInterface();
	}

	/**
	 * Permet de trouver l'adresse MAC d'une IP dans la table ARP
	 * 
	 * @param ip: l'adresse IP
	 * @return l'adresse MAC trouvée
	 */
	public MAC getMac(IPv4 ip) {
		return tableARP.getMac(ip);
	}

	/**
	 * Permet d'ajouter une entrée dans la talbe ARP avec une adresse MAC et son
	 * IP correspondante
	 * 
	 * @param mac: l'adresse MAC
	 * @param ip: l'adresse IP
	 */
	public void ajouteEntree(MAC mac, IPv4 ip) {
		tableARP.ajouteEntree(mac, ip);
	}

	/**
	 * Permet d'obtenir un tableau des sockets UDP
	 * 
	 * @return les socket UDP
	 */
	public SocketUDPHandler[] getSocketUDP() {
		return sockUDP;
	}

	/**
	 * Permet d'obtenir un tableau des sockets TCP
	 * 
	 * @return les socket TCP
	 */
	public SocketTCPHandler[] getSocketTCP() {
		return sockTCP;
	}

	/**
	 * Permet de configurer une socket RAW
	 * 
	 * @param s
	 */
	public void setSocketRaw(SocketRawHandler s) {
		sockRaw = s;
	}

	/**
	 * Permet d'obtenir le gestionnaire de socket RAW
	 * 
	 * @return
	 */
	public SocketRawHandler getSocketRaw() {
		return sockRaw;
	}

	/**
	 * Permet d'obtenir la table NAT de l'élément réseau IP
	 * 
	 * @return
	 */
	public TableNat getTableTCP() {
		return tableNatTcp;
	}
	

	/**
	 * Permet d'obtenir la table de routage de l'élément réseau
	 * 
	 * @return la table de routage
	 */
	public TableRoutage getTableRoutage() {
		return tableRoutage;
	}

	/**
	 * Permet d'obtenir l'interface IP correspondante avec l'adresse IP dans la
	 * table de routage
	 * 
	 * @param ip: l'adresse IP
	 * @return l'interface IP
	 */
	public InterfaceIP getInterfaceIP(IPv4 ip) {
		return tableRoutage.getInterfaceIP(ip);
	}

	/**
	 * Permet de créer une nouvelle route dans la table de routage avec une
	 * adresse IP de destination, une interface de sortie, une méthode
	 * d'apprentissage de route et un cout
	 * 
	 * @param ip: l'adresse IP
	 * @param i: l'interface IP
	 * @param methode: la méthode
	 * @param cout: le cout
	 */
	public void nouvelleRoute(IPv4 ip, InterfaceIP i, Methode methode, int cout) {
		nouvelleRoute(ip, i, null, methode, cout);
	}

	/**
	 * Permet de créer une nouvelle route dans la table de routage avec une
	 * adresse IP de destination, une adresse IP de prochain saut, une méthode
	 * d'apprentissage de route et un cout
	 * 
	 * @param ip: l'adresse IP
	 * @param via: l'adresse IP de prochain saut
	 * @param methode: la méthode
	 * @param cout: le cout
	 */
	public void nouvelleRoute(IPv4 ip, IPv4 via, Methode methode, int cout) {
		nouvelleRoute(ip, null, via, methode, cout);
	}

	/**
	 * Permet de créer une nouvelle route dans la table de routage avec une
	 * adresse IP de destination, une interface de sortie, une adresse IP de
	 * prochain saut, une méthode d'apprentissage de route et un cout
	 * 
	 * @param ip: l'adresse IP
	 * @param i: l'interface IP
	 * @param via: l'adresse IP de prochain saut
	 * @param methode: la méthode
	 * @param cout: le cout
	 */
	public void nouvelleRoute(IPv4 ip, InterfaceIP i, IPv4 via,
			Methode methode, int cout) {
		tableRoutage.nouvelleRoute(ip, i, via, methode, cout);
	}

	/**
	 * Permet de supprimer une route avec une adresse IP de destination et une
	 * interface de sortie
	 * 
	 * @param ip: l'adresse IP
	 * @param i: l'interface IP
	 */
	public void supprimeRoute(IPv4 ip, InterfaceIP i) {
		tableRoutage.supprimeRoute(ip, i);
	}

	/**
	 * Permet de supprimer une route avec une adresse IP de destination et une
	 * adresse IP de prochain saut
	 * 
	 * @param ip: l'adresse IP
	 * @param via: l'adresse IP de prochain saut
	 */
	public void supprimeRoute(IPv4 ip, IPv4 via) {
		tableRoutage.supprimeRoute(ip, via);
	}

	/**
	 * Permet de supprimer une route
	 * 
	 * @param route: la route
	 */
	public void supprimeRoute(Route route) {
		tableRoutage.supprimeRoute(route);
	}

	/**
	 * Permet d'obtenir une route pour une adresse IP de destination
	 * 
	 * @param ip: l'adresse IP
	 * @return la route
	 */
	public Route getRoute(IPv4 ip) {
		return tableRoutage.getRoute(ip);
	}

	/**
	 * Permet de definir sur quelles interface IP une adresse IP peut-elle être
	 * trouvée
	 * 
	 * @param dest: l'adresse IP de destination
	 * @return un tableau d'adresse IP
	 */
	public abstract InterfaceIP[] getInterfaceSortie(IPv4 dest);

	/**
	 * Défini ce que fait un élément réseau IP lorsqu'il recoit un paquet sur
	 * son interface IP au niveau de la couche réseau du modèle OSI
	 * 
	 * @param p: le paquet
	 * @param i: l'interface IP
	 */
	public abstract void recoitCoucheReseau(Paquet p, InterfaceIP i);

	/**
	 * Défini ce que fait un élément réseau IP lorsqu'il recoit un paquet7 au
	 * niveau de la couche transport du modèle OSI
	 * 
	 * @param p: le paquet
	 */
	public abstract void recoitCoucheTransport(Paquet p);

	/**********************************************************************
	 * <p>
	 * But:<br>
	 * Classe représentant les interfaces IP que les éléments réseaux IP
	 * contiennent
	 * </p>
	 * <p>
	 * Description:<br>
	 * Cette classe permet de gérer des interfaces IP d'éléments réseaux.
	 * </p>
	 * 
	 * @author Raphaël Buache
	 * @author Magali Frölich
	 * @author Cédric Rudareanu
	 * @author Yann Malherbe
	 * @version 1.0
	 * @modify 18.06.2014
	 ***********************************************************************/
	public class InterfaceIP extends Interface {
		private IPv4 ip;
		private MethodeApprentissageIP methodeApprentissageIP;

		/**
		 * Permet de créer une interface IP en lui donnant un nom et son numéro.
		 * L'adresse MAC est automatiquement générée
		 * 
		 * @param i: le numéro de l'interface
		 * @param nom: le nom de l'interface
		 */
		public InterfaceIP(int i, String nom) {
			super(i, nom);
		}

		/**
		 * Permet d'obtenir l'adresse IP de l'interface IP
		 * 
		 * @return l'adresse IP
		 */
		public IPv4 getIp() {
			return ip;
		}

		/**
		 * Permet de connaître la méthode d'apprentissage de l'adreses IP
		 * 
		 * @return la méthode d'apprentissage
		 */
		public MethodeApprentissageIP getMethodeApprentissageIP() {
			return methodeApprentissageIP;
		}

		/**
		 * Permet de configurer une adresse IP sur une interface IP avec une
		 * méthode d'apprentissage
		 * 
		 * @param ip: l'adresse IP
		 * @param methode: la méthode
		 */
		public void setIp(IPv4 ip, MethodeApprentissageIP methode) {
			if (this.ip != null){		
				try {
					supprimeRoute(new IPv4(this.ip.toString(), 32), this);
				} catch (IPNonValide e) {
					e.printStackTrace();
				}
				supprimeRoute(this.ip, this);
			}
			this.ip = ip;
			methodeApprentissageIP = methode;
			if (ip != null){
				nouvelleRoute(ip, this, Methode.DirectlyConnected, 1);
				try {
					nouvelleRoute(new IPv4(ip.toString(), 32), this,
							Methode.Local, 1);
				} catch (IPNonValide e) {
				}
			}
		}

		/**
		 * Permet de supprimer l'adresse IP de l'interface IP
		 */
		public void supprimeIP() {
			try {
				tableRoutage.supprimeRoute(ip, this);
				tableRoutage.supprimeRoute(new IPv4(ip.toString(), 32), this);
				methodeApprentissageIP = null;
				ip = null;
			} catch (IPNonValide e) {
			}
		}
	}

	/**
	 * Permet de définir comment une adresse IP d'interface IP a été apprise
	 * 
	 */
	public enum MethodeApprentissageIP {
		Manuel, DHCP;
	}
}
