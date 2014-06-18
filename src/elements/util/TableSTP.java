package elements.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import protocoles.ProtocoleSTP;
import elements.ElementReseau;
import elements.ElementReseau.Interface;
import elements.Switch;


/**********************************************************************
 * <p>
 * But:<br>
 * Classe qui implémente la table STP, modifiée par le procotol STP.
 * </p><p>
 * Description:<br>
 * Cette table permet de sauver un noeud de l'arbre généré par le protocole STP.
 * Cela permet de supprimer les boucles générés par les éléments Switch.
 * 
 * La table STP envoie toutes les 2 secondes un message hello. 
 * Ce message est un broadcast qui est traité par les autres switchs pour 
 * mettre à jour l'arbre.
 * 
 * Toutes les 30 secondes la table STP efface sa configuration.
 * 
 * Attribut de la table STP :
 * -------------------------------------------------------------------
 * private ElementReseau.Interface[] interfaceElement:
 * La table STP possède une référence vers la liste des interfaces de son switch.
 * 
 * private State[] stateInterface:
 * Les interfaces du switch ont plusieurs états. Cet état est sauvé dans 
 * le tableau stateInterface.
 * 
 * private boolean[] isInterfaceSTPactif:
 * Les interfaces du switch peuvent être connectées avec d'autres switch
 * dans ce cas isInterfaceSTPactif est à true pour l'interface du switch 
 * correspondante.
 * 
 * private int[] bidDestination:
 * Dans ce cas, l'interface connaîtra également le bid du switch auquel elle est 
 * connectée, le tableau bidDestination gère cela.
 * 
 * 	private int BID = -1 		: BID du switch (unique) 
 * 	private int rootBID = -1 	: BID root 
 *	private int sourceBID = -1  : BID qui annoncé le dernier root
 *	private int metric = 0 		: Nombre de saut pour arriver au root
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
public class TableSTP implements Runnable,Serializable {
	
	private static final long serialVersionUID = 1L;
	Switch element;
	transient Thread helloTime;
	
	private int BID = -1;
	private int rootBID = -1;
	private int sourceBID = -1;
	private int metric = 0;
	
	private int rootPort = -1;
	
	private ElementReseau.Interface[] interfaceElement;
	
	private State[] stateInterface;
	private State[] stateInterface_copy;
	boolean first_copy = true;
	
	private boolean[] isInterfaceSTPactif;
	private int[] bidDestination;
	
	private boolean running;
	
	
	/**
	 * Les interfaces STP peuvent être dans 5 états : BLOCKING, DESIGNATED, ROOT, 
	 * 												  LEARNING, BACKUP.
	 * 
	 * L'état root indique que l'interface est connectée à un switch ROOT où que 
	 * c'est cette interface qui doit être utilisée pour atteindre le switch ROOT.
	 * 
	 * Il ne devrait y avoir qu'un switch ROOT par sous-réseau et chaque switch
	 * ne devrait avoir qu'une interface root. Définir par la variable rootPort.
	 * 
	 * Le switch ROOT n'a pas d'interface root. 
	 * 
	 * L'état DESIGNATED indique à l'interface que l'interface de destination 
	 * est une interface ROOT. Chaque switch devrait avoir une interface DESIGNATED. 
	 * Plusieurs dans le cas du switch ROOT.
	 * 
	 * Exemple : switch_ROOT [DESIGNATED] <-> [ROOT] switch_1 [DESIGNATED] <-> [ROOT] switch_2
	 * 
	 * L'état LEARNING indique que l'interface n'est pas connecté où qu'elle n'est pas connecté  
	 * à un switch.
	 * 
	 * L'état BACKUP indique une double liaison. 
	 * C'est-à-dire que 2 liens directes branchent 2 switchs.
	 * 
	 */
	enum State {
		BACKUP(),
		ROOTPORT(),
		DESIGNATED(),
		BLOCKING(),
		LEARNING();
	}
	
	public TableSTP(Switch e) { 
		element = e;
		calculerBID(e.getInterface());
		running = true;
		helloTime = new Thread(this);
		helloTime.start();
	}
	
	/**
	 * Cette métghode permet de définir le BID du switch.
	 * Elle ne doit être appellée qu'une seule fois !
	 * 
	 * La définition de l'arbre STP se repose sur les BID des switchs. 
	 * Un nouveau BID correspond à un nouveau switch.
	 * 
	 * Pour définir un numéro unique, le bid est calculé avec la méthode hashCode
	 * sur la référence des adresses MAC.
	 * 
	 * 
	 * Le BID root est initialisé avec le BID du switch
	 */
	public void calculerBID(ElementReseau.Interface[] list) { 
		
		isInterfaceSTPactif = new boolean[list.length];
		
		interfaceElement = list;
		stateInterface = new State[list.length];
		stateInterface_copy = new State[list.length];
		
		bidDestination = new int[list.length];
		
		for(ElementReseau.Interface i : list) {
			isInterfaceSTPactif[i.getNumero()] = false;
			stateInterface[i.getNumero()] = State.LEARNING;
			
			bidDestination[i.getNumero()] = -1;
			
			if(BID == -1) {
				BID = i.getMac().hashCode();
			
			} else if(BID > i.getMac().hashCode()) {
				BID = i.getMac().hashCode();
			}
			
			i.setColor(false);
			
		}
		
		rootBID = BID;
		sourceBID = BID;
	}
	
	public int getBid() { return BID; }
	
	public void setRootBID(int bid) { rootBID = bid; }
	public int getRootBID() { return rootBID; }
	
	public boolean isRoot() { return BID == rootBID; }
	
	public int getMetric(){return metric;}
	public void setMetric(int m) {metric = m;}
	
	public int getSourceBID(){return sourceBID;}
	public void setSourceBID(int source) {sourceBID = source;}
	
	public void setActiveInterfaceSTP(ElementReseau.Interface i, boolean isActive) { isInterfaceSTPactif[i.getNumero()] = isActive; }
	public boolean isInterfaceSTPActive(ElementReseau.Interface i) { return isInterfaceSTPactif[i.getNumero()]; }
	public void setActiveAllInterface(boolean isActive) {
		for(Interface inter : interfaceElement) { 
				isInterfaceSTPactif[inter.getNumero()] = isActive;
			}
	}
	
	public void setBIDdestination(ElementReseau.Interface i, int bid) { bidDestination[i.getNumero()] = bid; }
	public int getBIDdestination(ElementReseau.Interface i) { return bidDestination[i.getNumero()]; }

	
	/**
	 * Définir une interface en ROOT.
	 * 
	 * Il ne peut y avoir qu'une seule interface ROOT par switch.
	 * Si une interface est déjà en état ROOT alors elle passe en état LEARNING.
	 * 
	 * Puis l'interface passée en paramètre prend l'état ROOT.
	 */
	public void setRootPort(ElementReseau.Interface i) { 
		
		if(rootPort != -1) {
			stateInterface[rootPort] = State.LEARNING;
		}
		
		stateInterface[i.getNumero()] = State.ROOTPORT;
		rootPort = i.getNumero();
	}
	
	public ElementReseau.Interface getRootPort() {
		if(rootPort == -1)
			return null;
		else
			return interfaceElement[rootPort];
	}
	
	public int getRootPortID() {
		return rootPort;
	}
	
	public void stop()
	{
		running = false;
	}
	
	/**
	 * Définir les interfaces en BACKUP.
	 * 
	 * Il peut y avoir plusieurs interfaces BACKUP par switch.
	 * 
	 * Si plusieurs interfaces sont connectées avec le même switch, il faut que 1 ou plusieurs interfaces soit
	 * en états BACKUP. Le but étant de n'avoir qu'un seul lien vers cahque switch.
	 * 
	 * Il est possible de contrôler les liens avec le tableau qui enregistre les bid de destinations.
	 * 
	 * Pour chaque interface dont le BID de destination est définit :
	 * 	
	 * 		libérer l'interface si elle était en mode BACKUP.
	 * 		Mettre toutes les interfaces connectés aux mêmes switch en mode BACKUP.
	 * 
	 * Pour chaque interface, libérer une seule interface par groupe de connexion vers le même switch.
	 * 
	 */
	public boolean setBackupPort(ElementReseau.Interface i) { 
		
		for(Interface inter : interfaceElement) {
			
			if(bidDestination[inter.getNumero()] == -1)
				continue;
			
			//Libérer éventuellement le port
			if(stateInterface[inter.getNumero()] == State.BACKUP)
				stateInterface[inter.getNumero()] = State.LEARNING;
			
			for(Interface checkBackup: interfaceElement) {
				
				if(bidDestination[checkBackup.getNumero()] == -1)
					continue;
				
				if(inter.getNumero() == checkBackup.getNumero())
					continue;
				
				//Chercher un doublon
				if(bidDestination[inter.getNumero()] == bidDestination[checkBackup.getNumero()]) {
					stateInterface[inter.getNumero()] = State.BACKUP;
				}
			}
			
		}
		
		int bidBackup = -1;
		
		for(Interface inter : interfaceElement) {
			
			if(stateInterface[inter.getNumero()] != State.BACKUP) {
				continue;
			}
			
			if(bidBackup == -1 || bidBackup != bidDestination[inter.getNumero()]) {
				stateInterface[inter.getNumero()] = State.LEARNING;
				bidBackup = bidDestination[inter.getNumero()];
			}
		}
		
		return stateInterface[i.getNumero()] == State.BACKUP;
	}
	
	public void setDesignatedPort(ElementReseau.Interface i) { 
		stateInterface[i.getNumero()] = State.DESIGNATED;
	}
	
	public void setBlockingPort(ElementReseau.Interface i) {
		stateInterface[i.getNumero()] = State.BLOCKING;
	}
	
	public State getStatePort(ElementReseau.Interface i) {
		return stateInterface[i.getNumero()];
	}
	
	/**
	 * Cette méthode bloque les ports DESIGNATED supplémentaires.
	 * La table STP ne peut avoir qu'une seul interface ROOT. 
	 * 
	 * L'autre interface est automatiquement mis en LEARNING.
	 * Si cette interface est connectée à un switch alors la table la bloque.
	 * 
	 */
	public void checkBlockPort() {
		
		for(Interface inter : interfaceElement) {
			
			if(stateInterface[inter.getNumero()] == State.LEARNING && isInterfaceSTPactif[inter.getNumero()]) {
				stateInterface[inter.getNumero()] = State.BLOCKING;
				inter.setColor(false);				
			}
			else
				inter.setColor(true);
		}
		
		setActiveAllInterface(false);
		
		if(first_copy) {
			
			for(Interface inter : interfaceElement)
				stateInterface_copy[inter.getNumero()] = stateInterface[inter.getNumero()];
		}
	}
	
	public boolean isActiveBySTP(ElementReseau.Interface i) {
		
		boolean b = (stateInterface_copy[i.getNumero()] == State.DESIGNATED || stateInterface_copy[i.getNumero()] == State.ROOTPORT || stateInterface_copy[i.getNumero()] == State.LEARNING);
		i.setColor(b);
		return b;
	}
	
	public void reset(){
		
		first_copy = false;

		for(Interface inter : interfaceElement)
			stateInterface_copy[inter.getNumero()] = stateInterface[inter.getNumero()];
		
		for(ElementReseau.Interface i : interfaceElement) {
			isInterfaceSTPactif[i.getNumero()] = false;
			stateInterface[i.getNumero()] = State.LEARNING;
			
			bidDestination[i.getNumero()] = -1;
		}
		
		rootBID = BID;
		sourceBID = BID;
	}

	
	@SuppressWarnings("static-access")
	@Override
	public void run() {

		/* Toutes les 30 secondes la configuration est effacée. 
		   Pour gérer les modifications de la topologie */
		Thread timer = new Thread() { 
			
			@Override
			public void run() {
				while(running) {
					try {
						helloTime.sleep(30000);
						reset();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		timer.start();
			
		int count = 0;
		
		while(running) {

			//On vérifie périodiquement s'il faut bloquer des ports.
			if(count % 6 == 0) {
				checkBlockPort();
			}
			
			//HELLO TIME
			ProtocoleSTP.annoncerRoot(element, metric+1);
			
			try {
				helloTime.sleep(2000);
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			count++;
		}
	}
	
	/**
	 * Permet de redemarrer le thread
	 * 
	 * @param in: le flux d'entrée
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		helloTime = new Thread(this);
		helloTime.start();
	}
}
