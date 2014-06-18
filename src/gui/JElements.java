package gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import elements.Hub;
import elements.PC;
import elements.Routeur;
import elements.Serveur;
import elements.Switch;

/**********************************************************************
 * <p>
 * But:<br>
 * Liste des éléments disponibles
 * </p><p>
 * Description:<br>
 * Cette classe permet de d'ajouter dans la partie gauche de l'application la
 * liste des éléments réseaux ainsi que leurs configuration (comme le nombre de ports
 * et leurs nom affichés à gauche)
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
@SuppressWarnings("serial")
public class JElements extends JScrollPane{

	/**
	 * Constructeur de la classe
	 */
	public JElements() {
		this.setMinimumSize(new Dimension(120, 200));
		JPanel elemPan = new JPanel();
		elemPan.setBackground(Color.WHITE);
		elemPan.setMinimumSize(new Dimension(120, 100));
		elemPan.setLayout(new BoxLayout(elemPan, BoxLayout.PAGE_AXIS));
		
		elemPan.add(new jElementCreate("/img/pc_64.png",new PC(1, true),"PC"));
		elemPan.add(new jElementCreate("/img/hub_64.png",new Hub(4, true), "Hub 4 ports"));
		elemPan.add(new jElementCreate("/img/hub_64.png",new Hub(8, true), "Hub 8 ports"));
		elemPan.add(new jElementCreate("/img/switch_64.png",new Switch(4, true), "Switch 4 ports"));
		elemPan.add(new jElementCreate("/img/switch_64.png",new Switch(8, true), "Switch 8 ports"));
		elemPan.add(new jElementCreate("/img/routeur_64.png",new Routeur(2, true), "Routeur 2 ports"));
		elemPan.add(new jElementCreate("/img/routeur_64.png",new Routeur(4, true), "Routeur 4 ports"));
		elemPan.add(new jElementCreate("/img/serveur_64.png",new Serveur(1, true), "Serveur"));
		
		this.setViewportView(elemPan);	
		this.setBackground(Color.WHITE);
	}
	
}
