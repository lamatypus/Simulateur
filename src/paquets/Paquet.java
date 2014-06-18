package paquets;

import elements.ElementReseau;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe abstraite repr�sentant les paquets transitant sur le r�seau
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler les paquets et de leur comportement
 * </p>
 * 
 * @author Rapha�l Buache
 * @author Magali Fr�lich
 * @author C�dric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public abstract class Paquet implements Cloneable, Runnable {
	protected byte[] donnee;
	protected Thread thread;
	protected ElementReseau proprietaire;

	/**
	 * Cr�er un paquet contenant des donn�es depuis un �l�ment r�seau
	 * 
	 * @param e: l'�l�ment r�seau
	 * @param d: les donn�es
	 */
	public Paquet(ElementReseau e, byte[] d) {
		proprietaire = e;
		donnee = d;
		thread = new Thread(this);
	}

	/**
	 * Permet d'envoyer le paquet
	 */
	public void envoie() {
		thread.start();
	}

	/**
	 * Permet d'obtenir les donn�es du paquet
	 * 
	 * @return les donn�es
	 */
	public byte[] getDonnee() {
		return donnee;
	}

	/**
	 * Permet d'obtenir les donn�es du paquet en format texte
	 * 
	 * @return les donn�e
	 */
	public String getString() {
		return String.valueOf(donnee);
	}

	/**
	 * Permet de dupliquer un paquet
	 */
	@Override
	public Object clone() {
		Paquet p = null;
		try {
			p = (Paquet) super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return p;
	}

	/**
	 * Permet de faire attendre un paquet
	 */
	public void attendre() {
		proprietaire.waitPaquet();
	}

	/**
	 * Permet d'obtenir le type du paquet
	 * 
	 * @return le type
	 */
	public abstract String typeDePaquet();

	/**
	 * Permet d'obtenir des infos sur le paquet
	 * 
	 * @return les infos
	 */
	public abstract String info();

}
