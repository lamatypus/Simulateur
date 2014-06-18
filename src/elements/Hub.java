package elements;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import paquets.Paquet;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe d'�l�ment r�seau hub.
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de g�rer le comportement des hubs.
 * </p>
 * 
 * @author Rapha�l Buache
 * @author Magali Fr�lich
 * @author C�dric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
@SuppressWarnings("serial")
public class Hub extends ElementReseau {
	private static int globalId = 1;
	public int id;

	/**
	 * Cr�er un nouveau hub avec un nombre d'interface
	 * 
	 * @param nbInterface: le nombre d'interface
	 */
	public Hub(int nbInterface) {
		super(nbInterface, "Fa 0/");
		id = globalId++;
	}

	/**
	 * Cr�er un hub utilis� pour l'interface graphique
	 * 
	 * @param nbInterface: le nombre d'interface
	 * @param ghost
	 */
	public Hub(int nbInterface, boolean ghost) {
		super(nbInterface, ghost);
	}

	/**
	 * Permet d'obtenir le nom du hub et son num�ro
	 */
	@Override
	public String getInfo() {
		return "Hub-" + String.valueOf(id);
	}

	/**
	 * Recoit le paquet sur son interface (i) et en envoie une copie sur chacune
	 * des autres interfaces
	 * 
	 * @param p: le paquet
	 * @param i: l'interface
	 */
	@Override
	public void recoitCoucheLiaison(final Paquet p, final Interface i) {
		LOGGER.info(getInfo() + " = Recoit couche liaison");
		
		for (final Interface inter : Ilist) {
			if (!inter.equals(i)) {
				Paquet newPaquet;
				newPaquet = (Paquet) p.clone();
				inter.envoyer(newPaquet);
			}
		}
	}

	/**
	 * Permet d'obtenir le nom du hub et son num�ro
	 */
	@Override
	public String allInfo() {
		return "<h3>" + getInfo() + "</h3><hr>No configuration available";
	}

	/**
	 * Ne fait rien, le hub n'est pas configurable
	 */
	@Override
	public void config() {
	}

	/**
	 * Permet de cr�er un nouveau hub identique � l'actuel
	 */
	@Override
	public ElementReseau newElem() {
		return new Hub(nbInterfaces);
	}

	/**
	 * Permet d'enregistrer les informations du hub
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
	 * Permet de lire les informations du hub
	 * 
	 * @param in: le flux d'entr�e
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
