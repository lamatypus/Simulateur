package controleur;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import standards.CurrentTime;
import elements.Hub;
import elements.PC;
import elements.Routeur;
import elements.Serveur;
import elements.Switch;
import gui.*;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe principal
 * </p><p>
 * Description:<br>
 * Cette classe lance l'ensemble de l'application. Elle gère aussi les 
 * références pour la sauvegarde de l'environnement.
 * Contient le logger général.
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public class Simulateur{

	public static boolean modified = false;
	public static File file = null;
	public static JPrincipal frame;
	public static LinkedList<JDragElement> elements = new LinkedList<JDragElement>();
	public static LinkedList<JDragElement[]> connectedElements = new LinkedList<JDragElement[]>();
	
	public static Logger LOGGER = Logger.getLogger("InfoLogging");
	
	private static boolean logIntoStdOut = false;
	
	protected Simulateur(){

	}
	
	public static void main(String[] args) {
		
		
		LOGGER.setUseParentHandlers(logIntoStdOut);
		
		
		LOGGER.info("Démarrage application");

		CurrentTime.getInstance().init();
		frame = new JPrincipal();

		
	}
	
	/**Permet d'ajouter un nouvel élément à l'environnement.
	 * 
	 * @param e	L'élément a ajouter.
	 */
	public static void ajoutElement(JDragElement e){
		elements.add(e);
	}
	
	/**Permet de supprimer un élément de l'environnement.
	 * 
	 * @param e L'élément a supprimer.
	 */
	public static void suppElement(JDragElement e){
		elements.remove(e);
	}

	/**Permet de récupèrer la fenêtre graphique
	 * 
	 * @return La fenêtre graphique
	 */
	public static JPrincipal getJPrincipal(){
		return frame;
	}

	/**
	 * Permet de reseter tout l'environnement de travail.
	 */
	public static void reset(){		
		PC.reset();
		Serveur.reset();
		Hub.reset();
		Switch.reset();
		Routeur.reset();
		
		for(JDragElement jdragElement : elements){
			jdragElement.getElement().destroy();
		}
		
		elements.clear();
		connectedElements.clear();
		frame.reseaux.removeAll();
		frame.reseaux.repaint();
		modified = false;
		file = null;
	}
	
	/**Permet de sauvegarder l'environnement courant.
	 * 
	 * @param f Le fichier de destination
	 */
	public static void save(File f){
		try {
			OutputStream out = new FileOutputStream(f);
			
			ObjectOutputStream obj = new ObjectOutputStream(out);
			obj.writeObject(elements);
			obj.writeObject(connectedElements);
			obj.close();
			out.close();
	         
		} catch (IOException e) {
			LOGGER.info("Error save file");
			JOptionPane.showMessageDialog(frame,"Error while saving file");
			e.printStackTrace();
		}
		
		file = f;
		modified = false;
	}
	
	public static void saveConfigHtml (File file){
		try {
			Writer writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file)));
			
			writer.write("<html><body><div style='width: 60%; margin: auto;'>");
			
			for (JDragElement element : elements){
				writer.write("<div>");
				writer.write(element.getElement().allInfo());
				writer.write("</div>");
			}
			writer.write("</div></body></html>");
			writer.flush();
			
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**Permet de réstituer l'environnement de travail depuis un fichier
	 * 
	 * @param f	Le fichier source.
	 */
	@SuppressWarnings("unchecked")
	public static void open(File f){
		reset();
		try {
			
			FileInputStream fileIn = new FileInputStream(f);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			elements = (LinkedList<JDragElement>) in.readObject();
			connectedElements = (LinkedList<JDragElement[]>) in.readObject();
			in.close();
			fileIn.close();
			
		} catch (IOException | ClassNotFoundException e) {
			LOGGER.info("Error open file");
			JOptionPane.showMessageDialog(frame,"Error while opening file");
		}
		file = f;
		
		//Redraw
		for(JDragElement d:elements){
			frame.reseaux.add(d);
		}
		frame.reseaux.repaint();
	}
	
}
