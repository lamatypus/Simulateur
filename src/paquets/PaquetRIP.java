package paquets;

import application.PortUDP;
import protocoles.ProtocoleRIP;
import standards.IPv4;
import elements.ElementReseauIP;
import elements.util.TableRoutage;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe repr�sentant les paquets RIP transitant sur le r�seau
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler les paquets RIP et de leur comportement
 * </p>
 * 
 * @author Rapha�l Buache
 * @author Magali Fr�lich
 * @author C�dric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public class PaquetRIP extends PaquetUDP implements Cloneable {
	private TableRoutage tableRoutage;

	/**
	 * Cr�er un paquet RIP depuis un �l�ment r�seau IP avec une donn�e et une
	 * table de routage
	 * 
	 * @param e: l'�l�ment r�seau IP
	 * @param donnee: les donn�es
	 * @param tableRoutage: la table de routage
	 */
	public PaquetRIP(ElementReseauIP e, byte[] donnee, TableRoutage tableRoutage) {
		super(e, donnee, IPv4.getGeneralBroadcast(), PortUDP.RIP.port());
		this.tableRoutage = tableRoutage;
		this.setIpDest(IPv4.getGeneralBroadcast());
	}

	/**
	 * Permet de dupliquer un paquet
	 */
	@Override
	public Object clone() {
		PaquetRIP p = null;
		p = (PaquetRIP) super.clone();
		return p;
	}

	/**
	 * Permet d'obtenir le type du paquet
	 * 
	 * @return le type
	 */
	@Override
	public String typeDePaquet() {
		return "RIP";
	}

	/**
	 * Permet d'envoyer le paquet par le biais du protocole RIP
	 */
	@Override
	public void run() {
		ProtocoleRIP.envoie((ElementReseauIP) proprietaire, this);
	}

	/**
	 * Permet d'obtenir la table de routage du paquet
	 * 
	 * @return la table de routage
	 */
	public TableRoutage getTableRoutage() {
		return tableRoutage;
	}
}
