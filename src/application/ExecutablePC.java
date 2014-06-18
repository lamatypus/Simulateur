package application;

import elements.config.*;

/**********************************************************************
 * <p>
 * But:<br>
 * Définit les méthodes de base pour qu'une application s'interface
 * avec un terminal.
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public abstract class ExecutablePC {
	
	private ShellPC shell;
	
	/**Constructeur par défaut. L'application doit connaitre un terminal.
	 * 
	 * @param s Le terminal
	 */
	public ExecutablePC(ShellPC s) { shell = s; }
	
	/**Permet d'écrire en sortie sur le terminal. (stdout)
	 * 
	 * @param sortie	Chaine de sortie
	 */
	public void sortieConsole(String sortie){
		shell.sortieConsole(sortie);
	}
	
	/**Permet de recevoir les entrées du terminal. (stdin)
	 * 
	 * @param entree	Chaine reçue.
	 */
	abstract public void entreeConsole(String entree);
	
	/**Doit implémenter la terminaison de l'application.
	 * Similaire à un signal SIGKILL.
	 */
	abstract public void controleC();
	
	/**
	 * Permet d'informer au shell que l'application est terminée.
	 */
	public void exit() {
		shell.exitApplication();
	}
}