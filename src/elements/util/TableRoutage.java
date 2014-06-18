package elements.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Logger;
import standards.IPv4;
import elements.ElementReseauIP;
import elements.ElementReseauIP.InterfaceIP;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe représentant la table de routage
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler le comportement d'une table de routage qui est
 * contenue dans tout élément réseau IP. Elle permet de définir par quelle
 * interface envoyer un paquet pour une adresse IP de destination donnée
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
public class TableRoutage implements Serializable {
	public static Logger LOGGER = Logger.getLogger("InfoLogging");
	private ElementReseauIP element;
	private LinkedList<Route> tableRoutage = new LinkedList<Route>();

	/**
	 * Créer une table de routage pour un élément réseau IP associé
	 * 
	 * @param element
	 */
	public TableRoutage(ElementReseauIP element) {
		this.element = element;
	}

	/**
	 * Permet de créer une nouvelle route dans la table de routage avec une
	 * adresse IP de destination, une interface de sortie, une adresse IP de
	 * prochain saut, une méthode d'apprentissage de route et un cout
	 * 
	 * @param ip: l'adresse IP
	 * @param i: l'interface
	 * @param prochainSaut: l'adresse IP de prochain saut
	 * @param methode: la méthode
	 * @param cout: le cout
	 */
	public void nouvelleRoute(IPv4 ip, InterfaceIP i, IPv4 prochainSaut,
			Methode methode, int cout) {
		LOGGER.info("Table de routage de " + element.getInfo() + ": Ajout de "
				+ ip + " via " + prochainSaut);
		tableRoutage.add(new Route(ip, i, prochainSaut, methode, cout));
	}

	/**
	 * Permet de supprimer une route avec une adresse IP de destination et une
	 * interface de sortie
	 * 
	 * @param ip: l'adresse IP
	 * @param i: l'interface
	 */
	public void supprimeRoute(IPv4 ip, InterfaceIP i) {
		System.out.println(ip + "   " + i);
		for (Route route : tableRoutage) {
			if (route.ip.compare(ip)
					&& route.ip.getMasque().compare(ip.getMasque())
					&& route.interfaceIP.equals(i)) {
				System.out.println("found");
				tableRoutage.remove(route);
				break;
			}
		}
	}

	/**
	 * Permet de supprimer une route
	 * 
	 * @param route: la route
	 */
	public void supprimeRoute(Route route) {
		for (Route r : tableRoutage) {
			if (route.equals(r)) {
				tableRoutage.remove(route);
				break;
			}
		}
	}

	/**
	 * Permet de supprimer une route avec une adresse IP de destination et une
	 * adresse IP de prochain saut
	 * 
	 * @param ip: l'adresse IP
	 * @param prochainSaut: l'adresse IP de prochain saut
	 */
	public void supprimeRoute(IPv4 ip, IPv4 prochainSaut) {
		for (Route route : tableRoutage) {
			if (route.ip.compare(ip)
					&& route.prochainSaut.compare(prochainSaut)) {
				tableRoutage.remove(route);
				break;
			}
		}
	}

	/**
	 * Permet de savoir si la table de routage contient une route pour une
	 * adresse IP et la retourne sans appliquer la suite récursive
	 * 
	 * @param ip: l'adresse IP
	 * @return la route trouvée
	 */
	public Route contient(IPv4 ip) {
		for (Route route : tableRoutage) {
			if (route.ip.compare(ip)
					&& route.ip.getMasque().compare(ip.getMasque())) {
				return route;
			}
		}
		return null;
	}

	/**
	 * Permet de trouver la route correspondante pour une adresse IP de
	 * destination
	 * 
	 * @param ip: l'adresse IP
	 * @return la route
	 */
	public Route getRoute(IPv4 ip) {
		return getRoute(ip, null);
	}

	/**
	 * Permet de trouver la route correspondante pour une adresse IP de
	 * destination avec une adresse de prochain saut initiale
	 * 
	 * @param ip: l'adresse IP
	 * @param prochainSaut: l'adresse IP de prochain saut
	 * @return
	 */
	public Route getRoute(IPv4 ip, IPv4 prochainSaut) {
		Collections.sort(tableRoutage, Collections.reverseOrder());

		for (Route route : tableRoutage) {
			if (route.ip.estDansSousReseau(ip) >= 0) {
				if (route.prochainSaut != null) {
					if (route.prochainSaut == prochainSaut){
						return route;
					}
					return getRoute(route.prochainSaut, route.prochainSaut);
				} else {
					return new Route(route.ip, route.interfaceIP, prochainSaut,
							route.methode, route.cout);
				}
			}
		}
		return null;
	}

	/**
	 * Permet d'obtenir les routes sous forme de liste de routes
	 * 
	 * @return la liste de routes
	 */
	public LinkedList<Route> getRoutes() {
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
		return getRoute(ip).interfaceIP;
	}

