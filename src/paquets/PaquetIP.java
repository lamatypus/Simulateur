package paquets;

import protocoles.ProtocoleIP;
import elements.ElementReseauIP;
import standards.IPv4;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe représentant les paquets IP transitant sur le réseau
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler les paquets IP et de leur comportement
 * </p>
 * 
 * @author Raphaël Buache
 * @author Magali Frölich
 * @author Cédric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public class PaquetIP extends PaquetEthernet implements Cloneable {
	private IPv4 ipSource, ipDest;
	private Protocole protocole;
	
	/**
	 * Créer un paquet IP depuis un élément réseau IP avec une donnée,  une
	 * adresse IP de destination et un protocole de la couche supérieure
	 * 
	 * @param e: l'élément réseau IP
	 * @param d: les données
	 * @param dest: l'adresse IP de destination
	 * @param p: le protocole
	 */
	public PaquetIP(ElementReseauIP e,byte[] d, IPv4 dest,Protocole p){
		super(e,d,PaquetEthernet.Type.IPv4);
		ipDest = dest;
		protocole = p;
	}
	
	/**
	 * Permet d'obtenir l'adresse IP source
	 * 
	 * @return l'IP source
	 */
	public IPv4 getIpSource(){
		return ipSource;
	}
	
	/**
	 * Permet d'obtenir l'adresse IP de destination
	 * 
	 * @return l'IP de destination
	 */
	public IPv4 getIpDest(){
		return ipDest;
	}
	
	/**
	 * Permet de configurer l'adresse IP source
	 * 
	 * @param s: l'adresse IP source
	 */
	public void setIpSource(IPv4 s){
		ipSource = s;
	}
	
	/**
	 * Permet de configurer l'adresse IP de destination
	 * 
	 * @param d: l'adresse IP de destination
	 */
	public void setIpDest(IPv4 d){
		ipDest = d;
	}
	
	/**
	 * Permet de configurer le protocole de la couche supérieure
	 * 
	 * @param p: le protocole
	 */
	public void setProtocole(Protocole p){
		protocole = p;
	}
	
	/**
	 * Permet d'obtenir le protocole de la couche supérieure
	 * 
	 * @return le protocole
	 */
	public Protocole getProtocole(){
		return protocole;
	}

	/**
	 * Permet d'obtenir le type du paquet
	 * 
	 * @return le type
	 */
	@Override
	public String typeDePaquet() {
		return "IP";
	}
	
	/**
	 * Permet de dupliquer un paquet
	 */
	@Override
	public Object clone() {
		PaquetIP p = null;
		p = (PaquetIP) super.clone();
		return p;
	}
	
	/**
	 * Permet de représenter le protocole de la couche supérieure
	 *
	 */
	public enum Protocole {
		ICMP (0x1),
		TCP (0x6),
		UDP (0x11);
		
		private int valeur;
		
		private Protocole (int valeur){
			this.valeur = valeur;
		}
		
		public int valeur (){
			return valeur;
		}

	}

	/**
	 * Permet d'envoyer le paquet par le biais du protocole IP
	 */
	@Override
	public void run() {
		ProtocoleIP.envoie((ElementReseauIP)proprietaire, this);
	}
	
}
