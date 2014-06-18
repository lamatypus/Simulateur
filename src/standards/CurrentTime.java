package standards;

/**********************************************************************
 * <p>
 * But:<br>
 * Récupérer le temps écoulé depuis le lancement de l'application
 * </p><p>
 * Description:<br>
 * Cette classe permet de gérer et récupérer le temps depuis le lancement de l'application
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
public class CurrentTime{

	long startTime;
	private static CurrentTime time;
	
	/**
	 * Constructeur de la classe
	 */
	private CurrentTime(){
		
	}
	
	/**
	 * Initialise le temps de début avec le temps actuel
	 */
	public void init(){
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * @return Temps écoulé entre l'appel de init() et getTime()
	 */
	public long getTime(){
		return System.currentTimeMillis() - startTime;
	}

	@Override
	public String toString(){
		return Long.toString(getTime());
	}
	
	/**
	 * @return une instance de la classe
	 */
	public static CurrentTime getInstance(){
		if(time == null){
			time = new CurrentTime();
		}
		return time;
	}
	
}
