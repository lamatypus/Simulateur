package standards;

import java.io.Serializable;

import exception.IPNonValide;
import exception.MasqueNonValide;

/**********************************************************************
 * <p>
 * But:<br>
 * Gestion d'une adresse IPv4
 * </p><p>
 * Description:<br>
 * Cette classe permet de gérer une adresse IPv4 et offre différents outils
 * comme récupérer l'adresse de broadcast, faire des comparaisons, définir un masque etc...
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
public class IPv4 implements Serializable{

	private byte[] adresse = new byte[4];
	private MasqueIPv4 masque;

	/**
	 * Constructeur de la classe, assigne une adresse en 0.0.0.0
	 */
	public IPv4() {
		for (int i = 0; i < adresse.length; i++) {
			adresse[i] = 0;
		}
	}
	
	/**
	 * Constructeur de la classe avec une adresse ip fournie sous forme de tableau de bytes
	 * @param ad tableau de 4 bytes
	 * @throws IPNonValide
	 */
	public IPv4(byte[] ad) throws IPNonValide{
		if(ad == null || ad.length != adresse.length){
			throw new IPNonValide();
		}
		adresse = ad;
	}
	

	/**
	 * Constructeur de la classe avec une adresse ip et masque fournis sous forme de tableaux de bytes
	 * @param ad tableau de 4 bytes
	 * @param m tableau de 4 bytes
	 * @throws IPNonValide
	 * @throws MasqueNonValide
	 */
	public IPv4(byte[] ad, byte[] m) throws IPNonValide, MasqueNonValide {
		if (ad.length != adresse.length) {
			throw new IPNonValide();
		}
		masque = new MasqueIPv4(m);
		adresse = ad;
	}

	/**
	 * Définit une adresse ip avec un tableau de 4 bytes passé en paramètre
	 * @param b tableau de 4 bytes
	 */
	private void setByte(byte[] b) {
		adresse = b;
	}
	
	/**
	 * Retourne l'adresse de broadcast de l'adresse IP
	 * @return objet IPv4 avec l'adresse de broadcast
	 */
	public IPv4 getBroadcast (){
		IPv4 broadcast = new IPv4();
		MasqueIPv4 masqueInverse = masque.getMasqueInverse();
		
		for (int i = 0; i < adresse.length; i++){
			broadcast.adresse[i] = (byte) (masqueInverse.masque[i] | adresse[i]);
		}
		
		return broadcast;
	}
	
	/**
	 * Retourne l'adresse de broadcast générale (255.255.255.255)
	 * @return objet IPv4 avec l'adresse de broadcast générale
	 */
	public static IPv4 getGeneralBroadcast(){
		IPv4 ip = new IPv4();
		ip.setByte(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff});
		return ip;
	}
	
	/**
	 * Compare deux adresse IPv4
	 * @param ip
	 * @return vrai si identique
	 */
	public boolean compare (IPv4 ip){
		if(ip == null)
			return false;
		
		return ip.getBinaire() == getBinaire();
	}

	//Format x.x.x.x
	/**
	 * Constructeur de la classe IPv4 avec une ip au format String x.x.x.x en paramètre
	 * @param ip au format String x.x.x.x
	 * @throws IPNonValide
	 */
	public IPv4(String ip) throws IPNonValide{
		
		if(ip == null)
			throw new IPNonValide();
		
		String[] traite = ip.split("\\.");
		
		if(traite.length != adresse.length)
			throw new IPNonValide();
		
		for(int i=0;i<traite.length;i++){
			try{
				adresse[i] = (byte)Integer.parseInt(traite[i]);
			}catch(NumberFormatException e){
				throw new IPNonValide();
			}
		}
	}
	
	/**
	 * Constructeur de la classe IPv4 avec une ip et masque au format String x.x.x.x en paramètre
	 * @param ip au format String x.x.x.x
	 * @param masque au format String x.x.x.x
	 * @throws IPNonValide
	 * @throws MasqueNonValide
	 */
	public IPv4(String ip, String masque) throws IPNonValide, MasqueNonValide{
		
		if(ip == null)
			throw new IPNonValide();
		
		String[] traite = ip.split("\\.");
		
		if(traite.length != adresse.length)
			throw new IPNonValide();
		
		for(int i=0;i<traite.length;i++){
			try{
				adresse[i] = (byte)Integer.parseInt(traite[i]);
			}catch(NumberFormatException e){
				throw new IPNonValide();
			}
		}
		
		this.masque = new MasqueIPv4(masque);
	}
	
	/**
	 * Constructeur de la classe IPv4 avec une ip au format String x.x.x.x en paramètre et masque au format CIDR
	 * @param ip au format String x.x.x.x
	 * @param m compris entre 0 et 32
	 * @throws IPNonValide
	 */
	public IPv4(String ip, int m) throws IPNonValide{
		this(ip);
		try {
			setMasque(m);
		} catch (MasqueNonValide e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @return vrai si l'adresse ipv4 est une adresse de broadcast générale
	 */
	public boolean isBroadcast() {
		for (int i = 0; i < adresse.length; i++) {
			if (adresse[i] != 255) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Ipv4 au format binaire
	 * @return l' adresse ip au format binaire
	 */
	public int getBinaire (){
		return getBinaire(adresse);
	}
	
	/**
	 * Ipv4 au format binaire
	 * @param adresse au format d'un tableau de 4 bytes à convertir au format binaire
	 * @return l'adresse au format binaire
	 */
	public static int getBinaire (byte[] adresse){
		if(adresse.length != 4)
			return -1;
		
		int tmp = 0;
		tmp |= ((int)adresse[0] << 24) & 0xFF000000;
		tmp |= ((int)adresse[1] << 16) & 0xFF0000;
		tmp |= ((int)adresse[2] << 8) & 0xFF00;
		tmp |= ((int)adresse[3]) & 0xFF;
		return tmp;
	}
	
	/**
	 * Retourne un tableau de 4 bytes représentant une IPv4 en fonction d'une adresse au format binaire
	 * @param binaire
	 * @return tableau de 4 byte
	 */
	public static byte[] getIPv4(int binaire){
		byte[] ip = new byte[4];
		
		ip[0] = (byte)((binaire & 0xFF000000) >> 24);
		ip[1] = (byte)((binaire & 0xFF0000) >> 16);
		ip[2] = (byte)((binaire & 0xFF00) >> 8);
		ip[3] = (byte)((binaire & 0xFF));
				
		return ip;
	}
	
	/**
	 * Permet de savoir si une adresse IP passée en paramètre est dans le même sous réseau que l'adresse IP de la classe
	 * @param ip
	 * @return le masque au format CIDR si dans le même sous réseau, autrement: -1
	 */
	public int estDansSousReseau (IPv4 ip){
		
		if ((ip.getBinaire() & getBinaire(masque.masque)) == (this.getBinaire() & getBinaire(masque.masque))){
			return masque.getCIDR();
		}
		return -1;
	}

	/**
	 * @return ip au format tableau de 4 bytes
	 */
	public byte[] getIPv4() {
		return adresse;
	}
	
	/**
	 * @return le masque de l'adresse IP
	 */
	public MasqueIPv4 getMasque(){
		return masque;
	}

	/**
	 * Définit un masque à une ip au format CIDR
	 * @param m compris entre 0 et 32
	 * @throws MasqueNonValide
	 */
	public void setMasque(int m) throws MasqueNonValide{
		
		if(m >= 0 && m <= 32){
						
			byte[] octet = new byte[4];
			
			for(int i=0; i < m; i++) {
				octet[i/8] |= (byte)(1 << 7-(i-(i/8*8)));
			}
			
			masque = new MasqueIPv4(octet);
			
		}
		else throw new MasqueNonValide();
		
	}
	
	/**
	 * Définit un masque au format d'un tableau de 4 bytes à une ip avec
	 * @param m tableau de 4 bytes
	 * @throws MasqueNonValide
	 */
	public void setMasque(byte[] m) throws MasqueNonValide{
		masque = new MasqueIPv4(m);
	}
	
	@Override	
	public String toString() {
		String tmp = "";
		for (int i = 0; i < adresse.length; i++) {
			tmp += 0xFF & adresse[i];
			if (i < adresse.length - 1) {
				tmp += ".";
			}
		}
		return tmp;
	}
}
