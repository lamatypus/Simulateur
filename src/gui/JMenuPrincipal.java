package gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import controleur.Simulateur;

/**********************************************************************
 * <p>
 * But:<br>
 * Gestion du menu principal
 * </p><p>
 * Description:<br>
 * Cette classe permet de gérer le menu principal se trouvant 
 * en haut de l'application
 * 
 * fichier
 *	nouveau
 *	ouvrir
 *	enregistre
 *	enregistre Sous 
 *	fermer
 *
 * édition
 *	Exporter les configuration 
 *	Exporter les captures 
 *	Afficher/masquer le nom des interfaces
 *	
 * capture 
 *	filtre
 *	
 * aide 
 *	À propos 
 *	manuel
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
public class JMenuPrincipal extends JMenuBar{

	private Component principal;
	
	JMenu fichier = new JMenu("Fichier");
	JMenuItem nouveau = new JMenuItem("Nouveau");
	JMenuItem ouvrir  = new JMenuItem("Ouvrir");
	JMenuItem enregistre = new JMenuItem("Enregistrer");
	JMenuItem enregistreSous = new JMenuItem("Enregistrer sous");
	JMenuItem fermer =  new JMenuItem("Fermer");
	
	JMenu edition = new JMenu("Edition");
	JMenuItem expConf = new JMenuItem("Exporter les configurations");
	JMenuItem expCap = new JMenuItem("Exporter les captures");
	JMenuItem affInt = new JMenuItem("Afficher/masquer le nom des interfaces");
	
	JMenu capture = new JMenu("Capture");
	JMenuItem filtre = new JMenuItem("Filtre");
	
	JMenu aide = new JMenu("Aide");
	JMenuItem apropos = new JMenuItem("A propos");
	JMenuItem manuel = new JMenuItem("Manuel");
	
	/**
	 * Constructeur de la classe
	 * @param c 
	 */
	public JMenuPrincipal(Component c) {
		principal = c;
		
		fichier.add(nouveau);
		fichier.add(ouvrir);
		fichier.add(enregistre);
		fichier.add(enregistreSous);
		fichier.add(fermer);
		
		edition.add(expConf);
		edition.add(expCap);
		edition.add(affInt);
		
		capture.add(filtre);
		
		aide.add(apropos);
		aide.add(manuel);
		
		nouveau.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				controleSave();
				Simulateur.reset();
			}
		});
		ouvrir.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				controleSave();
				
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileFilter(new FileNameExtensionFilter("Lamatypus file (*.tib)", "tib"));
				
				int ret = fc.showOpenDialog(JMenuPrincipal.this.principal);
				if(ret == JFileChooser.APPROVE_OPTION){
					File file = fc.getSelectedFile();
					
					if(!file.getName().endsWith(".tib")){
						file = new File(file.getAbsolutePath() + ".tib");
					}
					
					Simulateur.open(file);
				}
				
			}
		});
		enregistre.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(Simulateur.file == null){
					dialogSave();
				}
				else{
					Simulateur.save(Simulateur.file);
				}
				
			}
		});
		enregistreSous.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dialogSave();
			}
		});
		fermer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controleSave();
				System.exit(0);	
			}
		});
		expCap.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileFilter(new FileNameExtensionFilter("CSV Documents (*.csv)", "csv"));
				fc.setSelectedFile(new File("report.csv"));
				
				int ret = fc.showSaveDialog(JMenuPrincipal.this.principal);
				if(ret == JFileChooser.APPROVE_OPTION){
					File file = fc.getSelectedFile();
					
					if(!file.getName().endsWith(".csv")){
						file = new File(file.getAbsolutePath() + ".csv");
					}
					
					Simulateur.getJPrincipal().captures.saveCapture(file);
				}
			}
			
		});
		
		expConf.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileFilter(new FileNameExtensionFilter("HTML Documents (*.html)", "html"));
				fc.setSelectedFile(new File("conf.html"));
				
				int ret = fc.showSaveDialog(JMenuPrincipal.this.principal);
				if(ret == JFileChooser.APPROVE_OPTION){
					File file = fc.getSelectedFile();
					
					if(!file.getName().endsWith(".html")){
						file = new File(file.getAbsolutePath() + ".html");
					}
					
					Simulateur.saveConfigHtml(file);
				}
			}
			
		});
				
		affInt.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Simulateur.frame.reseaux.affInt = !Simulateur.frame.reseaux.affInt;
				Simulateur.frame.reseaux.repaint();
			}
		});
		
		apropos.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new JApropos(principal);
				
			}
		});
		
		manuel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new JAide();
				
			}
		});
		
		filtre.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new JFiltre(Simulateur.frame);
				
			}
		});
		
		manuel.setAccelerator(KeyStroke.getKeyStroke("F1"));
		enregistre.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
		nouveau.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_MASK));
		ouvrir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));
		apropos.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,InputEvent.SHIFT_MASK));
		filtre.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,InputEvent.CTRL_MASK));
		
		this.add(fichier);
		this.add(edition);
		this.add(capture);
		this.add(aide);
		
		
	}
	
	/**
	 * Fenêtre de dialogue pour l'enregistrement de la simulation courante.
	 */
	private void dialogSave(){
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(new FileNameExtensionFilter("Lamatypus file (*.tib)", "tib"));
		fc.setSelectedFile(new File("network.tib"));
		
		int ret = fc.showSaveDialog(JMenuPrincipal.this.principal);
		if(ret == JFileChooser.APPROVE_OPTION){
			File file = fc.getSelectedFile();
			
			if(!file.getName().endsWith(".tib")){
				file = new File(file.getAbsolutePath() + ".tib");
			}
			
			Simulateur.save(file);
		}
	}

	/**
	 * Popup s'affichant lors de la fermeture si une modification à eu lieu 
	 * depuis la dernière sauvegarde ou si la simulation courante 
	 * n'a jamais été sauvegardée.
	 */
	public void controleSave(){
		if(Simulateur.modified){
			int reponse = JOptionPane.showConfirmDialog(principal,
				       "Voulez-vous sauvegarder les modifications ?",
				       "Sauvegarde?", 
				        JOptionPane.YES_NO_OPTION);
			
		  if (reponse == JOptionPane.YES_OPTION){
			  if(Simulateur.file == null){
					dialogSave();
				}
				else{
					Simulateur.save(Simulateur.file);
				}
				
		  }
		  else if (reponse == JOptionPane.NO_OPTION){
			  //Rien
		  }
		}
	}
	
}
