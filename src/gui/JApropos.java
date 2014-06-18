package gui;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe de la popup "À propos"
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet d'afficher un popup "À propos" 
 * avec la possibilité de se rendre sur le site web lamatypus.tk
 * </p>
 * 
 * @author Raphaël Buache
 * @author Magali Frölich
 * @author Cédric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class JApropos extends JDialog{

	/**
	 * Constructeur de la classe
	 * @param c Composent parent
	 * le popup serra placé en fonction de ce composent
	 */
	public JApropos(Component c){
		
		this.setSize(300, 150);
		this.setUndecorated(false);
		this.setModal(true);
		this.setTitle("A propos");
		
		this.setBackground(Color.WHITE);
		ImageIcon i = new ImageIcon(getClass().getResource("/img/lama_icon.png"));
		this.setIconImage(i.getImage());
		this.setResizable(false);
		
		JPanel panel = new JPanel();
		this.add(panel);
		panel.setLayout(null);
		
		JLabel logo = new JLabel(new ImageIcon(new ImageIcon(getClass().getResource("/img/lama_icon.png")).getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
		JLabel titre = new JLabel("Lamatypus \u00a9  V1.0");
		JLabel stitre = new JLabel("Simulateur de réseaux informatiques");
		JLabel credit = new JLabel("Developped by:");
		JLabel credit2 = new JLabel("Buache Raphaël, Fröhlich Magali");
		JLabel credit3 = new JLabel("Malherbe Yann, Rudareanu Cédric");
		JLabel site = new JLabel("<html><a href=\"\">http://www.lamatypus.tk</a>");
		
		titre.setFont(new Font("Serif", Font.BOLD, 20));
		stitre.setFont(new Font("Serif", Font.BOLD, 10));
		credit.setFont(new Font("Serif", Font.BOLD, 12));
		credit2.setFont(new Font("Serif", Font.ITALIC, 10));
		credit3.setFont(new Font("Serif", Font.ITALIC, 10));
		site.setFont(new Font("Serif", Font.BOLD, 10));
		
		logo.setBounds(0, 0, 120, 120);
		titre.setBounds(110, 10, 200, 25);
		stitre.setBounds(110, 30, 200, 20);
		credit.setBounds(150, 60, 150, 16);
		credit2.setBounds(122, 64, 200, 40);
		credit3.setBounds(120, 74, 200, 40);
		site.setBounds(182, 108, 200, 14);
		
		panel.add(logo);
		panel.add(titre);
		panel.add(stitre);
		panel.add(credit);
		panel.add(credit2);
		panel.add(credit3);
		panel.add(site);
		
		goWebsite(site);
		   
		this.setLocationRelativeTo(c);
		this.setVisible(true);
	}
	
	/**
	 * Lance le navigateur par défaut de l'utilisateur à l'adresse
	 * passée en paramètre
	 * @param website
	 */
	private void goWebsite(JLabel website) {
		this.setAlwaysOnTop(false);
		
		website.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					
					Desktop.getDesktop().browse(new URI("http://www.lamatypus.tk"));
				} catch (URISyntaxException ex) {
					//It looks like there's a problem
				}
				 catch (IOException ex) {
					//It looks like there's a problem
				}
			}
		});
	}
}
