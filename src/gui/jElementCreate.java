package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import controleur.Simulateur;
import elements.ElementReseau;

/**********************************************************************
 * <p>
 * But:<br>
 * Permet de gérer la création d'un élément
 * </p><p>
 * Description:<br>
 * Cette classe permet de gérer la création d'un élément et son
 * déplacement de la liste des éléments de gauche jusqu'à son emplacement
 * dans l'espace de travail central.
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
public class jElementCreate extends JLabel  {
	
	private boolean dragging = false;
	
	private static final String HTML = 
	        "<html>" +
	        "<style type'text/css'>" +
	        "body, html { padding: 0px; margin: 0px; }" +
	        "</style>" +
	        "<body>" +
	        "<div style='width:500px;height:500px;'></div>"+
	        "</body>";
	
	
	/**
	 * Constructeur de la classe
	 * @param img Chemin de l'image de l'élément
	 * @param e Élément
	 * @param name Nom de l'élément dans la liste de gauche
	 */
	public jElementCreate(final String img,final ElementReseau e, final String name)
	{
				
		super(HTML);
		setAlignmentX(CENTER_ALIGNMENT);
		setLayout(new FlowLayout(FlowLayout.CENTER));
		
		
		JLabel label = new JLabel(name);
		JLabel pic = new JLabel();
		pic.setIcon(new ImageIcon(getClass().getResource(img)));
		label.setHorizontalAlignment(JLabel.CENTER);
		
		int maxWidth = (pic.getPreferredSize().width > label.getPreferredSize().width ? pic.getPreferredSize().width:label.getPreferredSize().width);
		label.setPreferredSize(new Dimension(maxWidth, label.getPreferredSize().height));
		int height = pic.getPreferredSize().height+label.getPreferredSize().height+20;
		
		this.setPreferredSize(new Dimension(maxWidth, height));
		setBorder(BorderFactory.createLineBorder(Color.black, 1));
		this.add(label);
		this.add(pic);
	
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if(Simulateur.frame.dragElelemt && dragging)
					Simulateur.frame.release();
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				((JComponent)jElementCreate.this.getParent()).scrollRectToVisible(jElementCreate.this.getBounds());
			}
			
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			
			@Override
			public void mouseDragged(MouseEvent arg0) {
				
				if(JDragElement.lastDroped)
				{
					JDragElement.lastDroped  = false;
					Simulateur.frame.addElement(img, e);
					//Simulateur.frame.reseaux.repaint();
				}
				if(Simulateur.frame.dragElelemt)
				{
					dragging = true;
					Simulateur.frame.move();
				}
					
				
			}
		});
	}

}
