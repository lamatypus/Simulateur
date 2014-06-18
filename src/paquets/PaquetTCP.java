package paquets;

import elements.ElementReseauIP;
import protocoles.ProtocoleTCP;
import standards.IPv4;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe représentant les paquets TCP transitant sur le réseau
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler les paquets TCP et de leur comportement
 * </p>
 * 
 * @author Raphaël Buache
 * @author Magali Frölich
 * @author Cédric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public class PaquetTCP extends PaquetIP implements Cloneable {
	private int PortSource, PortDest;
	private Sync sync;

	/**
	 * Créer un paquet TCP depuis un élément réseau IP avec une donnée, une
	 * adresse IP de destination, un numéro de port source et de destination
	 * 
	 * @param e: l'élément réseau IP
	 * @param donnee: les données
	 * @param dest: l'adresse IP de destination
	 * @param PortDest: le numéro de port de destination
	 * @param PortSource: le numéro de port source
	 */
	public PaquetTCP(ElementReseauIP e, byte[] donnee, IPv4 dest, int PortDest,
			int PortSource) {
		super(e, donnee, dest, Protocole.TCP);
		this.PortDest = PortDest;
		this.PortSource = PortSource;
		this.sync = Sync.NULL;
	}

	/**
	 * Permet de définir le port source
	 * 
	 * @param p: le numéro de port
	 */
	public void setPortSource(short p) {
		PortSource = p;
	}

	/**
	 * Permet de définir le port de destination
	 * 
	 * @param p: le numéro de port
	 */
	public void setPortDest(short p) {
		PortDest = p;
	}

	/**
	 * Permet d'obtenir le port source
	 * 
	 * @return le port source
	 */
	public int getPortSource() {
		return PortSource;
	}

	/**
	 * Permet d'obtenir le port de destination
	 * 
	 * @return le port de destination
	 */
	public int getPortDest() {
		return PortDest;
	}

	/**
	 * Permet d'obtenir l'étape de la connexion
	 * 
	 * @return l'étape
	 */
	public Sync getSync() {
		return sync;
	}

	/**
	 * Permet de configurer l'étape de la connexion
	 * 
	 * @param s: l'étape de synchro
	 */
	public void setSync(Sync s) {
		sync = s;
	}

	/**
	 * Permet de dupliquer un paquet
	 */
	@Override
	public Object clone() {
		PaquetTCP p = null;
		p = (PaquetTCP) super.clone();
		return p;
	}

	/**
	 * Permet d'obtenir le type du paquet
	 * 
	 * @return le type
	 */
	@Override
	public String typeDePaquet() {
		return "TCP";
	}

	/**
	 * Permet d'envoyer le paquet par le biais du protocole TCP
	 */
	@Override
	public void run() {
		ProtocoleTCP.envoie((ElementReseauIP) proprietaire, this);
	}

	/**
	 * Etape de la connexion TCP
	 * 
	 */
	public enum Sync {
		NULL, SYN, SYN_ACK, ACK, FIN_ACK, FIN;
	}
}
