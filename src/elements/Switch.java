package elements;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;

import elements.util.TableSTP;
import paquets.Paquet;
import paquets.PaquetEthernet;
import paquets.PaquetSTP;
import protocoles.ProtocoleSTP;
import standards.MAC;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe d'élément réseau switch.
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de gérer le comportement des switchs.
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
public class Switch extends ElementReseau implements Serializable{
	private TableCAM tableCAM = new TableCAM();
	private TableSTP tableSTP;
	private static int globalId = 1;
	public int id;

	/**
	 * Créer un switch avec un nombre d'interface
	 * 
	 * @param nbInterface: nombre d'interface
	 */
	public Switch(int nbInterface) {
		super(nbInterface, "Fa 0/");

		id = globalId++;
		tableSTP = new TableSTP(this);
	}
	
	/**
	 * Permet de stopper le thread s'occupant de STP
	 */
	@Override
	public void destroy(){
		super.destroy();
		tableSTP.stop();
	}

	/**
	 * Créer un switch utilisé pour l'interface graphique
	 * 
	 * @param nbInterface: nombre d'interface
	 * @param ghost
	 */
	public Switch(int nbInterface, boolean ghost) {
		super(nbInterface, ghost);
	}

	/**
	 * Permet d'obtenir le nom du switch et son numéro
	 */
	@Override
	public String getInfo() {
		return "Switch-" + String.valueOf(id) + "\n";
	}

	/**
	 * Ne fait rien, un switch n'est pas configurable
	 */
	@Override
	public void config() {
	}

	
	/**
	 * Recoit le paquet sur son interface (i) et en envoie une copie sur la/les
	 * interfaces correspondantes
	 * 
	 * @param p: le paquet
	 * @param i: l'interface
	 */
	@Override
	public void recoitCoucheLiaison(Paquet p, Interface i) {
		LOGGER.info(getInfo() + "Recoit couche liaison");

		// Si paquet STP
		if (ProtocoleSTP.monPaquet(p)) {
			ProtocoleSTP.recoit(this, (PaquetSTP) p, i);
			return;
		}

		// Enregistre dans la table CAM
		tableCAM.ajouteCam(i, ((PaquetEthernet) p).getMacSource());
		Interface inter = tableCAM.getInterface(((PaquetEthernet) p)
				.getMacDest());

		if (inter != null && !tableSTP.isActiveBySTP(inter)
				&& !ProtocoleSTP.monPaquet(p)) {
			inter = null;
		}

		// Si pas d'interface connue pour l'adresse MAC
		if (inter == null) {
			Paquet newPaquet;

			// Envoie à toutes les interfaces
			for (Interface interfaceSortie : getInterface()) {
				if (!interfaceSortie.equals(i)) {
					newPaquet = (Paquet) p.clone();

					if (tableSTP.isActiveBySTP(interfaceSortie)) {
						interfaceSortie.envoyer(newPaquet);
					}
				}
			}
		}
		// Si interface trouvée dans table CAM
		else {

			if (tableSTP.isActiveBySTP(inter)) {
				inter.envoyer(p);
			}
		}
	}

	/**
	 * Permet d'obtenir toute les infos du switch et de sa table CAM
	 */
	@Override
	public String allInfo() {
		String tmp = "<h3>" + getInfo() + "</h3><hr>CAM Table content: <br>"
				+ (tableCAM.tableCam.size() == 0 ? "Empty<br/>" : "");
		for (TableCAM.CAM c : tableCAM.tableCam) {
			tmp += c.macDest + " -> eth" + c.interfaceSortie.getNumero()
					+ "<br>";
		}

		tmp += "BID : " + tableSTP.getBid() + "<br>";
		tmp += "BID root : " + tableSTP.getRootBID() + "<br>";
		tmp += "metric : " + tableSTP.getMetric() + "<br>";

		for (Interface i : getInterface())
			tmp += "Interface[" + i.getNumero() + "] : "
					+ tableSTP.getStatePort(i) + "<br>";

		return tmp;
	}

	/**
	 * Permet de créer un nouveau switch identique à l'actuel
	 */
	@Override
	public ElementReseau newElem() {
		return new Switch(nbInterfaces);
	}

	/**
	 * Permet d'obtenir la table STP
	 * 
	 * @return la table STP
	 */
	public TableSTP getTableSTP() {
		return tableSTP;
	}

	/**
	 * Permet d'enregistrer les informations du serveur
	 * 
	 * @param out: le flux de sortie
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeInt(globalId);
		out.writeInt(id);
	}

	/**
	 * Permet de lire les informations du serveur
	 * 
	 * @param in: le flux d'entrée
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		globalId = in.readInt();
		id = in.readInt();
	}

	/**
	 * Permet de reseter les id
	 */
	public static void reset() {
		globalId = 1;
	}

