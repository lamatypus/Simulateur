package paquets;

import elements.ElementReseau;
import protocoles.ProtocoleSTP;


/**********************************************************************
 * <p>
 * But:<br>
 * Classe qui implémente un paquet STP (BPDU) envoyé par le protocole STP.
 * </p><p>
 * Description:<br>
 * 
 * 	private int BIDsource;		BID du switch émeteur 
 *	private int BIDdestination; BID du switch de destination (prend la valeur -1 si inconnu)
 *	private int BIDroot;		BID du switch root		
 *	
 *	private int metric;			nombre de saut pour atteindre le switch root.
 *
 *	private boolean isSendByRootPort;		est-ce que l'interface émmetrice est en mode ROOT
 *	private boolean isSendByBlockingPort;	est-ce que l'interface émmétrice est en mode BLOCKING
 * 
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	14.06.2014
 ***********************************************************************/
public class PaquetSTP extends PaquetEthernet implements Cloneable {

	private int BIDsource;
	private int BIDdestination;
	private int BIDroot;
	
	private int metric;

	private boolean isSendByRootPort;
	private boolean isSendByBlockingPort;
	
	public int getBIDsource(){ return BIDsource; }
	public int getBIDdestination(){ return BIDdestination; }
	public int getBIDroot(){ return BIDroot; }
	public int getMetric(){ return metric; }
	public boolean isRootPort(){ return isSendByRootPort; }
	public boolean isBlockingPort(){ return isSendByBlockingPort; }
	
	public void setBIDsource(int bid){ BIDsource = bid; }
	public void setBIDdestination(int bid){ BIDdestination = bid; }
	public void setBIDroot(int bid){ BIDroot = bid; }
	public void setMetric(int m){ metric = m; }
	public void setRootPort(boolean root){ isSendByRootPort = root; }
	public void setBlockingPort(boolean state){ isSendByBlockingPort = state; }
	
	/**
	 * Créer un paquet STP depuis un élément réseau avec une donnée
	 * 
	 * @param e: l'élément réseau
	 * @param donnee: les données
	 */
	public PaquetSTP(ElementReseau e,byte[] donnee) {
		super(e,donnee, Type.STP);
	}

	/**
	 * Permet de dupliquer un paquet
	 */
	@Override
	public Object clone() {
		PaquetSTP p = null;
		p = (PaquetSTP) super.clone();
		return p;
	}
	
	/**
	 * Permet d'obtenir le type du paquet
	 * 
	 * @return le type
	 */
	@Override
	public String typeDePaquet() {
		return "STP";
	}
	
	/**
	 * Permet d'envoyer le paquet par le biais du protocole STP
	 */
	@Override
	public void run() {
		ProtocoleSTP.envoie(proprietaire, this);
	}
}
