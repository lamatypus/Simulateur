package application;

/**********************************************************************
 * <p>
 * But:<br>
 * Interface avec un service pouvant être exécuté en tache de fond sur un pc ou serveur
 * avec les commandes spécifique.
 * </p><p>
 * Description:<br>
 * Les services sont des tâches de fond exécuté par des pc ou des serveurs.
 * On peut les administrer via un terminal.
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
public interface Services {

	/**
	 * Permet de démarrer le service
	 */
	public void demarreService();
	
	/**
	 * Permet de stopper le service en cours
	 */
	public void stopService();
	
	/**Permet de connaître le nom du service
	 * 
	 * @return	le nom
	 */
	public String getName();
	
	/**
	 * Permet de savoir si le service possède un fichier de
	 * configuration éditable
	 * 
	 * @return true, si c'est le cas
	 */
	public boolean configurable();
	
	/**
	 * Permet de récupèrer la configuration actuel.
	 * 
	 * @return la configuration
	 */
	public String getConfig();
	
	/**
	 * Permet de mettre à jour la configuration.
	 * Attention, cette méthode est résponsable de ne pas à jour
	 * la configuration pendant que le service est en fonction.
	 * 
	 * @param s	La nouvelle configuration
	 */
	public void setConfig(String s);
	
}
