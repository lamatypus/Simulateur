package protocoles;

import controleur.Simulateur;
import paquets.Paquet;
import paquets.PaquetEthernet;
import paquets.PaquetICMP;
import paquets.PaquetIP;
import paquets.PaquetTCP;
import paquets.PaquetUDP;
import standards.IPv4;
import standards.MAC;
import elements.ElementReseauIP;
import elements.ElementReseauIP.InterfaceIP;
import exception.IPNonValide;
import exception.ProtocoleNonValide;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe représentant le protocole IP
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler le comportement du protocole IP 
 * permettant la communication entre deux éléments à la couche réseau
 * </p>
 * 
 * @author Raphaël Buache
 * @author Magali Frölich
 * @author Cédric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public class ProtocoleIP {

	/**
	 * Permet de savoir si le paquet est pour ce protocole
	 * 
	 * @param p: le paquet
	 * @return true si le paquet est pour ce protocole
	 */
	public static boolean monPaquet(Paquet p) {
		return PaquetIP.class.equals(p.getClass());
	}

	/**
	 * Permet de recevoir un paquet IP sur un élément réseau IP et d'appeler le
	 * bon protocole de la couche transport pour recevoir le paquet
	 * 
	 * @param e: l'élément réseau IP
	 * @param p: le paquet IP
	 * @throws ProtocoleNonValide
	 */
	public static void recoit(ElementReseauIP e, PaquetIP p)
			throws ProtocoleNonValide {
		Simulateur.LOGGER.info("Protocole IP = Recoit sur " + e.getInfo());
		switch (p.getProtocole()) {
		case ICMP:
			ProtocoleICMP.recoit(e, (PaquetICMP) p);
			break;
		case TCP:
			ProtocoleTCP.recoit(e, (PaquetTCP) p);
			break;
		case UDP:
			ProtocoleUDP.recoit(e, (PaquetUDP) p);
			break;
		default:
			throw new ProtocoleNonValide();
		}
	}

	/**
	 * Permet d'envoyer un paquet IP sur un élément réseau IP
	 * 
	 * @param e: l'élément réseau IP
	 * @param p: le paquet IP
	 */
	public static void envoie(ElementReseauIP e, PaquetIP p) {
		int compteurReqARP = 0;
		MAC macDest = null;
		InterfaceIP[] interfaces = e.getInterfaceSortie(p.getIpDest());
		PaquetIP newPaquet;
		Simulateur.LOGGER.info("Protocole IP = Envoie de " + e.getInfo());

		// Pour chaque interface de sortie
		for (InterfaceIP i : interfaces) {
			// Créer un nouveau paquet avec les adresse IP et source de
			// l'interface correspondante
			newPaquet = (PaquetIP) p.clone();
			((PaquetEthernet) newPaquet).setMacSource(i.getMac());
			try {
				// Si l'interface n'a pas d'adresse IP envoie le paquet en
				// annonyme et pour tout le monde
				if (i.getIp() == null) {
					newPaquet.setIpSource(new IPv4("0.0.0.0", 0));
					((PaquetEthernet) newPaquet).setMacDest(MAC.broadcast());
				} else {
					newPaquet.setIpSource(i.getIp());

					if (newPaquet.getIpDest().compare(
							IPv4.getGeneralBroadcast())) {
						macDest = MAC.broadcast();
					} else if (newPaquet.getMacDest() == null) {
						// Cherche l'adresse MAC dans la table ARP
						while (macDest == null) {
							if (i.getIp().estDansSousReseau(
									newPaquet.getIpDest()) > 0) {
								macDest = e.getMac(newPaquet.getIpDest());
							} else {
								macDest = e.getMac(e.getRoute(
										newPaquet.getIpDest())
										.getProchainSaut());
							}
	
							// Si trop de requête ARP sans réponse
							if (compteurReqARP >= 5){
								return;
							}
							
							// Si pas trouver attends la réponse de la requête
							// ARP
							if (macDest == null){
								compteurReqARP++;
								p.attendre();
							}
						}
					} else {
						macDest = newPaquet.getMacDest();
					}

					// Met à jour selon la MAC obtenue
					((PaquetEthernet) newPaquet).setMacDest(macDest);
				}
			} catch (IPNonValide e1) {
			}
			// Envoie à la couche inférieur
			ProtocoleEthernet.envoie(e, newPaquet);
		}
	}
}
