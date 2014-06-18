package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**********************************************************************
 * <p>
 * But:<br>
 * Afficher les informations d'un �l�ment s�lectionn�
 * </p><p>
 * Description:<br>
 * Cette classe permet d' afficher des informations/configurations
 * d'un �l�ment s�lectionn� en bas � gauche dans l'application.
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
public class JInformations extends JPanel{

	JPrincipal principal;
	JEditorPane editor;
	
	/**
	 * Constructeur de la classe
	 * @param j Fen�tre principale
	 */
	public JInformations(JPrincipal j) {
		super(new BorderLayout());
		principal = j;
		
		this.setMinimumSize(new Dimension(115, 140));
		

		this.setBackground(Color.WHITE);
		
		editor = new JEditorPane();
		editor.setBackground(Color.WHITE);
		editor.setContentType("text/html");
		editor.setText("<html><body>Info...</body></html>");
		editor.setEditable(false);
		
		editor.setForeground(Color.black);
		JScrollPane scroll = new JScrollPane(editor);
		
		this.add(scroll);
		
	}
	
	public void setInfo(String t){
		editor.setText(t);
	}

}