	/**********************************************************************
	 * <p>
	 * But:<br>
	 * Classe de la Table CAM incluse dans tout switch.
	 * </p>
	 * <p>
	 * Description:<br>
	 * Cette classe permet de gérer le comportement de la table CAM.
	 * </p>
	 * 
	 * @author Raphaël Buache
	 * @author Magali Frölich
	 * @author Cédric Rudareanu
	 * @author Yann Malherbe
	 * @version 1.0
	 * @modify 18.06.2014
	 ***********************************************************************/
	private class TableCAM implements Serializable {
		LinkedList<CAM> tableCam;

		/**
		 * Permet de créer une table CAM
		 */
		public TableCAM() {
			tableCam = new LinkedList<CAM>();
		}

		/**
		 * Permet de trouver l'interface de sortie pour une adresse MAC de
		 * destination
		 * 
		 * @param mac: l'adresse MAC
		 * @return l'interface
		 */
		public Interface getInterface(MAC mac) {
			for (CAM cam : tableCam) {
				if (cam.macDest.equals(mac)) {
					return cam.interfaceSortie;
				}
			}
			return null;
		}

		/**
		 * Permet de trouver un champ CAM existant avec son interface de 
		 * sortie et son adresse MAC de destination
		 * 
		 * @param interfaceSortie: l'interface de sortie
		 * @param macDest: l'adresse MAC de destination
		 * @return le champs CAM trouvé
		 */
		private CAM getCAM(Interface interfaceSortie, MAC macDest) {
			for (CAM cam : tableCam) {

				if (cam.timer > 10) {
					tableCam.remove(cam);
					return null;
				} else
					cam.timer++;

				if (interfaceSortie.equals(cam.interfaceSortie)
						&& macDest.equals(cam.macDest)) {
					return cam;
				}
			}
			return null;
		}

		/**
		 * Permet d'ajouter un champs CAM dans la table CAM avec l'interface
		 * de sortie et l'adresse MAC de destination
		 * 
		 * @param interfaceSortie: l'interface de sortie
		 * @param macDest: l'adresse MAC de destination
		 */
		private void ajouteCam(Interface interfaceSortie, MAC macDest) {
			Interface interfaceCourante = getInterface(macDest);

			if (interfaceCourante == null) {
				tableCam.add(new CAM(interfaceSortie, macDest));
			} else if (!interfaceSortie.equals(interfaceCourante)) {
				supprimeCam(interfaceCourante, macDest);
				tableCam.add(new CAM(interfaceSortie, macDest));
			}
		}

		/**
		 * Permet de supprimer un champs CAM dans la table CAM avec l'interface
		 * de sortie et l'adresse MAC de destination
		 * 
		 * @param interfaceSortie: l'interface de sortie
		 * @param macDest: l'adresse MAC de destination
		 */
		private void supprimeCam(Interface interfaceSortie, MAC macDest) {
			tableCam.remove(getCAM(interfaceSortie, macDest));
		}
		
		/**
		 * Représente une table CAM en string
		 */
		@Override
		public String toString() {
			String s = "";
			for (CAM cam : tableCam) {
				s += cam.toString();
			}
			return s;
		}

		/**********************************************************************
		 * <p>
		 * But:<br>
		 * Classe des champs CAM inclus dans les tables CAM.
		 * </p>
		 * <p>
		 * Description:<br>
		 * Cette classe permet de gérer le comportement des champs CAM.
		 * </p>
		 * 
		 * @author Raphaël Buache
		 * @author Magali Frölich
		 * @author Cédric Rudareanu
		 * @author Yann Malherbe
		 * @version 1.0
		 * @modify 18.06.2014
		 ***********************************************************************/
		private class CAM implements Serializable{
			private Interface interfaceSortie;
			private MAC macDest;
			private int timer;

			/**
			 * Créer un champs CAM avec l'interface de sortie et l'adresse 
			 * MAC de destination
			 * 
			 * @param interfaceSortie: l'interface de sortie
			 * @param macDest: l'adresse MAC de destination
			 */
			public CAM(Interface interfaceSortie, MAC macDest) {
				this.interfaceSortie = interfaceSortie;
				this.macDest = macDest;
				timer = 0;
			}

			/**
			 * Représente un champs CAM en string
			 */
			@Override
			public String toString() {
				return macDest + " => " + interfaceSortie + "\n";
			}
		}
	}
}
