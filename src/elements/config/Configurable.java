package elements.config;

import elements.config.ShellPC.Signal;

/**********************************************************************
 * <p>
 * But:<br>
 * Permet de rendre une classe configurable
 * </p><p>
 * Description:<br>
 * Elle doit impl�menter deux methodes. Une pour recevoir les ordres
 * et une pour recevoir des signaux.
 * </p>
 *
 * @author		Rapha�l Buache
 * @author     	Magali Fr�lich
 * @author     	C�dric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public interface Configurable {

	/**
	 * Cette m�thode doit traiter l'ordre re�u.
	 * 
	 * @param cmd la commande
	 */
	public void ordre(String cmd);
	
	/**
	 * Cette m�thode doit traiter le signal re�u.
	 * 
	 * @param s	le signal
	 */
	public void signal(Signal s);
	
}
