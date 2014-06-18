package paquets;

import elements.ElementReseauIP;
import protocoles.ProtocoleUDP;
import standards.IPv4;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe repr�sentant les paquets UDP transitant sur le r�seau
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler les paquets UDP et de leur comportement
 * </p>
 * 
 * @author Rapha�l Buache
 * @author Magali Fr�lich
 * @author C�dric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public class PaquetUDP extends PaquetIP implements Cloneable {
	private int PortSource, PortDest;

	/**
	 * Cr�er un paquet UDP depuis un �l�ment r�seau IP avec une donn�e, une
	 * adresse IP de destination et un num�ro de port de destination
	 * 
	 * @param e: l'�l�ment r�seau IP
	 * @param donnee: les donn�es
	 * @param dest: l'adresse IP de destination
	 * @param PortDest: le num�ro de port de destination
	 */
	public PaquetUDP(ElementReseauIP e, byte[] donnee, IPv4 dest, int PortDest) {
		super(e, donnee, dest, Protocole.UDP);
		this.PortDest = PortDest;
	}

	/**
	 * Permet de configurer le port source
	 * 
	 * @param p: le num�ro de port
	 */
	public void setPortSource(int p) {
		PortSource = p;
	}

	/**
	 * Permet de configurer le port de destination
	 * 
	 * @param p: le num�ro de port
	 */
	public void setPortDest(int p) {
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
	 * Permet de dupliquer un paquet
	 */
	@Override
	public Object clone() {
		PaquetUDP p = null;
		p = (PaquetUDP) super.clone();
		return p;
	}

	/**
	 * Permet d'obtenir le type du paquet
	 * 
	 * @return le type
	 */
	@Override
	public String typeDePaquet() {
		return "UDP";
	}

	/**
	 * Permet d'envoyer le paquet par le biais du protocole UDP
	 */
	@Override
	public void run() {
		ProtocoleUDP.envoie((ElementReseauIP) proprietaire, this);
	}
}
