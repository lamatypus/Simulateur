package gui;

/**********************************************************************
 * <p>
 * But:<br>
 * Permet toutes les interactions "graphiques" avec un élément
 * </p><p>
 * Description:<br>
 * Cette classe permet de gérer le Drag&Drop des éléments réseaux ainsi
 * que les différents menu lors du clic droit sur un élément.
 * 
 * Cette classe permet aussi la gestion du clic gauche de la souris 
 * sur l'élément et ainsi afficher les informations ou ouvrir la 
 * fenêtre de configuration de ce dernier.
 * 
 * Cette classe contient l'image de l'élément ainsi que sa position,
 * elle serra nécessaire pour dessiner les liaisons entre les différents éléments
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import controleur.Simulateur;
import elements.ElementReseau;
import elements.ElementReseau.Interface;

public class JDragElement extends JLabel implements Serializable{

	private static final long serialVersionUID = 4656794062177016692L;
	private volatile int draggedAtX, draggedAtY;
	public boolean dropped;
	public static boolean lastDroped = true;
	public static int visibility = 0;
	public static JDragElement srcLink = null;
	public static int srcNumInt;
	protected ElementReseau elem;
	protected LinkedList<JDragElement[]> connectedElements = new LinkedList<JDragElement[]>();
	private final Interface[] iListe;
	private JLabel label = new JLabel("");
	
	private static final String HTML = 
	        "<html>" +
	        "<style type'text/css'>" +
	        "body, html { padding: 0px; margin: 0px; }" +
	        "</style>" +
	        "<body>" +
	        "<div style='width:500px;height:500px;'></div>"+
	        "</body>";

	/**
	 * Constructeur de la classe avec une image et un élément réseau
	 * @param img Chemin de l'image
	 * @param e Élément réseau
	 */
	public JDragElement(String img, ElementReseau e) {
		this(img, e, 0, 0);
	}
	
	/**
	 * Constructeur de la classe avec une image, un élément réseau
	 * ainsi que la position que l'élément devra prendre en x et y
	 * @param img Chemin de l'image
	 * @param e Élément réseau
	 * @param x 
	 * @param y
	 */
	public JDragElement(String img, ElementReseau e, int x, int y)
	{
		super(HTML);
		
		elem = e;
		elem.setParent(this);
		Simulateur.elements.add(this);

		iListe = elem.getInterface();
		
		setAlignmentX(CENTER_ALIGNMENT);
		setLayout(new FlowLayout(FlowLayout.CENTER));
		
		label.setText(elem.getInfo());
		JLabel pic = new JLabel();
		pic.setIcon(new ImageIcon(getClass().getResource(img)));
		label.setHorizontalAlignment(JLabel.CENTER);
		
		int maxWidth = (pic.getPreferredSize().width > label.getPreferredSize().width ? pic.getPreferredSize().width:label.getPreferredSize().width);
		label.setPreferredSize(new Dimension(maxWidth, label.getPreferredSize().height));
		int height = pic.getPreferredSize().height+label.getPreferredSize().height+20;
		
		this.setPreferredSize(new Dimension(maxWidth, height));
		this.add(label);
		this.add(pic);
		
		setBounds(0, 0, this.getPreferredSize().width,
				this.getPreferredSize().height);

		this.setLocation(x, y-label.getPreferredSize().height);
		addMouseListener(new Mouse());
		addMouseMotionListener(new MouseMotion());
	}
	
	@Override
	public void repaint(){
		if(elem != null){
			this.label.setText(elem.getInfo());
		}
		//super.repaint();
	}
	
	/**
	 * Permet d'obtenir l'élément représenter par le JDragElement
	 * 
	 * @return l'élément
	 */
	public ElementReseau getElement (){
		return elem;
	}
	
	/**********************************************************************
	 * <p>
	 * But:<br>
	 * Classe pour la gestion de la sourie sur un élément
	 * </p>
	 * <p>
	 * Description:<br>
	 * Cette classe permet de gérer les différentes interactions de la souris
	 * sur un élément. Elle doit être rajoutée comme un listener sur un élément
	 * </p>
	 * 
	 * @author Raphaël Buache
	 * @author Magali Frölich
	 * @author Cédric Rudareanu
	 * @author Yann Malherbe
	 * @version 1.0
	 * @modify 18.06.2014
	 ***********************************************************************/
	@SuppressWarnings("serial")
	class Mouse extends MouseAdapter implements Serializable{
		
			public void mousePressed(MouseEvent e) {
				Simulateur.modified = true;
				draggedAtX = e.getX();
				draggedAtY = e.getY();
				Simulateur.frame.info.setInfo(JDragElement.this.elem.allInfo());
				((JReseaux) getParent()).setLayer(JDragElement.this,
						++visibility);
			}

			public void mouseClicked(MouseEvent e) {
				int nbClics = e.getClickCount();
				int button = e.getButton();
				
				if (!JReseaux.link && !JReseaux.delete) {
					if (nbClics == 2 && button == MouseEvent.BUTTON1) {
						elem.config();
						
					} 
					else if (nbClics == 1 && button == MouseEvent.BUTTON3) {
						ActionListener actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent actionEvent) {
								int numint = Integer.parseInt(actionEvent
										.getActionCommand().substring(
												actionEvent.getActionCommand()
														.length() - 1));
								ICapture i = Simulateur.frame.captures
										.ajouteTab(JDragElement.this.elem
												.getInfo() +" " + iListe[numint].getNom());
								(iListe[numint]).setAnalyse(i);
							}
						};

						JPopupMenu popupMenu = new JPopupMenu();
						JMenu capture = new JMenu("Captures");
						for (int i = 0; i < iListe.length; i++) {
							String connected = iListe[i].getInterfaceDest() == null ? "": "(connected) ";
							JMenuItem item = new JMenuItem(connected+iListe[i].getNom());
							capture.add(item);
							item.addActionListener(actionListener);
						}
						popupMenu.add(capture);
						popupMenu.show(e.getComponent(), e.getX(), e.getX());

					}

				} else if (JReseaux.link && nbClics == 1) {
					
					if(button == MouseEvent.BUTTON1)
					{
						ActionListener actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent actionEvent) {
								int numint = Integer.parseInt(actionEvent
										.getActionCommand().substring(
												actionEvent.getActionCommand()
														.length() - 1));
								
								if(iListe[numint].getInterfaceDest() != null)
								{
									Interface tmp = iListe[numint].getInterfaceDest(); 
									
									
									for(JDragElement[] jd : connectedElements)
									{
										if(jd[0] != JDragElement.this)
										{
											if(Arrays.asList(jd[0].iListe).contains(tmp))
											{
												jd[0].connectedElements.remove(jd);
												Simulateur.connectedElements.remove(jd);
												connectedElements.remove(jd); 
												break;
											}
										}
										else if(jd[1] != JDragElement.this)
										{
											if(Arrays.asList(jd[1].iListe).contains(tmp))
											{
												jd[1].connectedElements.remove(jd);
												Simulateur.connectedElements.remove(jd);
												connectedElements.remove(jd);
												break;
											}
										}
										
	
									}
									
									Simulateur.frame.reseaux.repaint();
									ElementReseau.disconnect(tmp.getParent(), iListe[numint].getParent(), tmp.getNumero(), iListe[numint].getNumero());								
									
								}
	
								if (srcLink == null) {
									srcLink = JDragElement.this;
									srcNumInt = numint;
								} else if (!(srcLink == JDragElement.this))
								{
									ElementReseau.connect(srcLink.elem, elem, srcNumInt, numint);
	
									JDragElement[] liaison = new JDragElement[] { srcLink, JDragElement.this };
									connectedElements.add(liaison);
									srcLink.connectedElements.add(liaison);
									
									Simulateur.connectedElements.add(liaison);
									Simulateur.frame.reseaux.repaint();
	
									srcLink = null;
								}
								else
								{
									srcLink = null;
									JOptionPane.showMessageDialog(Simulateur.frame, "Ne peut pas se connecter à lui-même");
								}
							
	
							}
						};
	
						//System.out.println("menu choix interfaces");
						JPopupMenu popupMenu = new JPopupMenu();
						for (int i = 0; i < iListe.length; i++) {
							if(iListe[i].getInterfaceDest() == null) {
							String connected = iListe[i].getInterfaceDest() == null ? "": "(connected) ";
							JMenuItem item = new JMenuItem(connected + iListe[i].getNom());
							popupMenu.add(item);
							item.addActionListener(actionListener);
							}
						}
	
						popupMenu.show(e.getComponent(), e.getX(), e.getX());
					}
					else if(button == MouseEvent.BUTTON3) {
						
						
						
						ActionListener actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent actionEvent) {
								int numint = Integer.parseInt(actionEvent.getActionCommand().substring(actionEvent.getActionCommand().length() - 1));
								ElementReseau.disconnect(iListe[numint].getParent(), iListe[numint].getInterfaceDest().getParent(), numint, iListe[numint].getInterfaceDest().getNumero());
								Simulateur.frame.reseaux.repaint();
							
							}
						};

						JPopupMenu popupMenu = new JPopupMenu();
						JMenu disconnect = new JMenu("Déconnexion");
						for (int i = 0; i < iListe.length; i++) {
							
							if(iListe[i].getInterfaceDest() != null) {
							JMenuItem item = new JMenuItem("(connected) "+iListe[i].getNom());
							disconnect.add(item);
							item.addActionListener(actionListener);
							}
						}
						popupMenu.add(disconnect);
						popupMenu.show(e.getComponent(), e.getX(), e.getX());
						
						
						
					}
					
				

				}
				else if (JReseaux.delete && nbClics == 1 && button == MouseEvent.BUTTON1) {
					// lastDroped = true;
					Simulateur.frame.reseaux.remove(JDragElement.this);
					Simulateur.elements.remove(JDragElement.this);
					Simulateur.frame.reseaux.scroll();
					
//					for(Interface i : iListe)
//					{
//						Interface tmp;
//						if((tmp = i.getInterfaceDest()) != null)
//						{
//							//TODO
//							ElementReseau.disconnect(i.getParent(), tmp.getParent(), i.getNumero(), tmp.getNumero());
//						}
//					}
					
					elem.destroy();
					
					
					for(JDragElement[] jd : connectedElements)
					{
						Simulateur.connectedElements.remove(jd);
						if(jd[0] != JDragElement.this)
						{
							jd[0].connectedElements.remove(jd);
						}
						else if(jd[1] != JDragElement.this)
						{
							jd[1].connectedElements.remove(jd);
						}
							
					}
					
					Simulateur.frame.reseaux.repaint();
					return;

				}

			}
		}

	/**********************************************************************
	 * <p>
	 * But:<br>
	 * Classe pour la gestion de la souris sur un élément
	 * </p>
	 * <p>
	 * Description:<br>
	 * Cette classe permet de gérer les mouvements de la souris (Drag&Drop)
	 * </p>
	 * 
	 * @author Raphaël Buache
	 * @author Magali Frölich
	 * @author Cédric Rudareanu
	 * @author Yann Malherbe
	 * @version 1.0
	 * @modify 18.06.2014
	 ***********************************************************************/
	@SuppressWarnings("serial")
	class MouseMotion extends MouseMotionAdapter implements Serializable{
			public void mouseDragged(MouseEvent e) {
				
				if (!JReseaux.link && !JReseaux.delete) {
					if (!dropped)
						lastDroped = (dropped = true);
					
					move(e);
					Simulateur.frame.reseaux.scroll();
					((JComponent)JDragElement.this.getParent()).scrollRectToVisible(JDragElement.this.getBounds());
				}

			}
		}

	/**
	 * Déplace un élément avec la souris
	 * @param e
	 */
	private void move(MouseEvent e) {
		int currentDragX = (e.getX() - draggedAtX + getLocation().x);
		int currentDragY = (e.getY() - draggedAtY + getLocation().y);

		currentDragX = currentDragX < 0 ? 0 : currentDragX;
		currentDragY = currentDragY < 0 ? 0 : currentDragY;

		currentDragX = currentDragX > getParent().getSize().width
				- getSize().width ? getParent().getSize().width
				- getSize().width : currentDragX;
		currentDragY = currentDragY > getParent().getSize().height
				- getSize().height ? getParent().getSize().height
				- getSize().height : currentDragY;

		setLocation(currentDragX, currentDragY);
	}
}
