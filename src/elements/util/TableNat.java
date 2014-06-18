package elements.util;

import java.io.Serializable;
import java.util.LinkedList;

import standards.IPv4;

/**********************************************************************
 * <p>
 * But:<br>
 * Repr�sente une table NAT.
 * </p><p>
 * Description:<br>
 * La table NAT est utilis�e dans les connexion TCP c�t� serveur.
 * Lorsqu'un nouveau socket serveur est cr��, le serveur �coute les connexions
 * entrantes. Lorsqu'un client arrive, un nouveau socket est cr�� et les
 * communications suivantes se font au travers du NAT pour lib�rer le port
 * de connexion de base.
 * </p>
 *
 * @author		Rapha�l Buache
 * @author     	Magali Fr�lich
 * @author     	C�dric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public class TableNat implements Serializable{

	private static final long serialVersionUID = 1L;
	private LinkedList<Entry> table = new LinkedList<Entry>();
	
	/**
	 * Ajoute une nouvelle entr�e � la table
	 * 
	 * @param ad adresse IP
	 * @param i	 port initial
	 * @param m	 port de redirection
	 */
	public void nouvelleEntree(IPv4 ad,int i,int m){
		
		for(Entry e : table){
			if(e.adresse == ad && e.portInitial == i){
				return;
			}
		}
		
		table.add(new Entry(ad,i,m));
	}
	
	/**Supprime une entr�e de la table
	 * 
	 * @param ad adresse IP
	 * @param i	 port initial
	 */
	public void supprimeEntree(IPv4 ad,int i){
		for(Entry e : table){
			if(e.adresse == ad && e.portInitial == i){
				table.remove(e);
				return;
			}
		}
	}
	
	/**
	 * Retourne le port de communication selon la table
	 * 
	 * @param ad adresseIP
	 * @param i	 port initial
	 * @return	le port de redirection si entr�e pr�sente, sinon le port initial.
	 */
	public int getPortMachine(IPv4 ad,int i){
		for(Entry e : table){
			if(e.adresse == ad && e.portInitial == i){
				return e.portMachine;
			}
		}
		return i;
	}
	
	/**
	 * 
	 * Repr�sente une entr�e de la table NAT
	 *
	 */
	private class Entry implements Serializable{

		private static final long serialVersionUID = 1L;
		IPv4 adresse;
		int portInitial;
		int portMachine;
		
		/**Cr�e l'entr�e
		 * 
		 * @param ad	adresse ip
		 * @param i		port initial
		 * @param m		port redirect
		 */
		public Entry(IPv4 ad,int i,int m){
			adresse = ad;
			portInitial = i;
			portMachine = m;
		}
		
		
	}
}
