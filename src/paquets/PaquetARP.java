package paquets;

import elements.ElementReseau;
import elements.ElementReseauIP;
import protocoles.ProtocoleARP;
import standards.MAC;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe repr�sentant les paquets ARP transitant sur le r�seau
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler les paquets ARP et de leur comportement
 * </p>
 * 
 * @author Rapha�l Buache
 * @author Magali Fr�lich
 * @author C�dric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public class PaquetARP extends PaquetEthernet implements Cloneable {

	/**
	 * Cr�er un paquet ARP depuis un �l�ment r�seau avec une donn�e, une adresse
	 * MAC source et une de destination
	 * 
	 * @param e: l'�l�ment r�seau
	 * @param donnee: les donn�es
	 * @param source: l'adresse MAC source
	 * @param dest: l'adresse MAC de destination
	 */
	public PaquetARP(ElementReseau e, byte[] donnee, MAC source, MAC dest) {
		super(e, donnee, PaquetEthernet.Type.ARP, source, dest);
	}

	/**
	 * Permet de dupliquer un paquet
	 */
	@Override
	public Object clone() {
		PaquetARP p = null;
		p = (PaquetARP) super.clone();
		return p;
	}

	/**
	 * Permet d'obtenir le type du paquet
	 * 
	 * @return le type
	 */
	@Override
	public String typeDePaquet() {
		return "ARP";
	}

	/**
	 * Permet d'envoyer le paquet par le biais du protocole ARP
	 */
	@Override
	public void run() {
		ProtocoleARP.envoie((ElementReseauIP) proprietaire, this);
	}

	/**
	 * 
	 * Permet de d�finir si le paquet est de type requ�te ou r�ponse
	 * 
	 */
	public enum Style {
		Requete, Reponse;
	}
}
