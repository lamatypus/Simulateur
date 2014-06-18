package gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import controleur.Simulateur;

/**********************************************************************
 * <p>
 * But:<br>
 * Gestion des outils
 * </p><p>
 * Description:<br>
 * Cette classe permet de gérer les outils se trouvant 
 * en haut de l'application
 * 
 * Mode normale
 * Mode connexion
 * Mode suppression
 * Reset de la simulation.
 * 
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
public class JOutils extends JToolBar {
	MouseAdapter adaptMouse;
	
	public JOutils() {
		JButton souris = new JButton(new ImageIcon(new ImageIcon(
				getClass().getResource("/img/mouse3.png")).getImage().getScaledInstance(20, 20,
				Image.SCALE_SMOOTH)));
		JButton cable = new JButton(new ImageIcon(new ImageIcon(
				getClass().getResource("/img/connect.png")).getImage().getScaledInstance(20, 20,
				Image.SCALE_SMOOTH)));
		JButton supp = new JButton(new ImageIcon(new ImageIcon(
				getClass().getResource("/img/delete3.png")).getImage().getScaledInstance(20, 20,
				Image.SCALE_SMOOTH)));
		JButton reset = new JButton(new ImageIcon(new ImageIcon(
				getClass().getResource("/img/restart1.png")).getImage().getScaledInstance(20, 20,
				Image.SCALE_SMOOTH)));

		souris.setFocusable(false);
		cable.setFocusable(false);
		supp.setFocusable(false);
		reset.setFocusable(false);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit(); 
		Image deleteIcon = toolkit.getImage(getClass().getResource("/img/icon-delete.png"));
		Image connectIcon = toolkit.getImage(getClass().getResource("/img/connect.png"));
		Point hotSpotCenter = new Point(8,8);
		final Cursor cursorDelete = toolkit.createCustomCursor(deleteIcon, hotSpotCenter, "Delete");
		final Cursor cursorConnect = toolkit.createCustomCursor(connectIcon, hotSpotCenter, "Connect");
		
		adaptMouse = new MouseAdapter() {

			public void mousePressed(MouseEvent arg0) {
				Simulateur.frame.reseaux.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				JReseaux.delete = false;
				JReseaux.link = false;
				JDragElement.srcLink = null;
			}
		};
		souris.addMouseListener(adaptMouse);
		
		souris.registerKeyboardAction(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOutils.this.adaptMouse.mousePressed(null);
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		supp.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent arg0) {
				Simulateur.frame.reseaux
						.setCursor(cursorDelete);
				JReseaux.delete = true;
				JReseaux.link = false;
			}
		});

		cable.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent arg0) {
				Simulateur.frame.reseaux
						.setCursor(cursorConnect);
				JReseaux.delete = false;
				JReseaux.link = true;
				JDragElement.srcLink = null;
			}
		});
		
		reset.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent arg0) {
				JReseaux.delete = false;
				JReseaux.link = false;
				JDragElement.srcLink = null;
				Simulateur.frame.menu.controleSave();
				Simulateur.reset();
			}
		});
		
		souris.setToolTipText("Sélection");
		cable.setToolTipText("Connection");
		supp.setToolTipText("Supprimer");
		reset.setToolTipText("Reset");

		this.add(souris);
		this.add(Box.createHorizontalStrut(10));
		this.add(cable);
		this.add(Box.createHorizontalStrut(10));
		this.add(supp);
		this.add(Box.createHorizontalStrut(10));
		this.add(reset);

		this.setFloatable(false);

		this.setBackground(Color.WHITE);
		this.setBorderPainted(false);
		this.setMargin(new Insets(3, 0, 0, 5));
	}

	@Override
	protected void paintComponent(Graphics g) {
		// Create the 2D copy
		Graphics2D g2 = (Graphics2D) g.create();

		// Apply vertical gradient
		g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight(),
				Color.LIGHT_GRAY));
		g2.fillRect(0, 0, getWidth(), getHeight());

		// Dipose of copy
		g2.dispose();
	}

}