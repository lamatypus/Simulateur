package application;

/**********************************************************************
 * <p>
 * But:<br>
 * Interface avec un service pouvant �tre ex�cut� en tache de fond sur un pc ou serveur
 * avec les commandes sp�cifique.
 * </p><p>
 * Description:<br>
 * Les services sont des t�ches de fond ex�cut� par des pc ou des serveurs.
 * On peut les administrer via un terminal.
 * 
 * </p>
 *
 * @author		Rapha�l Buache
 * @author     	Magali Fr�lich
 * @author     	C�dric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public interface Services {

	/**
	 * Permet de d�marrer le service
	 */
	public void demarreService();
	
	/**
	 * Permet de stopper le service en cours
	 */
	public void stopService();
	
	/**Permet de conna�tre le nom du service
	 * 
	 * @return	le nom
	 */
	public String getName();
	
	/**
	 * Permet de savoir si le service poss�de un fichier de
	 * configuration �ditable
	 * 
	 * @return true, si c'est le cas
	 */
	public boolean configurable();
	
	/**
	 * Permet de r�cup�rer la configuration actuel.
	 * 
	 * @return la configuration
	 */
	public String getConfig();
	
	/**
	 * Permet de mettre � jour la configuration.
	 * Attention, cette m�thode est r�sponsable de ne pas � jour
	 * la configuration pendant que le service est en fonction.
	 * 
	 * @param s	La nouvelle configuration
	 */
	public void setConfig(String s);
	
}
