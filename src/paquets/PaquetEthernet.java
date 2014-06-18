package paquets;

import protocoles.ProtocoleEthernet;
import elements.ElementReseau;
import standards.*;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe repr�sentant les paquets ethernet transitant sur le r�seau
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler les paquets ethernet et de leur comportement
 * </p>
 * 
 * @author Rapha�l Buache
 * @author Magali Fr�lich
 * @author C�dric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public class PaquetEthernet extends Paquet implements Cloneable {
	private MAC macSource;
	private MAC macDest;
	private Type type;

	/**
	 * Cr�er un paquet ethernet depuis un �l�ment r�seau avec une donn�e et un
	 * type correspondant au protocole de la couche sup�rieure
	 * 
	 * @param e: l'�l�ment r�seau
	 * @param donnee: les donn�es
	 * @param t: le type
	 */
	public PaquetEthernet(ElementReseau e, byte[] donnee, Type t) {
		super(e, donnee);
		macSource = null;
		macDest = null;
		type = t;
	}

	/**
	 * Cr�er un paquet ethernet depuis un �l�ment r�seau avec une donn�e, un
	 * type correspondant au protocole de la couche sup�rieure, une adresse MAC
	 * source et destination
	 * 
	 * @param e: l'�l�ment r�seau
	 * @param donnee: les donn�es
	 * @param t: le type
	 * @param source: l'adresse MAC source
	 * @param dest: l'adresse MAC de destination
	 */
	public PaquetEthernet(ElementReseau e, byte[] donnee, Type t, MAC source,
			MAC dest) {
		super(e, donnee);
		macSource = source;
		macDest = dest;
		type = t;
	}

	/**
	 * Permet d'obtenir l'adresse MAC source
	 * 
	 * @return la MAC source
	 */
	public MAC getMacSource() {
		return macSource;
	}

	/**
	 * Permet d'obtenir l'adresse MAC de destination
	 * 
	 * @return la MAC de destination
	 */
	public MAC getMacDest() {
		return macDest;
	}

	/**
	 * Permet d'ajouter l'adresse MAC source
	 * 
	 * @param source: l'adresse MAC source
	 */
	public void setMacSource(MAC source) {
		macSource = source;
	}

	/**
	 * Permet d'ajouter l'adresse MAC de destination
	 * 
	 * @param dest: l'adresse MAC de destination
	 */
	public void setMacDest(MAC dest) {
		macDest = dest;
	}

	/**
	 * Permet de configurer le type correspondant au protocole de la couche
	 * sup�rieure
	 * 
	 * @param t: le type
	 */
	public void setType(Type t) {
		type = t;
	}

	/**
	 * Permet d'obtenir le type correspondant au protocole de la couche
	 * sup�rieure
	 * 
	 * @return le type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Permet de dupliquer un paquet
	 */
	@Override
	public Object clone() {
		PaquetEthernet p = null;
		p = (PaquetEthernet) super.clone();
		return p;
	}


	/**
	 * Permet d'obtenir le type du paquet
	 * 
	 * @return le type
	 */
	@Override
	public String typeDePaquet() {
		return "Ethernet";
	}

	/**
	 * Permet d'obtenir des infos sur le paquet
	 * 
	 * @return les infos
	 */
	@Override
	public String info() {
		return String.valueOf(super.getDonnee());
	}

	/**
	 * Type correspondant au protocole de la couche sup�rieure
	 *
	 */
	public enum Type {
		IPv4(0x800),
		ARP(0x806),
		IPv6(0x86DD), 
		STP(0x42); // Non conforme car normalement dans LLC avec 802.3

		private int valeur;

		private Type(int valeur) {
			this.valeur = valeur;
		}

		public int valeur() {
			return valeur;
		}

	}

	/**
	 * Permet d'envoyer le paquet par le biais du protocole ethernet
	 */
	@Override
	public void run() {
		ProtocoleEthernet.envoie(proprietaire, this);
	}

}
