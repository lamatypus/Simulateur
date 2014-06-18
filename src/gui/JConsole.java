package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultCaret;

import elements.config.*;
import elements.config.ShellPC.Signal;

/**********************************************************************
 * <p>
 * But:<br>
 * Représentation graphique du terminal d'un élément configurable.
 * </p><p>
 * Description:<br>
 * La console est toujours en lien avec un élément configurable.
 * L'utilisateur tape des commandes et la console envoie la requête à 
 * l'élément. Celui-ci répond via une méthode dans cette classe.
 * 
 * L'utilisateur peut envoyer des signaux. C'est de la responsabilité
 * de l'élément de la traiter.
 * 
 * La console possède un historique. Chaque commande envoyée est enregistrée.
 * Elle peuvent donc être retrouvée au moyen des flèches.
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/

public class JConsole extends JFrame{

	private static final long serialVersionUID = 1L;
	private JTextArea consoleCmd = new JTextArea();
	private JTextArea consoleEdition = new JTextArea();
	
	private String commande = "";
	private Configurable config;
	JScrollPane panel = new JScrollPane();
	
	private int cursorCmd = 0;
	private int histPos = -1;
	private LinkedList<String> historique = new LinkedList<String>();
	private boolean modifCmd = false;
	
	/**Constructeur unique.
	 * 
	 * @param nom Le nom de la console qui sera affiché
	 * @param c	l'élément configurable avec lequel la console interagit.
	 */
	public JConsole(String nom, Configurable c){
		config = c;
		
		this.setSize(600, 350);
		this.setResizable(true);
		this.setUndecorated(false);
		
		this.setTitle("Lamatypus - " + nom + " - Console de configuration");
	
		ImageIcon i = new ImageIcon(getClass().getResource("/img/console_icon.png"));
		this.setIconImage(i.getImage());
		
		//Insert dans un scrollable
		panel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				
		this.setBackground(Color.BLACK);
		
		this.add(panel);
		this.setLocation(900, 50);
		
		//envoie un signal de terminaison à l'élément si la fenêtre se ferme.
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent windowEvent) {
		    	JConsole.this.config.signal(ShellPC.Signal.CTRL_C);
		    }
		});
		
		//configuration textBox console de commande
		consoleCmd.setForeground(Color.WHITE);
		consoleCmd.setBackground(Color.BLACK);
		consoleCmd.setFont(new Font("Monospaced",Font.BOLD,15));
		consoleCmd.setCaretColor(Color.WHITE);
		consoleCmd.setAutoscrolls(true);
		consoleCmd.setLineWrap(true);
		consoleCmd.setCaret(new ConsoleCaret());
		
		//Gére l'appui sur les touche
		consoleCmd.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				
				if(arg0.getKeyCode() == KeyEvent.VK_F1){
					JOptionPane.showMessageDialog(JConsole.this, "Ctrl-C : Quitte le programme courant\nCtrl-X : Quitte l'éditeur de configuration"
			                ,"Aide de la console", JOptionPane.PLAIN_MESSAGE);
				}
				else if ((arg0.getKeyCode() == KeyEvent.VK_C) && ((arg0.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    config.signal(Signal.CTRL_C);
            		JConsole.this.commande = "";
            		JConsole.this.cursorCmd = 0;
            		JConsole.this.modifCmd = false;
            		JConsole.this.histPos = -1;
                }
				else if("1234567890qwertzuiopasdfghjklyxcvbnm,.-_'\": /QWERTZUIOPASDFGHJKLYXCVBNM|<>$éàè!+*ç%&()=äö¦@#[]".indexOf(arg0.getKeyChar())!=-1){
					
					if(cursorCmd == 0){
						commande = arg0.getKeyChar() + commande;
						
					}
					else if(cursorCmd == commande.length()){
						commande += arg0.getKeyChar();
					}
					else{
						commande = commande.substring(0, cursorCmd) + arg0.getKeyChar() + commande.substring(cursorCmd);
					}
					JConsole.this.cursorCmd++;
					JConsole.this.modifCmd = true;
				}
			}
			
		});
	    
		//Remplacement de l'action par défaut de certaine touche.
		int condition = JComponent.WHEN_FOCUSED;
		InputMap inputMap = consoleCmd.getInputMap(condition);
		ActionMap actionMap = consoleCmd.getActionMap();
		
		KeyStroke backSpaceStroke = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,0);
		KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0);
		KeyStroke leftStroke = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0);
		KeyStroke rigthtStroke = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0);
		KeyStroke upStroke = KeyStroke.getKeyStroke(KeyEvent.VK_UP,0);
		KeyStroke downStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0);
		KeyStroke deleteStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0);
		inputMap.put(enterStroke, enterStroke.toString());
		inputMap.put(backSpaceStroke, backSpaceStroke.toString());
		inputMap.put(leftStroke, leftStroke.toString());
		inputMap.put(rigthtStroke, rigthtStroke.toString());
		inputMap.put(upStroke, upStroke.toString());
		inputMap.put(downStroke, downStroke.toString());
		inputMap.put(deleteStroke, deleteStroke.toString());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK), "ctrlv");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,0), "tab");
		//La touche enter
		actionMap.put(enterStroke.toString(), new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				consoleCmd.append("\n");
				JConsole.this.sendCmd(JConsole.this.commande);
				//System.out.println(JConsole.this.commande);
				if(JConsole.this.commande.length() > 0){
					JConsole.this.historique.addFirst(JConsole.this.commande);
				}
				JConsole.this.histPos = -1;
				JConsole.this.commande = "";
				JConsole.this.cursorCmd = 0;
				JConsole.this.modifCmd = false;
			}
		});
		//La touche backspace
		actionMap.put(backSpaceStroke.toString(), new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(JConsole.this.cursorCmd > 0 && JConsole.this.commande.length() > 0){
					int tmpCaret = JConsole.this.consoleCmd.getCaretPosition();
					
					
					if(JConsole.this.cursorCmd == JConsole.this.commande.length()){
						JConsole.this.commande = JConsole.this.commande.substring(0,JConsole.this.cursorCmd-1);
						
						JConsole.this.consoleCmd.setText(
								JConsole.this.consoleCmd.getText().substring(0,JConsole.this.consoleCmd.getCaretPosition()-1));
					}
					else{
						JConsole.this.commande = JConsole.this.commande.substring(0,JConsole.this.cursorCmd-1)
								+ JConsole.this.commande.substring(JConsole.this.cursorCmd);
						
						JConsole.this.consoleCmd.setText(
								JConsole.this.consoleCmd.getText().substring(0,JConsole.this.consoleCmd.getCaretPosition()-1)
								+ JConsole.this.consoleCmd.getText().substring(JConsole.this.consoleCmd.getCaretPosition()));
					}
					 
					JConsole.this.cursorCmd--;
					JConsole.this.consoleCmd.setCaretPosition(tmpCaret-1);
					
					JConsole.this.modifCmd = true;
				}
			}
		});
		//La touche delete
		actionMap.put(deleteStroke.toString(), new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(JConsole.this.commande.length()-JConsole.this.cursorCmd > 0 && JConsole.this.commande.length() > 0){
					int tmpCaret = JConsole.this.consoleCmd.getCaretPosition();
					
					
					if(JConsole.this.cursorCmd == 0){
						JConsole.this.commande = JConsole.this.commande.substring(1,JConsole.this.commande.length());
						
					}
					else{
						JConsole.this.commande = JConsole.this.commande.substring(0,JConsole.this.cursorCmd)
								+ JConsole.this.commande.substring(JConsole.this.cursorCmd+1);
							
					}
					
					JConsole.this.consoleCmd.setText(
							JConsole.this.consoleCmd.getText().substring(0,JConsole.this.consoleCmd.getCaretPosition())
							+ JConsole.this.consoleCmd.getText().substring(JConsole.this.consoleCmd.getCaretPosition()+1));

					JConsole.this.consoleCmd.setCaretPosition(tmpCaret);

					JConsole.this.modifCmd = true;
				}
			}
		});
		//La touche flèche gauche
		actionMap.put(leftStroke.toString(), new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(JConsole.this.cursorCmd > 0){
					JConsole.this.cursorCmd--;
					JConsole.this.consoleCmd.setCaretPosition(JConsole.this.consoleCmd.getCaretPosition()-1);
				}
			}
		});
		//La touche flèche droite
		actionMap.put(rigthtStroke.toString(), new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(JConsole.this.cursorCmd < JConsole.this.commande.length()){
					JConsole.this.cursorCmd++;
					JConsole.this.consoleCmd.setCaretPosition(JConsole.this.consoleCmd.getCaretPosition()+1);
				}
			}
		});
		//La touche flèche haut
		actionMap.put(upStroke.toString(), new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int posToSee = JConsole.this.histPos;
				
				if(JConsole.this.histPos < JConsole.this.historique.size()-1){
					
					
					if(JConsole.this.commande.length() > 0 && JConsole.this.modifCmd == true){
						JConsole.this.historique.addFirst(JConsole.this.commande);
						posToSee = 0;
					}
					//Enelve la commande actuel
					JConsole.this.consoleCmd.setText(JConsole.this.consoleCmd.getText().substring(0,
							JConsole.this.consoleCmd.getText().length() - JConsole.this.commande.length()));
					
					JConsole.this.commande = JConsole.this.historique.get(++posToSee);
					
					JConsole.this.consoleCmd.append(JConsole.this.commande);
					JConsole.this.cursorCmd = JConsole.this.commande.length();

					JConsole.this.histPos = posToSee;
					JConsole.this.modifCmd = false;
				}
				
			}
		});
		//La touche flèche bas
		actionMap.put(downStroke.toString(), new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int posToSee = JConsole.this.histPos;

				if(posToSee >= 0 && JConsole.this.commande.length() > 0 && JConsole.this.modifCmd == true){
					JConsole.this.historique.addFirst(JConsole.this.commande);

					JConsole.this.consoleCmd.setText(JConsole.this.consoleCmd.getText().substring(0,
							JConsole.this.consoleCmd.getText().length() - JConsole.this.commande.length()));
					JConsole.this.commande = "";
					JConsole.this.cursorCmd = 0;
					
					--posToSee;
					
					JConsole.this.modifCmd = false;
					
				} 
				else if(posToSee > 0){
					
					//enleve commande courante
					JConsole.this.consoleCmd.setText(JConsole.this.consoleCmd.getText().substring(0,
							JConsole.this.consoleCmd.getText().length() - JConsole.this.commande.length()));
					
					JConsole.this.commande = JConsole.this.historique.get(--posToSee);
					JConsole.this.consoleCmd.append(JConsole.this.commande);
					JConsole.this.cursorCmd = JConsole.this.commande.length();
					
					JConsole.this.modifCmd = false;
				}
				else if (posToSee == 0){
		
					JConsole.this.consoleCmd.setText(JConsole.this.consoleCmd.getText().substring(0,
							JConsole.this.consoleCmd.getText().length() - JConsole.this.commande.length()));
					JConsole.this.commande = "";
					JConsole.this.cursorCmd = 0;
					
					--posToSee;
					
					JConsole.this.modifCmd = false;
				}
				
				JConsole.this.histPos = posToSee;
			}
		});
		
		//Supprime le control V
		actionMap.put("ctrlv", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
                //Ne fait rien
            }
        });
		//Supprime le TAB
		actionMap.put("tab", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
                //Ne fait rien
            }
        });
				
		//configuration textBox console d'edition
		consoleEdition.setForeground(Color.WHITE);
		consoleEdition.setBackground(Color.BLACK);
		consoleEdition.setFont(new Font("Monospaced",Font.BOLD,15));
		consoleEdition.setCaretColor(Color.WHITE);
		consoleEdition.setAutoscrolls(true);
		consoleEdition.setLineWrap(true);
		consoleEdition.setEditable(true);
		consoleEdition.getCaret().setVisible(true);
		consoleEdition.setCaret(new ConsoleCaret());

		consoleEdition.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_F1){
					JOptionPane.showMessageDialog(JConsole.this, "Ctrl-C : Quitte le programme courant\nCtrl-X : Quitte l'éditeur de configuration"
			                ,"Aide de la console", JOptionPane.PLAIN_MESSAGE);
				}
				else if ((arg0.getKeyCode() == KeyEvent.VK_X) && ((arg0.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
					 config.signal(Signal.CTRL_X);
				}
			}
		});
		this.setVisible(true);
		
		setConsole();
	}


	/**Envoie une commande à l'élément
	 * 
	 * @param cmd la commande
	 */
	private void sendCmd(String cmd){
		config.ordre(cmd);
	}
	
	/**Affiche une réponse de commande dans la console
	 * 
	 * @param rep la réponse
	 */
	public void reponse(String rep){
		//System.out.println(rep); //DEBUG	
		consoleCmd.append(rep);
		consoleCmd.setCaretPosition(consoleCmd.getDocument().getLength());
	}
	
	/**
	 * Met la console en mode normal
	 */
	public void setConsole(){
		panel.setViewportView(consoleCmd);
		panel.revalidate();
		consoleCmd.requestFocus();
	}
	
	/**
	 * Met la console en mode édition
	 */
	public void setEdition(){
		consoleEdition.setText(null);
		panel.setViewportView(consoleEdition);
		panel.revalidate();
		consoleEdition.requestFocus();
	
	}
	/**Met à jour le texte d'édition
	 * 
	 * @param s le nouveau texte
	 */
	public void setTextEdition(String s){
		
		consoleEdition.setText(s);
		consoleEdition.setCaretPosition(0);		
	}
	
	/**Retourne le texte d'édition
	 * 
	 * @return le texte modifié
	 */
	public String getTextEdition(){
		return consoleEdition.getText();
	}
	
	/**
	 * 
	 * Surcharge du Caret par défaut pour une question esthétique.
	 *
	 */
	class ConsoleCaret extends DefaultCaret{
		
		private static final long serialVersionUID = 1L;

		protected ConsoleCaret(){
			super();
			setBlinkRate(750);
		}
		
        @Override
        protected void positionCaret(MouseEvent e) {
            e.consume();
        }

        @Override
        protected void moveCaret(MouseEvent e) {
           e.consume();
        }	

	}
}
