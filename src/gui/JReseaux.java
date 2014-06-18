package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.JLayeredPane;

import controleur.Simulateur;
import elements.ElementReseau;
import elements.ElementReseau.Interface;

/**********************************************************************
 * <p>
 * But:<br>
 * Gestion du panneau réseau
 * </p><p>
 * Description:<br>
 * Cette classe permet de gérer le panneau réseau sur lequel les éléments
 * seront placés. 
 * 
 * Le panneau doit être redimensionnement et "scrollable"
 * C'est sur le panneau réseau que serra dessiné les liaisons entre les éléments.
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
@SuppressWarnings("serial")
public class JReseaux extends JLayeredPane{

	public static boolean delete = false;
	public static boolean link = false;
	private int maxX, maxY;
	public boolean affInt = false;
	
	/**
	 * Constructeur de la classe
	 */
	public JReseaux() {
		
		maxX = getWidth();
		maxY = getHeight();
		this.setOpaque(true);
		this.setBackground(Color.WHITE);
		
//		this.setPreferredSize(new Dimension(2000, 1000));
		
		addComponentListener(new ComponentAdapter() {
			
			public void componentResized(ComponentEvent e) {
				scroll();
			}

		});
				
	}
	
	/**
	 * Gestion de la taille du panneau réseau en fonction de la position des éléments.
	 */
	public void scroll()
	{
//		maxX = maxY = 0;
		int tmpMaxX = 0, tmpMaxY = 0;
		for(JDragElement jElem: Simulateur.elements)
		{
			tmpMaxX = jElem.getX() + jElem.getWidth()+10 > tmpMaxX ? jElem.getX() + jElem.getWidth()+10:tmpMaxX;
			tmpMaxY = jElem.getY() + jElem.getHeight()+10 > tmpMaxY ? jElem.getY() + jElem.getHeight()+10:tmpMaxY;
			
		}
		if(tmpMaxX != maxX || tmpMaxY !=maxY)
		{
			maxX = tmpMaxX;
			maxY = tmpMaxY;
			this.setPreferredSize(new Dimension(maxX, maxY));
			this.revalidate();
			Simulateur.frame.scrollReseau.repaint();
		}
		else
		{
			this.repaint();
		}
		//Simulateur.frame.scrollReseau.repaint();
		//System.out.println(maxX + " : " + maxY);
	}
	@Override
	public void repaint()
	{
		if(Simulateur.frame != null)
			Simulateur.frame.repaint();
	}
	
	/**
	 * Dessin des traits reliant les éléments ainsi que les ronds représentants l'état de la liaison
	 */
	public void paintComponent (Graphics g){
		//super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int x1, x2,
			y1, y2,
			xp1, yp1,
			xp2, yp2;
		
		int distPoint = 45,
			radius = 8,
			thickness = 2;
		
		if(affInt)
			distPoint*=1.5;
		
		LinkedList<Interface> drawnInterfaces = new LinkedList<Interface>();
		
		LinkedList<Interface> loopedInterfaces = new LinkedList<Interface>();
		LinkedList<Interface> normalInterfaces = new LinkedList<Interface>();
		
		double dist;
		
		
		
		for(JDragElement jElem: Simulateur.elements) {
			
			jElem.repaint();
			Interface[] it = jElem.elem.getInterface();
			for(Interface i : it) {
				
				if(i.getInterfaceDest() != null) {
				
					if(!drawnInterfaces.contains(i)) {
						drawnInterfaces.add(i);
						
						// check if loop
						ElementReseau tmp = i.getInterfaceDest().getParent();
						for(int x = Arrays.asList(it).indexOf(i)+1; x < it.length; x++) {
							if(it[x].getInterfaceDest() != null)
							{
								if(it[x].getInterfaceDest().getParent().equals(tmp)) {
									if(!loopedInterfaces.contains(i))
										loopedInterfaces.add(i);
									loopedInterfaces.add(it[x]);
									normalInterfaces.add(it[x]);
									drawnInterfaces.add(it[x]);
									
								}
							}
						}
						
						
						normalInterfaces.add(i);

						//dessine
						
						float offset = (-radius-thickness/2)*loopedInterfaces.size()/2;
						for(Interface di: normalInterfaces) {
							drawnInterfaces.add(di.getInterfaceDest());
							
							JDragElement Elem1 = di.getParent().getParent();
							JDragElement Elem2 = di.getInterfaceDest().getParent().getParent();
							
							
							x1 = Elem1.getX() + (int)Math.floor(Elem1.getSize().getWidth()/2);
							y1 = Elem1.getY() + (int)Math.floor(Elem1.getSize().getHeight()/2);
							 
							x2 = Elem2.getX() + (int)Math.floor(Elem2.getSize().getWidth()/2);
							y2 = Elem2.getY() + (int)Math.floor(Elem2.getSize().getHeight()/2);
							 
							
							if(loopedInterfaces.contains(di))
							{
								
								int angle = (int) Math.abs(Math.atan2(y2 - y1, x2 - x1) * 180 / Math.PI);
								if((angle >= 0 && angle <= 45) || (angle >= 135 && angle <= 180)) {
									
									y1 -= offset;
									y2 -= offset;
								}
								else
								{
									x1 -= (offset+2*thickness);
									x2 -= (offset+2*thickness);
								}
								
								offset += radius;
								
							}
							
							dist = Math.sqrt(Math.pow(x2-x1, 2)+Math.pow(y2-y1, 2));
							 
							xp1 = (int) ((x2-x1)*(distPoint/dist)+x1);
							yp1 = (int) ((y2-y1)*(distPoint/dist)+y1);
							 
							xp2 = (int) ((x1-x2)*(distPoint/dist)+x2);
							yp2 = (int) ((y1-y2)*(distPoint/dist)+y2);
							
							g2d.setColor(Color.BLACK);
							g2d.setStroke ( new BasicStroke ( thickness ) );
							g2d.draw(new Line2D.Double(x1, y1, x2, y2));
							
							
							if(dist > 2* distPoint)
							{
								if(di.isActive())
									g2d.setColor(Color.GREEN);
								else
									g2d.setColor(Color.RED);
								
								if(di.isGreen())
									g2d.setColor(Color.GREEN);
								else
									g2d.setColor(Color.RED);
								
								g2d.fill(new Ellipse2D.Double(xp1 - (radius-thickness)/2, yp1 - (radius-thickness)/2, radius, radius));
								 
								if(di.getInterfaceDest().isActive() || di.getInterfaceDest().isGreen())
									g2d.setColor(Color.GREEN);
								else
									g2d.setColor(Color.RED);
			
								if(di.getInterfaceDest().isGreen())
									g2d.setColor(Color.GREEN);
								else
									g2d.setColor(Color.RED);
								
								 g2d.fill(new Ellipse2D.Double(xp2 - (radius-thickness)/2, yp2 - (radius-thickness)/2, radius, radius));
								 
								 
								 if(affInt)
									{
										g2d.setColor(Color.BLUE.darker());
										g2d.drawString(di.getNom(), xp1, yp1);
										g2d.drawString(di.getInterfaceDest().getNom(), xp2, yp2);
									}
							}
							
						}

					}
				
					loopedInterfaces.clear();
					normalInterfaces.clear();
				}
			}

		}
		
	}
}
