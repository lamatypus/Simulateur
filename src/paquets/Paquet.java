package paquets;

import elements.ElementReseau;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe abstraite représentant les paquets transitant sur le réseau
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler les paquets et de leur comportement
 * </p>
 * 
 * @author Raphaël Buache
 * @author Magali Frölich
 * @author Cédric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public abstract class Paquet implements Cloneable, Runnable {
	protected byte[] donnee;
	protected Thread thread;
	protected ElementReseau proprietaire;

	/**
	 * Créer un paquet contenant des données depuis un élément réseau
	 * 
	 * @param e: l'élément réseau
	 * @param d: les données
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
	 * Permet d'obtenir les données du paquet
	 * 
	 * @return les données
	 */
	public byte[] getDonnee() {
		return donnee;
	}

	/**
	 * Permet d'obtenir les données du paquet en format texte
	 * 
	 * @return les donnée
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
