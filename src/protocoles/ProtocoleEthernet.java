package protocoles;

import controleur.Simulateur;
import paquets.*;
import elements.ElementReseau;
import elements.ElementReseau.Interface;
import elements.ElementReseauIP;
import elements.ElementReseauIP.InterfaceIP;
import elements.Switch;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe représentant le protocole Ethernet
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler le comportement du protocole Ethernet en
 * faisant transiter des information d'une interface à une autre
 * </p>
 * 
 * @author Raphaël Buache
 * @author Magali Frölich
 * @author Cédric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public class ProtocoleEthernet {

	/**
	 * Permet de savoir si le paquet est pour ce protocole
	 * 
	 * @param p: le paquet
	 * @return true si le paquet est pour ce protocole
	 */
	public static boolean monPaquet(Paquet p) {
		return PaquetEthernet.class.equals(p.getClass());
	}

	/**
	 * Permet d'envoyer un paquet Ethernet d'une interface à son interface de
	 * destination
	 * 
	 * @param e: l'élément réseau
	 * @param p: le paquet ethernet
	 */
	public static void envoie(ElementReseau e, PaquetEthernet p) {
		Simulateur.LOGGER.info("Protocole Ethernet = Envoie de " + e.getInfo());

		/*
		 * Bloque les paquets si l'interface est bloquée par le protocole STP.
		 * Sauf pour les paquets du protocole STP.
		 */
		if (e.getClass() == Switch.class && !ProtocoleSTP.monPaquet(p)) {

			if (!((Switch) e).getTableSTP().isActiveBySTP(
					e.getInterface(p.getMacSource()))) {
				return;
			}
		}

		// Envoie le paquet sur l'interface de sortie correspondante
		if (((PaquetEthernet) p).getMacSource() != null && 
				((PaquetEthernet) p).getMacDest() != null) {
			Interface i = e.getInterface(((PaquetEthernet) p).getMacSource());
			if (i.isActive()){
				i.envoyer(p);
			}
		}

	}

	/**
	 * Permet de recevoir un paquet Ethernet sur une interface
	 * 
	 * @param i: l'interface
	 * @param e: l'élément réseau
	 * @param p: le paquet ethernet
	 */
	public static void recoit(Interface i, ElementReseau e, PaquetEthernet p) {
		Simulateur.LOGGER
				.info("Protocole Ethernet = Recoit sur " + e.getInfo());

		// Si le paquet est pour cet élément
		if (((PaquetEthernet) p).getMacDest() != null && 
				(((PaquetEthernet) p).getMacDest().equals(i.getMac()) || 
					((PaquetEthernet) p).getMacDest().isBroadcast())) {

			// Si paquet STP
			if (p.getType() == PaquetEthernet.Type.STP) {
				ProtocoleSTP.recoit(e, (PaquetSTP) p, i);
			}
			// Si paquet IP
			else if (e instanceof ElementReseauIP) {
				((ElementReseauIP) e).recoitCoucheReseau(p, (InterfaceIP) i);
			}
		}
	}
}
