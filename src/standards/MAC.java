package standards;

import java.io.Serializable;
import java.util.*;

import exception.MACNonValide;

/**********************************************************************
 * <p>
 * But:<br>
 * Gestion d'une adresse MAC
 * </p><p>
 * Description:<br>
 * Cette classe permet de gérer une adresse MAC
 * et offre différents outils sur celle-ci
 * 
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
@SuppressWarnings("serial")
public class MAC implements Serializable {

	private byte[] adresse = new byte[6];
	private static LinkedList<MAC> list = new LinkedList<MAC>();
	private static Random r = new Random();
	private final static MAC broadcast = new MAC(new byte[] { (byte) 0xff,
			(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff });

	/**
	 * Constructeur de la classe.
	 * Génère aléatoirement une adresse MAC Unique
	 */
	public MAC() {
		do {
			r.nextBytes(adresse);
		} while (existe(this));

		list.add(this);
	}

	/**
	 * Constructeur de la classe avec une adresse en paramètre
	 * @param adresse tableau de 6 bytes
	 */
	private MAC(byte[] adresse) {
		this.adresse = adresse;
		list.add(this);
	}

	/**
	 * Constructeur de la classe avec en paramètre l'adresse au format string
	 * @param s
	 * @throws MACNonValide
	 */
	public MAC(String s) throws MACNonValide {
		String[] args = s.split(":");
		if (args.length != 6)
			throw new MACNonValide();
		for (int i = 0; i < 6; i++) {
			if (args[i].length() != 2)
				throw new MACNonValide();
			adresse[i] = (byte) ((Character.digit(args[i].charAt(0), 16) << 4) + Character
					.digit(args[i].charAt(1), 16));
		}

	}

	/**
	 * Vérifie si une adresse MAC existe déjà
	 * @param mac adresse à tester
	 * @return vrai si existe
	 */
	private boolean existe(MAC mac) {
		for (MAC courant : list) {
			if (mac.equals(courant)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Compare une adresse MAC avec l'adresse MAC de la classe
	 * @param m adresse à tester
	 * @return vrai si équivalent
	 */
	public boolean equals(MAC m) {
		for (int i = 0; i < adresse.length; i++) {
			if (m.adresse[i] != this.adresse[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return vrai si l'adresse MAC est une adresse de broadcast
	 */
	public boolean isBroadcast() {
		for (int i = 0; i < adresse.length; i++) {
			if (adresse[i] != (byte) 0xff) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return une adresse mac de broadcast (FF:FF:FF:FF:FF:FF)
	 */
	public static MAC broadcast() {
		return broadcast;
	}

	@Override
	public String toString() {
		String tmp = "";
		for (int i = 0; i < adresse.length; i++) {
			tmp += String.format("%02x", adresse[i]);
			if (i < adresse.length - 1)
				tmp += ":";
		}
		return tmp;
	}
}
