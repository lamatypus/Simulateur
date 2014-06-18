package elements;

import java.io.Serializable;
import java.util.Random;
import java.util.logging.Logger;

import controleur.Simulateur;
import gui.ICapture;
import gui.JCaptures;
import gui.JDragElement;
import standards.*;
import paquets.*;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe abtraite englobant les différents éléments réseaux
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de gérer des éléments réseaux, d'obtenir leurs
 * interfaces, de les connecter entre eux et d'obtenir des informations sur
 * ceux-ci.
 * </p>
 * 
 * @author Raphaël Buache
 * @author Magali Frölich
 * @author Cédric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
@SuppressWarnings("serial")
public abstract class ElementReseau implements Serializable {

	public static Logger LOGGER = Logger.getLogger("InfoLogging");

	protected Interface[] Ilist;
	protected int nbInterfaces;
	protected JDragElement parent;

	/**
	 * Constructeur appellé par les sous-classes
	 */
	public ElementReseau() {
	}

	/**
	 * Créer un élément réseau avec un nombre d'interfaces et le nom utilisé
	 * pour le nom des interfaces
	 * 
	 * @param nbInterface: nombre d'interface
	 * @param nom
	 */
	public ElementReseau(int nbInterface, String nom) {
		Ilist = new Interface[nbInterface];
		this.nbInterfaces = nbInterface;
		for (int i = 0; i < nbInterface; i++) {
			Ilist[i] = new Interface(i, nom + i);
			Ilist[i].setActive(true);
		}
	}
	
	public void destroy() {
		
		for(Interface i : Ilist)
		{
			if(i.o != null)
				i.o.destroy();
			i.setActive(false);
			if(i.getInterfaceDest() != null)
				disconnect(i.getParent(), i.getInterfaceDest().getParent(), i.getNumero(), i.getInterfaceDest().getNumero());
			
		}
		
		Simulateur.frame.info.setInfo("");
		
	}

	/**
	 * Créer un élément réseau utilisé pour l'interface graphique
	 * 
	 * @param nbInterface: nombre d'interface
	 * @param ghost
	 */
	public ElementReseau(int nbInterface, boolean ghost) {
		this.nbInterfaces = nbInterface;
	}

	/**
	 * Permet d'obtenir la un tableau contenant toute les interfaces de
	 * l'élément réseau
	 * 
	 * @return le tableau d'interfaces
	 */
	public Interface[] getInterface() {
		return Ilist;
	}

	/**
	 * Permet d'obtenir l'interface correspondante à une adresse MAC
	 * 
	 * @param mac: l'adresse MAC
	 * @return l'interface si trouvée sinon null
	 */
	public Interface getInterface(MAC mac) {
		for (Interface i : getInterface()) {
			if (i.getMac().equals(mac)) {
				return i;
			}
		}
		return null;
	}

	/**
	 * Permet de définir l'élément graphique parent
	 * 
	 * @param p: le parent 
	 */
	public void setParent(JDragElement p) {
		this.parent = p;
	}

	/**
	 * Permet d'obtenir l'élément graphique parent
	 * 
	 * @return le parent
	 */
	public JDragElement getParent() {
		return this.parent;
	}

	/**
	 * Permet de connecter deux éléments réseaux (e1,e2) entre eux par le biais
	 * de deux numéros d'interface (i1, i2)
	 * 
	 * @param e1: l'élément réseau 1
	 * @param e2: l'élément réseau 2
	 * @param i1: le numéro de l'interface de e1
	 * @param i2: le numéro de l'interface de e2
	 * @return true si la connection à eu lieu
	 */
	public static boolean connect(ElementReseau e1, ElementReseau e2, int i1,
			int i2) {
		if (e1 == null || 
				e2 == null || 
				i1 >= e1.getInterface().length || 
				i2 >= e2.getInterface().length)
			return false;
		if (e1.getInterface()[i1].getInterfaceDest() != null || 
				e2.getInterface()[i2].getInterfaceDest() != null)
			return false;

		e1.getInterface()[i1].setInterfaceDest(e2.getInterface()[i2]);
		e2.getInterface()[i2].setInterfaceDest(e1.getInterface()[i1]);

		return true;
	}

	/**
	 * Permet de déconnecter deux éléments réseaux (e1,e2) entre eux par le
	 * biais de deux numéros d'interface (i1, i2)
	 * 
	 * @param e1: l'élément réseau 1
	 * @param e2: l'élément réseau 2
	 * @param i1: le numéro de l'interface de e1
	 * @param i2: le numéro de l'interface de e2
	 * @return true si la déconnection à eu lieu
	 */
	public static boolean disconnect(ElementReseau e1, ElementReseau e2,
			int i1, int i2) {
		if (e1 == null || 
				e2 == null || 
				i1 >= e1.getInterface().length || 
				i2 >= e2.getInterface().length)
			return false;

		if (e1.getInterface()[i1].getInterfaceDest() != e2.getInterface()[i2]
				|| e2.getInterface()[i2].getInterfaceDest() != e1.getInterface()[i1])
			return false;

		e1.getInterface()[i1].setInterfaceDest(null);
		e2.getInterface()[i2].setInterfaceDest(null);

		return true;
	}

