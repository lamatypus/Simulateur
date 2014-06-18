package gui;

import paquets.Paquet;

/**********************************************************************
 * <p>
 * But:<br>
 * Permet d'envoyer les paquets � analyser � la capture.
 * </p><p>
 * Description:<br>
 * L'interface permet la liaison entre l'interface r�seau et la capture 
 * visible par l'utilisateur.
 * </p>
 *
 * @author		Rapha�l Buache
 * @author     	Magali Fr�lich
 * @author     	C�dric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public interface ICapture {

	/**
	 * Envoie un nouveau paquet � une capture en cours 
	 * @param p le paquet � analyser
	 */
	public void analysePaquet(Paquet p);
	
	/**
	 * Permet de supprimer la capture en cours si l'�l�ment est d�truit.
	 */
	public void destroy();
}
