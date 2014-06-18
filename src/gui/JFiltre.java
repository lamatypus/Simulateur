package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**********************************************************************
 * <p>
 * But:<br>
 * Permet de gérer les filtres à appliquer sur les captures
 * </p><p>
 * Description:<br>
 * Cette classe permet de choisir à l'aide de checkbox les filtres à
 * appliquer aux captures. Ainsi, l'utilisateur aura la possibilité de n'afficher
 * que les paquets qui l'intéresse.
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
public class JFiltre extends JDialog{

	LinkedList<JCheckBox> checkbox = new LinkedList<>();
	
	/**
	 * COnstructeur de la classe
	 * @param c
	 */
	public JFiltre(Component c){
		this.setSize(180, 220);
		this.setUndecorated(false);
		this.setTitle("Filtre captures");
		this.setModal(true);
		this.setBackground(Color.WHITE);
		ImageIcon i = new ImageIcon("img/lama_icon.png");
		this.setIconImage(i.getImage());
		this.setResizable(false);
		
		this.setLocationRelativeTo(c);
		
		
		this.add(new JLabel("Choisir les paquets visibles :"),BorderLayout.NORTH);
		
		JPanel checkPanel = new JPanel(new GridLayout(0, 1));
		for(int index=0;index<JCaptures.paquets.length;index++){
			final int tmpIndex = index;
			JCheckBox tmp = new JCheckBox(JCaptures.paquets[index],JCaptures.filtrePaquet[index]);
			
			tmp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JCaptures.filtrePaquet[tmpIndex] = !JCaptures.filtrePaquet[tmpIndex];
					JCaptures.updateFilter();
				}
			});
			
			checkPanel.add(tmp);
		}
        
        add(checkPanel, BorderLayout.LINE_START);
        
        this.setVisible(true);
	}
}
