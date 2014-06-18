package elements.util;

import java.io.Serializable;
import java.util.LinkedList;

import standards.IPv4;

/**********************************************************************
 * <p>
 * But:<br>
 * Représente une table NAT.
 * </p><p>
 * Description:<br>
 * La table NAT est utilisée dans les connexion TCP côté serveur.
 * Lorsqu'un nouveau socket serveur est créé, le serveur écoute les connexions
 * entrantes. Lorsqu'un client arrive, un nouveau socket est créé et les
 * communications suivantes se font au travers du NAT pour libérer le port
 * de connexion de base.
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public class TableNat implements Serializable{

	private static final long serialVersionUID = 1L;
	private LinkedList<Entry> table = new LinkedList<Entry>();
	
	/**
	 * Ajoute une nouvelle entrée à la table
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
	
	/**Supprime une entrée de la table
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
	 * @return	le port de redirection si entrée présente, sinon le port initial.
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
	 * Représente une entrée de la table NAT
	 *
	 */
	private class Entry implements Serializable{

		private static final long serialVersionUID = 1L;
		IPv4 adresse;
		int portInitial;
		int portMachine;
		
		/**Crée l'entrée
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
