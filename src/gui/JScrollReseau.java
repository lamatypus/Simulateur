package gui;

import java.awt.Color;

import javax.swing.JScrollPane;

/**********************************************************************
 * <p>
 * But:<br>
 * Panneau permettant le "scrolling"
 * </p><p>
 * Description:<br>
 * Cette classe permet de g�rer le scrolling sur le panneau r�seau
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
@SuppressWarnings("serial")
public class JScrollReseau extends JScrollPane {
	
	/**
	 * COnstructeur de la classe
	 */
	public JScrollReseau()
	{
		this.setBackground(Color.WHITE);
	}
	
	/**
	 * setViewportView(reseaux)
	 * @param reseaux
	 */
	public void setView(JReseaux reseaux){
		this.setViewportView(reseaux);
	}
	
}
