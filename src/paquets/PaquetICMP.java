package paquets;

import elements.ElementReseauIP;
import protocoles.ProtocoleICMP;
import standards.IPv4;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe repr�sentant les paquets ICMP transitant sur le r�seau
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler les paquets ICMP et de leur comportement
 * </p>
 * 
 * @author Rapha�l Buache
 * @author Magali Fr�lich
 * @author C�dric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public class PaquetICMP extends PaquetIP implements Cloneable {

	/**
	 * Cr�er un paquet ICMP depuis un �l�ment r�seau IP avec une donn�e et une
	 * adresse IP de destination
	 * 
	 * @param e: l'�l�ment r�seau IP
	 * @param donnee: les donn�es
	 * @param dest: l'adresse IP de destination
	 */
	public PaquetICMP(ElementReseauIP e,byte[] donnee, IPv4 dest) {
		super(e,donnee,dest,Protocole.ICMP);
		setIpDest(dest);
	}

	/**
	 * Permet de dupliquer un paquet
	 */
	@Override
	public Object clone() {
		PaquetICMP p = null;
		p = (PaquetICMP) super.clone();
		return p;
	}
	
	/**
	 * Permet d'obtenir le type du paquet
	 * 
	 * @return le type
	 */
	@Override
	public String typeDePaquet() {
		return "ICMP";
	}
	
	/**
	 * Permet d'envoyer le paquet par le biais du protocole ICMP
	 */
	@Override
	public void run() {
		ProtocoleICMP.envoie((ElementReseauIP)proprietaire, this);
	}
}
