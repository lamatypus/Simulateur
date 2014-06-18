package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.markdown4j.Markdown4jProcessor;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe Affiche une fenêtre d'aide
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet d'afficher une fenêtre d'aide contenant la 
 * documentation au format .md
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
public class JAide extends JFrame{
	
	JEditorPane p = new JEditorPane();
	static boolean actif = false;
	
	/**
	 * Constructeur de la classe.
	 * Définit une fenêtre de 550x600 pixels
	 */
	public JAide(){
		if(actif == true){
			return;
		}
		actif=true;
		
		
		this.setUndecorated(false);
		
		this.setTitle("Aide");
		
		this.setBackground(Color.WHITE);
		ImageIcon i = new ImageIcon(getClass().getResource("/img/lama_icon.png"));
		
		this.setIconImage(i.getImage());
		this.setResizable(true);
		
		

		p.setContentType("text/html");
		p.setEditable(false);
		JScrollPane scroll = new JScrollPane(p);
		this.add(scroll);
		
		p.addHyperlinkListener(new HyperlinkListener() {
		    public void hyperlinkUpdate(HyperlinkEvent e) {
		        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		        	chargeAide("/doc/" + e.getDescription());
		        }
		    }
		});
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent windowEvent) {
		        actif = false;
		    }
		});
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2, 5);
		
		this.setSize(dim.width/2, dim.height-50);
		
		//Charge l'index principale
		chargeAide("/doc/index.md");
		this.setVisible(true);

	}
	
	/**
	 * Charge un fichier d'aide dans la fenêtre
	 * @param chemin
	 */
	private void chargeAide(String chemin){
		String text = "";
		try {
			text = lisFichier(chemin);
			text = new Markdown4jProcessor().process(text);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//System.out.println("Problème d'accès au fichier du menu aide!");
		}

		text = text.replaceAll("src=\"","src=\"" + getClass().getResource("/doc/"));
		//System.out.println(text);
		p.setText(text);
		p.setCaretPosition(0);
	}
	
	/**
	 * Permet de lire un fichier d'aide
	 * @param chemin
	 * @return le contenu du fichier dans un String
	 * @throws java.io.IOException
	 */
	private String lisFichier(String chemin) throws java.io.IOException{
		StringBuffer donnee = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(chemin),"UTF-8"));
		char[] buf = new char[1024];
		int nbRead=0;
		while((nbRead=reader.read(buf)) != -1){
			String readData = String.valueOf(buf, 0, nbRead);
			donnee.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return donnee.toString();
	}
}