	/**
	 * Permet de faire attendre un paquet
	 */
	public synchronized void waitPaquet() {
		try {
			wait(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet de reveiller les paquets en attente
	 */
	public synchronized void wakeupPaquets() {
		notifyAll();
	}

	/**
	 * Permet d'obtenir le nom de l'élément réseau et son numéro
	 * 
	 * @return son nom + son numéro
	 */
	public abstract String getInfo();

	/**
	 * Permet d'obtenir toute les informations concernant l'élément.
	 * 
	 * @return les information de l'élément
	 */
	public abstract String allInfo();

	/**
	 * Permet de configurer l'élément par le biais d'une console
	 */
	public abstract void config();

	/**
	 * Permet de créer un nouvel élément réseau du même type que l'actuel
	 * 
	 * @return le nouvel élément
	 */
	public abstract ElementReseau newElem();

	/**
	 * Défini ce que fait un élément réseau lorsqu'il recoit un paquet sur son
	 * interface au niveau de la couche liaison du modèle OSI
	 * 
	 * @param p: le paquet
	 * @param i: l'interface
	 */
	public abstract void recoitCoucheLiaison(Paquet p, Interface i);

	/**********************************************************************
	 * <p>
	 * But:<br>
	 * Classe représentant les interfaces que les éléments réseaux contiennent
	 * </p>
	 * <p>
	 * Description:<br>
	 * Cette classe permet de gérer des interfaces d'éléments réseaux.
	 * </p>
	 * 
	 * @author Raphaël Buache
	 * @author Magali Frölich
	 * @author Cédric Rudareanu
	 * @author Yann Malherbe
	 * @version 1.0
	 * @modify 18.06.2014
	 ***********************************************************************/
	public class Interface implements Serializable {
		private Interface Idest;
		private MAC mac;
		private String nom;
		private transient ICapture o;
		private int numero;
		private Random rand = new Random();
		private boolean active;
		private boolean green = true;


		/**
		 * Permet de créer une interface en lui donnant un nom et son numéro.
		 * L'adresse MAC est automatiquement générée
		 * 
		 * @param i: numéro de l'interface
		 * @param nom: nom de l'interface
		 */
		public Interface(int i, String nom) {
			mac = new MAC();
			Idest = null;
			o = null;
			this.nom = nom;
			numero = i;
		}

		/**
		 * Permet d'obtenir l'élément réseau qui contient l'interface
		 * 
		 * @return l'élément réseau
		 */
		public ElementReseau getParent() {
			return ElementReseau.this;
		}

		/**
		 * Permet d'obtenir le numéro de l'interface
		 * 
		 * @return le numéro
		 */
		public int getNumero() {
			return numero;
		}

		/**
		 * Permet d'obtenir le nom de l'interface
		 * 
		 * @return le nom
		 */
		public String getNom() {
			return nom;
		}

		/**
		 * Permet de savoir si l'interface est activée ou non
		 * 
		 * @return true si active
		 */
		public boolean isActive() {
			return active;
		}

		/**
		 * Permet d'activé ou non une interface
		 * 
		 * @param active: si active
		 */
		public void setActive(boolean active) {
			this.active = active;
			setColor(active);
			Simulateur.frame.reseaux.repaint();
		}

		/**
		 * Permet de savoir si l'interface doit être verte pour l'affichage sur
		 * l'interface graphique
		 * 
		 * @return true si vert
		 */
		public boolean isGreen() {
			return green;
		}

		/**
		 * Permet de choisir si la couleur est verte ou non
		 * 
		 * @param vert: si vert
		 */
		public void setColor(boolean vert) {
			this.green = vert;
			Simulateur.frame.reseaux.repaint();
		}

		/**
		 * Permet d'accéder à l'interface qui est connectée à l'autre bout
		 * 
		 * @return l'interface
		 */
		public Interface getInterfaceDest() {
			return Idest;
		}

		/**
		 * Permet de configurer quelle interface est connectée à l'autre bout
		 * 
		 * @param dest: interface de destination
		 */
		public void setInterfaceDest(Interface dest) {
			Idest = dest;
		}

		/**
		 * Permet de connaitre l'adresse MAC de l'interface
		 * 
		 * @return l'adresse MAC
		 */
		public MAC getMac() {
			return mac;
		}

		/**
		 * Permet d'analyser le paquet en paramètre
		 * 
		 * @param p: le paquet
		 */
		void analyse(Paquet p) {
			if (o != null) {
				o.analysePaquet(p);
				JCaptures.updateFilter();
			}
			
		}

		/**
		 * Permet de recevoir un paquet sur l'interface
		 * 
		 * @param p: le paquet
		 * @param i: l'interface
		 */
		public void recoit(Paquet p, Interface i) {
			if (i.isActive()){
				analyse(p);
				
				if(ElementReseau.this instanceof Switch) {
					if((((Switch)ElementReseau.this)).getTableSTP().isActiveBySTP(this) || p instanceof PaquetSTP) {
						recoitCoucheLiaison(p, i);
					}
				}
				else
					recoitCoucheLiaison(p, i);
				
			}
		}

		/**
		 * Permet d'envoyer un paquet sur l'interface de destination
		 * 
		 * @param p: le paquet
		 */
		public void envoyer(Paquet p) {
			try {
				Thread.sleep(20 + rand.nextInt(80));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (Idest != null && isActive()) {
				analyse(p);
				
				if(ElementReseau.this instanceof Switch) {
					if((((Switch)ElementReseau.this)).getTableSTP().isActiveBySTP(this) || p instanceof PaquetSTP) {
						Idest.recoit(p, getInterfaceDest());
					}
				}
				else
					Idest.recoit(p, getInterfaceDest());
			}
		}

		/**
		 * Permet d'arrêter l'analyse de paquet
		 */
		public void suppAnalyse() {
			o = null;
		}

		/**
		 * Permet d'activé l'analyse de paquet
		 * 
		 * @param i: la capture
		 */
		public void setAnalyse(ICapture i) {
			o = i;
		}
	}
}
