package elements;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import application.DHCPserveur;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe d'élément réseau serveur.
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de gérer le comportement des serveurs.
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
public class Serveur extends PC {
	private static int globalId = 1;
	public int id;

	/**
	 * Créer un serveur avec un nombre d'interface IP et des services actifs
	 * 
	 * @param nbInterface: nombre d'interface
	 */
	public Serveur(int nbInterface) {
		super(nbInterface);
		super.services.addLast(new DHCPserveur(this));
	}

	/**
	 * Créer un serveur utilisé pour l'interface graphique
	 * 
	 * @param nbInterface: nombre d'interface
	 * @param ghost
	 */
	public Serveur(int nbInterface, boolean ghost) {
		super(nbInterface, ghost);
	}

	/**
	 * Set l'id du PC et incrémente l'id global
	 */
	@Override
	protected void incId() {
		id = globalId++;
	}

	/**
	 * Permet d'obtenir le nom du PC et son numéro
	 */
	@Override
	public String getInfo() {
		return "Serveur-" + String.valueOf(id);
	}

	/**
	 * Permet de créer un nouveau PC identique à l'actuel
	 */
	public ElementReseau newElem() {
		return new Serveur(nbInterfaces);
	}

	/**
	 * Permet d'enregistrer les informations du serveur
	 * 
	 * @param out: flux de sortie
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
	 * @param in: flux d'entrée
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
}
