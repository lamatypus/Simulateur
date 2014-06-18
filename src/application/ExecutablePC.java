package application;

import elements.config.*;

/**********************************************************************
 * <p>
 * But:<br>
 * D�finit les m�thodes de base pour qu'une application s'interface
 * avec un terminal.
 * </p>
 *
 * @author		Rapha�l Buache
 * @author     	Magali Fr�lich
 * @author     	C�dric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public abstract class ExecutablePC {
	
	private ShellPC shell;
	
	/**Constructeur par d�faut. L'application doit connaitre un terminal.
	 * 
	 * @param s Le terminal
	 */
	public ExecutablePC(ShellPC s) { shell = s; }
	
	/**Permet d'�crire en sortie sur le terminal. (stdout)
	 * 
	 * @param sortie	Chaine de sortie
	 */
	public void sortieConsole(String sortie){
		shell.sortieConsole(sortie);
	}
	
	/**Permet de recevoir les entr�es du terminal. (stdin)
	 * 
	 * @param entree	Chaine re�ue.
	 */
	abstract public void entreeConsole(String entree);
	
	/**Doit impl�menter la terminaison de l'application.
	 * Similaire � un signal SIGKILL.
	 */
	abstract public void controleC();
	
	/**
	 * Permet d'informer au shell que l'application est termin�e.
	 */
	public void exit() {
		shell.exitApplication();
	}
}