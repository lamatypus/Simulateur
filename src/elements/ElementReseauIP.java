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
 * Classe abtraite englobant les diff�rents �l�ments r�seaux IP h�ritant de la
 * classe d'�l�ment r�seau.
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de g�rer des �l�ments r�seaux IP, d'obtenir leurs
 * interfaces, de les connecter entre eux et d'obtenir des informations sur
 * ceux-ci.
 * </p>
 * 
 * @author Rapha�l Buache
 * @author Magali Fr�lich
 * @author C�dric Rudareanu
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
	 * Cr�er un �l�ment r�seau IP avec un nombre d'interfaces IP et le nom
	 * utilis� pour le nom des interfaces IP
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
	 * Cr�er un �l�ment r�seau IP utilis� pour l'interface graphique
	 * 
	 * @param nbInterface: le nombre d'interface
	 * @param ghost
	 */
	public ElementReseauIP(int nbInterface, boolean ghost) {
		super(nbInterface, ghost);
	}

	/**
	 * Permet d'obtenir la un tableau contenant toute les interfaces IP de
	 * l'�l�ment r�seau IP
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
	 * @return l'adresse MAC trouv�e
	 */
	public MAC getMac(IPv4 ip) {
		return tableARP.getMac(ip);
	}

	/**
	 * Permet d'ajouter une entr�e dans la talbe ARP avec une adresse MAC et son
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
	 * Permet d'obtenir la table NAT de l'�l�ment r�seau IP
	 * 
	 * @return
	 */
	public TableNat getTableTCP() {
		return tableNatTcp;
	}
	

	/**
	 * Permet d'obtenir la table de routage de l'�l�ment r�seau
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
	 * Permet de cr�er une nouvelle route dans la table de routage avec une
	 * adresse IP de destination, une interface de sortie, une m�thode
	 * d'apprentissage de route et un cout
	 * 
	 * @param ip: l'adresse IP
	 * @param i: l'interface IP
	 * @param methode: la m�thode
	 * @param cout: le cout
	 */
	public void nouvelleRoute(IPv4 ip, InterfaceIP i, Methode methode, int cout) {
		nouvelleRoute(ip, i, null, methode, cout);
	}

	/**
	 * Permet de cr�er une nouvelle route dans la table de routage avec une
	 * adresse IP de destination, une adresse IP de prochain saut, une m�thode
	 * d'apprentissage de route et un cout
	 * 
	 * @param ip: l'adresse IP
	 * @param via: l'adresse IP de prochain saut
	 * @param methode: la m�thode
	 * @param cout: le cout
	 */
	public void nouvelleRoute(IPv4 ip, IPv4 via, Methode methode, int cout) {
		nouvelleRoute(ip, null, via, methode, cout);
	}

	/**
	 * Permet de cr�er une nouvelle route dans la table de routage avec une
	 * adresse IP de destination, une interface de sortie, une adresse IP de
	 * prochain saut, une m�thode d'apprentissage de route et un cout
	 * 
	 * @param ip: l'adresse IP
	 * @param i: l'interface IP
	 * @param via: l'adresse IP de prochain saut
	 * @param methode: la m�thode
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
	 * Permet de definir sur quelles interface IP une adresse IP peut-elle �tre
	 * trouv�e
	 * 
	 * @param dest: l'adresse IP de destination
	 * @return un tableau d'adresse IP
	 */
	public abstract InterfaceIP[] getInterfaceSortie(IPv4 dest);

	/**
	 * D�fini ce que fait un �l�ment r�seau IP lorsqu'il recoit un paquet sur
	 * son interface IP au niveau de la couche r�seau du mod�le OSI
	 * 
	 * @param p: le paquet
	 * @param i: l'interface IP
	 */
	public abstract void recoitCoucheReseau(Paquet p, InterfaceIP i);

	/**
	 * D�fini ce que fait un �l�ment r�seau IP lorsqu'il recoit un paquet7 au
	 * niveau de la couche transport du mod�le OSI
	 * 
	 * @param p: le paquet
	 */
	public abstract void recoitCoucheTransport(Paquet p);

	/**********************************************************************
	 * <p>
	 * But:<br>
	 * Classe repr�sentant les interfaces IP que les �l�ments r�seaux IP
	 * contiennent
	 * </p>
	 * <p>
	 * Description:<br>
	 * Cette classe permet de g�rer des interfaces IP d'�l�ments r�seaux.
	 * </p>
	 * 
	 * @author Rapha�l Buache
	 * @author Magali Fr�lich
	 * @author C�dric Rudareanu
	 * @author Yann Malherbe
	 * @version 1.0
	 * @modify 18.06.2014
	 ***********************************************************************/
	public class InterfaceIP extends Interface {
		private IPv4 ip;
		private MethodeApprentissageIP methodeApprentissageIP;

		/**
		 * Permet de cr�er une interface IP en lui donnant un nom et son num�ro.
		 * L'adresse MAC est automatiquement g�n�r�e
		 * 
		 * @param i: le num�ro de l'interface
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
		 * Permet de conna�tre la m�thode d'apprentissage de l'adreses IP
		 * 
		 * @return la m�thode d'apprentissage
		 */
		public MethodeApprentissageIP getMethodeApprentissageIP() {
			return methodeApprentissageIP;
		}

		/**
		 * Permet de configurer une adresse IP sur une interface IP avec une
		 * m�thode d'apprentissage
		 * 
		 * @param ip: l'adresse IP
		 * @param methode: la m�thode
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
	 * Permet de d�finir comment une adresse IP d'interface IP a �t� apprise
	 * 
	 */
	public enum MethodeApprentissageIP {
		Manuel, DHCP;
	}
}
