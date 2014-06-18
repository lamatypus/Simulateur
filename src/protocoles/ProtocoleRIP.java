package protocoles;

import controleur.Simulateur;
import application.PortUDP;
import paquets.Paquet;
import paquets.PaquetRIP;
import elements.ElementReseauIP;
import elements.util.TableRoutage;
import elements.util.TableRoutage.Route;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe représentant le protocole RIP
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler le comportement du protocole RIP 
 * permettant de faire du routage dynamique sur les routeurs
 * </p>
 * 
 * @author Raphaël Buache
 * @author Magali Frölich
 * @author Cédric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public class ProtocoleRIP {

	/**
	 * Permet de savoir si le paquet est pour ce protocole
	 * 
	 * @param p: le paquet
	 * @return true si le paquet est pour ce protocole
	 */
	public static boolean monPaquet(Paquet p) {
		return PaquetRIP.class.equals(p.getClass());
	}

	/**
	 * Permet de recevoir de recevoir un paquet RIP sur un élément réseau IP
	 * 
	 * @param e: l'élément réseau IP
	 * @param p: le paquet RIP
	 */
	public static void recoit(ElementReseauIP e, PaquetRIP p) {
		Simulateur.LOGGER.info("Protocole RIP = Recoit de " + e.getInfo());

		TableRoutage tableRoutage = e.getTableRoutage();
		TableRoutage tableVoisin = p.getTableRoutage();
		Route existante;

		for (Route route : tableVoisin.getRoutes()) {
			if ((existante = tableRoutage.contient(route.getIp())) != null) {
				if ((existante.getCout() > route.getCout())) {
					// e.nouvelleRoute(route.getIp(), route.getInterfaceIP(),
					// route.getProchainSaut(), Methode.RIP, route.getCout()+1);
				}
			}
		}
	}

	/**
	 * Permet d'envoyer un paquet RIP depuis un élément réseau IP en passant
	 * par le protocole UDP
	 * 
	 * @param e: l'élément réseau IP
	 * @param p: le paquet RIP
	 */
	public static void envoie(ElementReseauIP e, PaquetRIP p) {
		Simulateur.LOGGER.info("Protocole RIP = Envoie de " + e.getInfo());
		// Appel couche du dessous
		e.getSocketUDP()[PortUDP.RIP.port()].sendToInterface(p);
	}
}
