package paquets;

import elements.ElementReseauIP;
import protocoles.ProtocoleTCP;
import standards.IPv4;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe repr�sentant les paquets TCP transitant sur le r�seau
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler les paquets TCP et de leur comportement
 * </p>
 * 
 * @author Rapha�l Buache
 * @author Magali Fr�lich
 * @author C�dric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public class PaquetTCP extends PaquetIP implements Cloneable {
	private int PortSource, PortDest;
	private Sync sync;

	/**
	 * Cr�er un paquet TCP depuis un �l�ment r�seau IP avec une donn�e, une
	 * adresse IP de destination, un num�ro de port source et de destination
	 * 
	 * @param e: l'�l�ment r�seau IP
	 * @param donnee: les donn�es
	 * @param dest: l'adresse IP de destination
	 * @param PortDest: le num�ro de port de destination
	 * @param PortSource: le num�ro de port source
	 */
	public PaquetTCP(ElementReseauIP e, byte[] donnee, IPv4 dest, int PortDest,
			int PortSource) {
		super(e, donnee, dest, Protocole.TCP);
		this.PortDest = PortDest;
		this.PortSource = PortSource;
		this.sync = Sync.NULL;
	}

	/**
	 * Permet de d�finir le port source
	 * 
	 * @param p: le num�ro de port
	 */
	public void setPortSource(short p) {
		PortSource = p;
	}

	/**
	 * Permet de d�finir le port de destination
	 * 
	 * @param p: le num�ro de port
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
	 * Permet d'obtenir l'�tape de la connexion
	 * 
	 * @return l'�tape
	 */
	public Sync getSync() {
		return sync;
	}

	/**
	 * Permet de configurer l'�tape de la connexion
	 * 
	 * @param s: l'�tape de synchro
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
