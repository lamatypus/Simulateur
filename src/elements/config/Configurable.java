package elements.config;

import elements.config.ShellPC.Signal;

/**********************************************************************
 * <p>
 * But:<br>
 * Permet de rendre une classe configurable
 * </p><p>
 * Description:<br>
 * Elle doit implémenter deux methodes. Une pour recevoir les ordres
 * et une pour recevoir des signaux.
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public interface Configurable {

	/**
	 * Cette méthode doit traiter l'ordre reçu.
	 * 
	 * @param cmd la commande
	 */
	public void ordre(String cmd);
	
	/**
	 * Cette méthode doit traiter le signal reçu.
	 * 
	 * @param s	le signal
	 */
	public void signal(Signal s);
	
}
