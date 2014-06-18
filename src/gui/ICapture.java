package gui;

import paquets.Paquet;

/**********************************************************************
 * <p>
 * But:<br>
 * Permet d'envoyer les paquets à analyser à la capture.
 * </p><p>
 * Description:<br>
 * L'interface permet la liaison entre l'interface réseau et la capture 
 * visible par l'utilisateur.
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public interface ICapture {

	/**
	 * Envoie un nouveau paquet à une capture en cours 
	 * @param p le paquet à analyser
	 */
	public void analysePaquet(Paquet p);
	
	/**
	 * Permet de supprimer la capture en cours si l'élément est détruit.
	 */
	public void destroy();
}
