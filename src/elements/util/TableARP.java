package elements.util;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.logging.Logger;
import paquets.Paquet;
import paquets.PaquetARP;
import paquets.PaquetARP.Style;
import standards.IPv4;
import standards.MAC;
import elements.ElementReseauIP;
import elements.ElementReseauIP.InterfaceIP;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe représentant la table ARP
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler le comportement d'une table ARP qui est
 * contenue dans tout élément réseau IP. Elle permet la translation d'adresse IP
 * en adresse MAC
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
public class TableARP implements Serializable {
	public static Logger LOGGER = Logger.getLogger("InfoLogging");
	private ElementReseauIP element;
	private LinkedList<ChampsARP> champsARP = new LinkedList<ChampsARP>();

	/**
	 * Créer une table ARP pour un élément réseau IP
	 * 
	 * @param element: l'élément réseau IP
	 */
	public TableARP(ElementReseauIP element) {
		this.element = element;
	}

	/**
	 * Permet d'ajouter une entrée ARP dans la table avec une adresse IP et une
	 * adresse MAC
	 * 
	 * @param mac: l'adresse MAC
	 * @param ip: l'adresse IP
	 */
	public void ajouteEntree(MAC mac, IPv4 ip) {
		champsARP.add(new ChampsARP(mac, ip));
		LOGGER.info("Table ARP de " + element.getInfo() + ": Ajout de " + mac
				+ " pour " + ip);
	}

	/**
	 * Permet de retourner l'adresse MAC correspondante à l'adresse IP passée en
	 * paramètre
	 * 
	 * @param ip: l'adresse IP
	 * @return l'adresse MAC correspondante à l'IP
	 */
	public MAC getMac(IPv4 ip) {
		InterfaceIP[] interfaces;
		Paquet newPaquet = null;

		if (ip.compare(IPv4.getGeneralBroadcast())) {
			return MAC.broadcast();
		}
		// Cherche dans la table ARP si adresse MAC connue
		for (ChampsARP c : champsARP) {
			if (c.adresseIP.compare(ip)) {
				return c.adresseMAC;
			}
		}

		// Récupère les interfaces correspondante pour l'IP choisie
		interfaces = element.getInterfaceSortie(ip);

		// Si pas d'interface définie
		if (interfaces == null)
			return null;

		// Pour chacune des interfaces
		for (InterfaceIP i : interfaces) {
			// On interroge chacune des interfaces avec des paquets ARP afin
			// de définir l'adresse MAC de l'IP
			newPaquet = new PaquetARP(element, (Style.Requete + " Who has "
					+ ip.toString() + " ?").getBytes(), i.getMac(),
					MAC.broadcast());

			newPaquet.envoie();
		}

		return null;
	}

	/**
	 * Champs ARP composer d'une adresse IP et de son adresse MAC correspondante
	 * 
	 */
	private class ChampsARP implements Serializable{
		private MAC adresseMAC;
		private IPv4 adresseIP;

		public ChampsARP(MAC adresseMAC, IPv4 adresseIP) {
			this.adresseMAC = adresseMAC;
			this.adresseIP = adresseIP;
		}
	}
}