	/**
	 * Permet d'obtenir la table de routage en String pour pouvoir être affichée
	 * 
	 * @return la table de routage
	 */
	@Override
	public String toString() {
		String prochainSaut, nom;
		String result = "Method\tIP dest\t\tCost\tNext hop\tOutput interface\n";
		for (Route route : tableRoutage) {
			prochainSaut = route.prochainSaut == null ? "unset"
					: route.prochainSaut.toString();
			nom = route.interfaceIP == null ? "-" : route.interfaceIP.getNom();
			result += route.methode.toString();
			result += "\t" + route.ip + "/" + route.ip.getMasque().getCIDR();
			result += "\t" + route.cout;
			result += "\t" + prochainSaut;
			result += "\t\t" + nom + "\n";
		}
		return result;
	}

	/**
	 * Permet d'obtenir la table de routage en format Html pour pouvoir être affichée
	 * 
	 * @return la table de routage
	 */
	public String toHtml() {
		String prochainSaut, nom;
		
		String result = "<table>"
				+ "<caption><b>Show ip route</b></caption>"
				+ "<tr><th>Method</th>"
				+ "<th>IP Dest</th>"
				+ "<th>Cost</th>"
				+ "<th>Next hop</th>"
				+ "<th>Output interface</th></tr>";
		
		for (Route route : tableRoutage) {
			prochainSaut = route.prochainSaut == null ? "unset"
					: route.prochainSaut.toString();
			nom = route.interfaceIP == null ? "-" : route.interfaceIP.getNom();
			
			result += "<tr><td>" + route.methode + "</td>"
					+ "<td>"+ route.ip +"</td>"
					+ "<td>" + route.cout+"</td>"
					+ "<td>"+ prochainSaut+"</td>"
					+ "<td>"+ nom +"</td></tr>";
		}
		return result += "</table>";
	}
	/**********************************************************************
	 * <p>
	 * But:<br>
	 * Classe représentant une route, c'est ce qui constitue la table de routage
	 * </p>
	 * <p>
	 * Description:<br>
	 * Cette classe permet de simuler le comportement d'une route, c'est elle
	 * qui va définir vers quelle interface de sortie ou vers quelle adresse IP
	 * un paquet va être envoyé
	 * 
	 * </p>
	 * 
	 * @author Raphaël Buache
	 * @author Magali Frölich
	 * @author Cédric Rudareanu
	 * @author Yann Malherbe
	 * @version 1.0
	 * @modify 18.06.2014
	 ***********************************************************************/
	@SuppressWarnings("rawtypes")
	public class Route implements Comparable, Serializable {
		private IPv4 ip;
		private InterfaceIP interfaceIP;
		private IPv4 prochainSaut;
		private int cout;
		private Methode methode;

		/**
		 * Créer une route en fonction de son adresse IP de destination, de
		 * l'interface de sortie, de l'adresse IP du prochain saut, de la
		 * méthode d'apprentissage de route et de son coût
		 * 
		 * @param ip: l'adresse IP
		 * @param i: l'interface
		 * @param prochainSaut: l'adresse IP de prochain saut
		 * @param methode: la méthode
		 * @param cout: le cout
		 */
		public Route(IPv4 ip, InterfaceIP i, IPv4 prochainSaut,
				Methode methode, int cout) {
			this.ip = ip;
			interfaceIP = i;
			this.prochainSaut = prochainSaut;
			this.methode = methode;
			this.cout = cout;
		}

		/**
		 * Permet d'obtenir l'adresse IP de destination de la route
		 * 
		 * @return l'IP
		 */
		public IPv4 getIp() {
			return ip;
		}

		/**
		 * Permet d'obtenir l'interface de sortie de la route
		 * 
		 * @return l'interface
		 */
		public InterfaceIP getInterfaceSortie() {
			return interfaceIP;
		}

		/**
		 * Permet d'obtenir l'adresse IP du prochain saut de la route
		 * 
		 * @return l'IP
		 */
		public IPv4 getProchainSaut() {
			return prochainSaut;
		}

		/**
		 * Permet d'obtenir le coût de la route
		 * 
		 * @return le cout
		 */
		public int getCout() {
			return cout;
		}

		/**
		 * Permet d'obtenir la méthode d'apprentissage de la route
		 * 
		 * @return la méthode
		 */
		public Methode getMethode() {
			return methode;
		}

		/**
		 * Permet de comparer deux routes entre-elles selon leur masque en
		 * format CIDR
		 */
		@Override
		public int compareTo(Object o) {
			int masqueCIDR = ((Route) o).ip.getMasque().getCIDR();
			return ip.getMasque().getCIDR() - masqueCIDR;
		}
	}

	/**
	 * Méthode d'apprentissage de la route
	 * 
	 */
	public enum Methode {
		Local("L"), DirectlyConnected("C"), Static("S"), RIP("R");

		private String valeur;

		private Methode(String valeur) {
			this.valeur = valeur;
		}

		@Override
		public String toString() {
			return valeur;
		}
	}
}
