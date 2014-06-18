package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import controleur.Simulateur;
import paquets.Paquet;
import paquets.PaquetEthernet;
import paquets.PaquetIP;
import paquets.PaquetTCP;
import paquets.PaquetUDP;
import standards.CurrentTime;
import elements.ElementReseau.Interface;

/**********************************************************************
 * <p>
 * But:<br>
 * Permet l'affichage des paquets capturé pour chaque interface.
 * </p><p>
 * Description:<br>
 * Chaque interface réseau où les paquets sont capturé sont dans un onglet 
 * séparé.
 * Chaque fois qu'un paquet arrive, il est redirigé vers le bonne onglet. 
 * Celui-ci est directement mis à jour.
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public class JCaptures extends JPanel{

	private static final long serialVersionUID = 1L;
	static String[] paquets = {"Ethernet","ARP","ICMP","IP","STP","TCP","UDP"};
	static boolean[] filtrePaquet = new boolean[paquets.length];
	
	static JTabbedPane onglet = new JTabbedPane();
	
	/**
	 * Initialisation de la gestion des onglets
	 */
	@SuppressWarnings("static-access")
	public JCaptures() {

		super(new GridLayout());
		this.add(onglet);
		
		for (int i = 0; i < filtrePaquet.length; i++){
			filtrePaquet[i] = true;
		}
		
		this.setBackground(Color.WHITE);
		onglet.setMinimumSize(new Dimension(120, 100));
		onglet.setBackground(Color.WHITE);
		onglet.setTabPlacement(onglet.TOP);
		onglet.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		onglet.addMouseListener(new MouseHandlerTab());

	}
	
	/**Ajoute un nouvel onglet et retourne une interface pour envoyer les paquets.
	 * 
	 * @param name	nom de l'onglet
	 * @return	l'interface pour envoyer les paquets
	 */
	public ICapture ajouteTab(String name){
	
		Analyse a = new Analyse();

		onglet.addTab(name,new ImageIcon(getClass().getResource("/img/binocular.png")), a);
		onglet.setSelectedComponent(a);
		return a.getCallBack();
		
	}
	
	/**Permet de sauver toutes les captures en cours dans un fichier CSV.
	 * 
	 * @param file le fichier de destination.
	 */
	public void saveCapture(File file){
		Writer writer = null;
		try {
			writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file)));
			int nbTab = onglet.getTabCount();
			for(int i=0;i<nbTab;i++){
				
				Object[][] data = ((Analyse)onglet.getComponentAt(i)).getModele().getData();
				String[] titre = ((Analyse)onglet.getComponentAt(i)).getModele().getTitle();
				
				writer.write("Captures de l'interface " + onglet.getTitleAt(i) + "\n");
				for(int col=0;col<titre.length;col++){
					writer.write(titre[col] + ";");
				}
				writer.write("\n");
				
				for(int row=0;row<data.length;row++){
					for(int col=0;col<titre.length;col++){
						writer.write(data[row][col] + ";");
					}
					writer.write("\n");
				}
				
				writer.write("\n\n");
			}
			writer.flush();
			
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Listener : double clique sur un onglet, supprime l'onglet.
	 *
	 */
	class MouseHandlerTab extends MouseAdapter{
		@SuppressWarnings("static-access")
		@Override
		public void mouseClicked(MouseEvent arg0) {
			if(arg0.getClickCount() == 2 && JCaptures.this.onglet.getTabCount() > 0){
				((Analyse)JCaptures.this.onglet.getSelectedComponent()).suppLiens();
				JCaptures.this.onglet.remove(JCaptures.this.onglet.getSelectedComponent());
				JCaptures.this.repaint();
			}
			
		}    	
    }
	
	/**
	 * Gestion du tableau qui reçoit les paquets.
	 *
	 */
	class Analyse extends JScrollPane{
		
		private static final long serialVersionUID = 1L;
		Interface i;
		Modele modele = new Modele();
		JTable t = new JTable(modele);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		final TableRowSorter<Modele> sorter = new TableRowSorter(modele);
		
		Analyse(){
			
	        t.setFillsViewportHeight(true);
	        t.setRowSelectionAllowed(true);
	        t.setFont(new Font("Monospaced", Font.PLAIN, 12));
			t.setAutoscrolls(true);
			this.getViewport().add(t);
			this.setAutoscrolls(true);
			this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			
			t.getColumnModel().getColumn(0).setPreferredWidth(100);
			t.getColumnModel().getColumn(5).setPreferredWidth(100);
			t.getColumnModel().getColumn(6).setPreferredWidth(100);
			t.getColumnModel().getColumn(7).setPreferredWidth(150);
			
			t.getColumnModel().getColumn(1).setPreferredWidth(150);
			t.getColumnModel().getColumn(2).setPreferredWidth(150);
			t.getColumnModel().getColumn(3).setPreferredWidth(150);
			t.getColumnModel().getColumn(4).setPreferredWidth(150);
			
			t.getColumnModel().getColumn(8).setPreferredWidth(250);
			
			t.setRowSorter(sorter);
			
			updateFilter();

		}
		
		/**
		 * Enleve l'analyse sur l'interface réseau.
		 */
		void suppLiens(){
			if(i != null)
				i.suppAnalyse();
		}
		
		/**Crée l'interface java pour envoyer des paquets depuis l'interface réseau,
		 * 
		 * @return l'interface.
		 */
		ICapture getCallBack(){
			return new ICapture() {
				
				@Override
				public void analysePaquet(Paquet p) {
					Object[] o = new Object[10];
					o[0] = CurrentTime.getInstance().toString();
					
					o[1] = ((PaquetEthernet)p).getMacSource().toString();
					o[2] = ((PaquetEthernet)p).getMacDest().toString();
					o[7] = p.typeDePaquet();
					o[8] = new String(p.getDonnee());
					
					if(p instanceof PaquetIP){
						o[3] = ((PaquetIP)p).getIpSource().toString();
						o[4] = ((PaquetIP)p).getIpDest().toString();
					}
					if(p instanceof PaquetUDP){
						o[5] = ((PaquetUDP)p).getPortSource();
						o[6] = ((PaquetUDP)p).getPortDest();
					}
					else if(p instanceof PaquetTCP){
						o[5] = ((PaquetTCP)p).getPortSource();
						o[6] = ((PaquetTCP)p).getPortDest();
					}
					o[9] = p;
					
					modele.addRow(o);

					
					Analyse.this.t.revalidate();
					Simulateur.frame.captures.revalidate();
					
					Analyse.this.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
						@Override
						public void adjustmentValueChanged(AdjustmentEvent e) {  
				            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
						}
				    });
					
					JCaptures.this.repaint();
					Analyse.this.repaint();
					
				}
				@Override
				public void destroy(){
					onglet.remove(Analyse.this);
				}
			};
		}
		
		/**retourne le modèle de donnée du tableau.
		 * 
		 * @return le modèle.
		 */
		Modele getModele(){
			return modele;
		}
		
		/**
		 * 
		 * Redéfinition du modèle de base pour l'ajout dynamique de nouvelles entrées
		 * dans le tableau.
		 *
		 */
	    @SuppressWarnings("serial")
		class Modele extends AbstractTableModel {
	    	
	    	private Object[][] data = new Object[0][];
			
			private String[] titre = {"Temps [ms]","MAC src","MAC dest","IP src","IP dest","Port src","Port dest","Protocole","Information"};

			/**
			 * Retourne le nombre de colonne.
			 * 
			 * @return le nombre de colonne.
			 */
	        public int getColumnCount() {
	            return titre.length;
	        }
	 
	        /**
	         * Retourne le nombre de ligne.
	         * @return nombre de ligne.
	         */
	        public int getRowCount() {
	            return data.length;
	        }
	 
	        /**
	         * Retourne le nom de colonne en fonction du numéro.
	         * @return le nom de colonne.
	         */
	        public String getColumnName(int col) {
	            return titre[col];
	        }
	 
	        /**
	         * Retourne le contenu d'une cellule en fonction de sa position.
	         * @return le contenu.
	         */
	        public Object getValueAt(int row, int col) {
	            return data[row][col];
	        }

	        /**
	         * Demande si la cellule est éditable.
	         * @return true, si oui.
	         */
	        public boolean isCellEditable(int row, int col) {

	                return false;

	        }
	        
	        /**
	         * Ajoute une nouvel ligne.
	         * @param o la nouvel ligne.
	         */
	        public void addRow(Object[] o){
	        	Object[][] tmp = new Object[data.length+1][];
	        	System.arraycopy( data, 0, tmp, 0, data.length );
	        	tmp[data.length] = o;
	        	data = tmp;
	        }
	        
	        /**
	         * Retourne le tableau complet de donnée.
	         * @return le tableau.
	         */
	        public Object[][] getData(){
	        	return data;
	        }
	        /**
	         * Retourne les titres du tableau.
	         * @return les titres.
	         */
	        public String[] getTitle(){
	        	return titre;
	        }
	    
	    }
	    

	}
	
	/**
	 * Met à jour le contenu du tableau en fonction des filtres utilisateurs.
	 */
	public static void updateFilter(){

		for(int i = 0; i<onglet.getTabCount(); i++){
			Analyse tmp = ((Analyse)onglet.getComponentAt(i));
			LinkedList <RowFilter<Object, Object>> filters = new LinkedList<>();
			for(int cond=0; cond<paquets.length; cond++){
				if(filtrePaquet[cond]){
					filters.add(RowFilter.regexFilter(paquets[cond],7));
				}
			}

			tmp.sorter.setRowFilter(RowFilter.orFilter(filters));
			
		}
	}
}
