package protocoles;

import controleur.Simulateur;
import paquets.Paquet;
import paquets.PaquetARP;
import paquets.PaquetARP.Style;
import paquets.PaquetEthernet;
import standards.IPv4;
import elements.ElementReseauIP;
import elements.ElementReseauIP.InterfaceIP;
import exception.IPNonValide;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe représentant le protocole ARP
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler le comportement du protocole ARP en essayer de
 * translater des adresse IP en adresse MAC
 * </p>
 * 
 * @author Raphaël Buache
 * @author Magali Frölich
 * @author Cédric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public class ProtocoleARP {

	/**
	 * Permet de savoir si le paquet est pour ce protocole
	 * 
	 * @param p: le paquet
	 * @return true si le paquet est pour ce protocole
	 */
	public static boolean monPaquet(Paquet p) {
		return PaquetARP.class.equals(p.getClass());
	}

	/**
	 * Permet de recevoir un paquet ARP sur une interface IP d'un élément réseau
	 * IP
	 * 
	 * @param i: l'interface IP
	 * @param e: l'élément réseau IP
	 * @param p: le paquet ARP
	 */
	public static void recoit(InterfaceIP i, ElementReseauIP e, PaquetARP p) {
		String donnee = new String(p.getDonnee());

		// Si requête
		if (donnee.startsWith(Style.Requete.toString())) {
			try {
				Simulateur.LOGGER.info("Protocole ARP = Recoit <Request> sur "
						+ e.getInfo());
				IPv4 ip = new IPv4(donnee.substring(
						(Style.Requete + " Who has ").length(),
						donnee.length() - 2));

				// Répond avec un nouveau paquet
				if (ip.compare(i.getIp())) {
					PaquetARP newPaquet = new PaquetARP(e,
							(Style.Reponse.toString() + " " + i.getMac()
									+ " has " + ip).getBytes(), i.getMac(),
							((PaquetEthernet) p).getMacSource());
					newPaquet.envoie();
				}
			} catch (IPNonValide e1) {
				e1.printStackTrace();
			}
		}

		// Si réponse
		else if (donnee.startsWith(Style.Reponse.toString())) {
			Simulateur.LOGGER.info("Protocole ARP = Recoit <Response> sur "
					+ e.getInfo());
			String[] s = donnee.split(" has ");

			try {
				if (s.length < 1) {
					throw new IPNonValide();
				}

				// Enregistre l'ip dans la table ARP
				IPv4 ip = new IPv4(s[1]);
				e.ajouteEntree(((PaquetEthernet) p).getMacSource(), ip);
				e.wakeupPaquets();
			} catch (IPNonValide e1) {
			}
		}
	}

	/**
	 * Permet d'envoyer un paquet ARP par le biais du protocole Ethernet
	 * 
	 * @param e: l'élément réseau IP
	 * @param p: le paquet ARP
	 */
	public static void envoie(ElementReseauIP e, PaquetARP p) {
		Simulateur.LOGGER.info("Protocole ARP = Envoie de " + e.getInfo());
		ProtocoleEthernet.envoie(e, (PaquetEthernet) p);
	}
}
