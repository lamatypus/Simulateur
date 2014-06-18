package standards;

import java.io.Serializable;
import java.util.Arrays;

import exception.MasqueNonValide;

/**********************************************************************
 * <p>
 * But:<br>
 * Gestion d'un masque pour une adresse IPv4
 * </p><p>
 * Description:<br>
 * Cette classe permet de gérer un masque pour une adresse IPv4 
 * et offre différents outils sur celui-ci
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
public class MasqueIPv4 implements Serializable {

	protected byte[] masque = new byte[4];

	/**
	 * Constructeur de la classe
	 * Le masque est initialisé à 255.255.255.255
	 */
	public MasqueIPv4() {
		for (int i = 0; i < masque.length; i++) {
			masque[i] = (byte) 255;
		}
	}

	/**
	 * * Constructeur de la classe avec un masque fourni sous forme de tableau de bytes
	 * @param m tableau de 4 bytes
	 */
	public MasqueIPv4(byte[] m) {
		masque = m;
	}

	/**
	 * Test si un masque est valide
	 * @param masque le masque à tester
	 * @return vrai si valide
	 */
	public static boolean estValide(byte[] masque) {
		byte[] valide = new byte[] { (byte) 255, (byte) 254, (byte) 252,
				(byte) 248, (byte) 240, (byte) 224, (byte) 192, (byte) 128,
				(byte) 0 };
		int i = 0;

		if (masque.length != 4)
			return false;

		while (masque[++i] == valide[0]) {
			if (i == 4)
				return true;
		}

		if (!Arrays.asList(valide).contains(masque[i]))
			return false;

		for (++i; i < 4; i++) {
			if (masque[i] != valide[8])
				return false;
		}

		return true;
	}

	/**
	 * @return le masque sous format d'un tableau de 4 bytes
	 */
	byte[] getMasque() {
		return masque;
	}

	/**
	 * @return le masque au format CIDR
	 */
	public int getCIDR() {
		int cidr = 0;
		byte[] possible = new byte[] { (byte) 0, (byte) 128, (byte) 192,
				(byte) 224, (byte) 240, (byte) 248, (byte) 252, (byte) 254 };

		for (int i = 0; i < 4; i++) {
			for (byte pos : possible) {
				if (pos == masque[i]) {
					return cidr;
				}
				cidr++;
			}
		}
		return cidr;
	}

	/**
	 * Constructeur de la classe avec en paramètre un masque au format String
	 * @param m String au format x.x.x.x
	 * @throws MasqueNonValide
	 */
	public MasqueIPv4(String m) throws MasqueNonValide {

		if (m == null)
			throw new MasqueNonValide();

		String[] traite = m.split("\\.");

		if (traite.length != masque.length)
			throw new MasqueNonValide();

		for (int i = 0; i < traite.length; i++) {
			try {
				masque[i] = (byte) Integer.parseInt(traite[i]);
			} catch (NumberFormatException e) {
				throw new MasqueNonValide();
			}
		}
	}

	/**
	 * @return le masque inverse
	 */
	public MasqueIPv4 getMasqueInverse() {
		MasqueIPv4 masqueInverse = new MasqueIPv4();

		for (int i = 0; i < masque.length; i++) {
			masqueInverse.masque[i] = (byte) (~masque[i] & 0xff);
		}

		return masqueInverse;
	}

	@Override
	public String toString() {
		String tmp = "";
		for (int i = 0; i < masque.length; i++) {
			tmp += 0xFF & masque[i];
			if (i < masque.length - 1) {
				tmp += ".";
			}
		}
		return tmp;
	}

	/**
	 * Compare un masque avec le masque de la classe
	 * @param masque à tester
	 * @return vrai si équivalent
	 */
	public boolean compare(MasqueIPv4 masque) {
		if (masque == null)
			return false;

		return masque.getBinaire() == getBinaire();
	}

	/**
	 * @return le masque au format binaire
	 */
	public int getBinaire() {
		return getBinaire(masque);
	}

	/**
	 * Retourne un masque au format binaire en fonction d'un masque passé en paramètre
	 * @param adresse au format d'un tableau de 4 bytes
	 * @return le masque au format binaire
	 */
	public static int getBinaire(byte[] adresse) {
		if (adresse.length != 4)
			return -1;

		int tmp = 0;
		tmp |= ((int) adresse[0] << 24) & 0xFF000000;
		tmp |= ((int) adresse[1] << 16) & 0xFF0000;
		tmp |= ((int) adresse[2] << 8) & 0xFF00;
		tmp |= ((int) adresse[3]) & 0xFF;
		return tmp;
	}
}
