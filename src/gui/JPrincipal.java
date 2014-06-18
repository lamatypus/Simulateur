package gui;

import elements.ElementReseau;

import java.awt.*;
import java.awt.event.WindowEvent;

import javax.swing.*;



/**********************************************************************
 * <p>
 * But:<br>
 * Fen�tre principale de l'application
 * </p><p>
 * Description:<br>
 * Cette classe permet d'afficher la fen�tre principale de l'application.
 * Elle g�re aussi le d�placement d'�l�ments pas encore cr�es 
 * lors du drag & drop de la liste de gauche jusqu'� l'espace de travail � droite.
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
public class JPrincipal extends JFrame{
	
	JMenuPrincipal menu = new JMenuPrincipal(this);
	JOutils outils = new JOutils();
	
	
	public JScrollReseau scrollReseau = new JScrollReseau();
	public JReseaux reseaux = new JReseaux();
	public JElements elements = new JElements();
	
	public JLabel newElement;
	public boolean dragElelemt = false;
	
	
	private volatile int draggedAtX, draggedAtY;
	private ElementReseau tempElement;
	private String tempImg;
	private JSplitPane separation;
	
	public JInformations info = new JInformations(this);
	JCaptures captures = new JCaptures();
	
	/**
	 * Constructeur de la classe
	 */
	public JPrincipal(){
		scrollReseau.setView(reseaux);
		
		newElement = new JLabel();
		this.setSize(800, 600);
		this.setUndecorated(false);
		
		this.setTitle("Lamatypus - Simulateur de r�seaux informatiques");
	
		ImageIcon i = new ImageIcon(getClass().getResource("/img/lama_icon.png"));
		this.setIconImage(i.getImage());
		
		this.setJMenuBar(menu);
		this.getContentPane().add(outils,BorderLayout.NORTH);
		
		JSplitPane hautConfig = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, elements, scrollReseau);
		JSplitPane basConfig = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, info, captures);
		separation = new JSplitPane(JSplitPane.VERTICAL_SPLIT, hautConfig, basConfig);
		hautConfig.setDividerSize(0);
		basConfig.setDividerSize(5);
		separation.setDividerSize(5);
		
		basConfig.setBackground(Color.WHITE);
		separation.setDividerLocation(350);
		basConfig.setDividerLocation(175);
		separation.setResizeWeight(1.0);
		
		
		this.add(separation);
		
		this.setMinimumSize(new Dimension(500, 300));
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		this.setVisible(true);
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent windowEvent) {
		    	menu.controleSave();
		    }
		});
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);		
	}


	/**
	 * Permet de d�placer un �l�ment pas encore cr�e
	 */
	public void move() {
		try
		{
			Point p = this.getMousePosition();
			int currentDragX, currentDragY;
			
			currentDragX = p.x-separation.getX();
			currentDragY = p.y-separation.getY();
			
			if((currentDragX - newElement.getWidth()/2 > 0 && currentDragY-newElement.getHeight() >0 )
					&&
				(currentDragX+newElement.getWidth()-10 < this.getWidth() && currentDragY+newElement.getHeight()+5 < this.getHeight()-captures.getHeight()))
			{
				draggedAtX = currentDragX - newElement.getWidth()/2;
				draggedAtY = currentDragY - newElement.getHeight()/2-20;
			}

		}
		catch (NullPointerException e)
		{
			
		}
		
		
		newElement.setLocation(draggedAtX, draggedAtY);

		//newElement.repaint();
		JPrincipal.this.repaint();
		
	}
	
	/**
	 * Cr�e l'�l�ment en cours de d�placement 
	 */
	public void release()
	{
		if(dragElelemt)
		{
			dragElelemt = false;
			
			this.remove(newElement);
			this.remove(separation);
			this.add(separation);
			this.repaint();
			JDragElement.lastDroped = true;

			int reseauStartX = reseaux.getLocationOnScreen().x-this.getLocationOnScreen().x;
			
			if(draggedAtX + newElement.getWidth()/2 > reseauStartX)
			{
				reseaux.add(new JDragElement(tempImg,tempElement.newElem(), draggedAtX-elements.getWidth(), draggedAtY-40), ++(JDragElement.visibility), 0);
			}

		}
	}
	
	/**
	 * Cr�e un �l�ment "fant�me" temporaire pouvant se d�placer sur toute la fen�tre principale.
	 * Cette m�thode est n�cessaire pour pouvoir faire du drag & drop entre deux composants swing
	 * @param img Chemin de l'image de l'�l�ment
	 * @param e �l�ment � cr�er
	 */
	public void addElement(String img, ElementReseau e)
	{
		Point p = JPrincipal.this.getMousePosition();
		tempElement = e;
		tempImg = img;
		
		draggedAtX = p.x-40;
		draggedAtY = p.y-100;
		
		
		newElement.setIcon(new ImageIcon(getClass().getResource(img)));
		newElement.setBounds(0, 0, newElement.getPreferredSize().width, newElement.getPreferredSize().height);
		dragElelemt = true;
		this.add(newElement, 1);
		newElement.setLocation(draggedAtX, draggedAtY);
		scrollReseau.getVerticalScrollBar().setValue(0);
		scrollReseau.getHorizontalScrollBar().setValue(0);
		this.repaint();
		
	}
}
