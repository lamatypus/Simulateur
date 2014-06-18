package elements.config;

import java.util.LinkedList;

import controleur.Simulateur;
import standards.IPv4;
import elements.ElementReseauIP.InterfaceIP;
import elements.ElementReseauIP.MethodeApprentissageIP;
import elements.Routeur;
import elements.config.ShellPC.Signal;
import elements.util.TableRoutage.Methode;
import exception.IPNonValide;
import exception.MasqueNonValide;
import gui.JConsole;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe qui contr�le une instance de la classe JConsole
 * </p><p>
 * Description:<br>
 * Cette classe g�re l'ex�cution des commandes sur la console pour le routeur.
 * Elle instancie la classe JConsole.
 * 
 * La vue JConsole permet de taper une commande et d'afficher son r�sultat.
 * 
 * Les commandes disponibles sont sp�cifi�es par la classe interne RouteurCmdList.
 * 
 * Pour avoir acc�s � la table de r�f�rence des commandes, il faut utiliser
 * la liste : list.
 * 
 * Cette classe g�re :
 * 		
 * 		Un historique des commandes. Accessible par la fl�che Haut.
 * 
 * 		Les param�tres contenant des espaces.
 * 		
 * 		Les alias. L'utilisateur peut taper des raccourcis des commandes. Par exemple :
 * 		La commande "Configure terminal" peut �tre abr�g�e "conf t" ou "co ter".
 * 		
 * 		Les diff�rents modes inh�rents au routeur : default mode, admin mode, config mode.. etc
 * 		ainsi que leur prompt respectif.
 * 
 * </p>
 *
 * @author		Rapha�l Buache
 * @author     	Magali Fr�lich
 * @author     	C�dric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	14.06.2014
 ***********************************************************************/
public class ConfigRouteur implements Configurable{
	
	interface ActionCommande {
		public void traiter(String[] cmd, int idCommande);
	}
	
	/**
	 * 
	 *  Les modes dans lequel la console peut se trouver
	 *
	 */
	enum StateRouter {
	
		DEFAULT_MODE(">"),
		ADMIN_MODE("#"),
		CONFIG_MODE("(config)#"),
		ROUTER_RIP_MODE("(config-routeur)#"),
		INTERFACE_MODE("(config-if)#"),
		TUNNEL_MODE("(config-if)#");
		
		private final String prompt;
		
		private StateRouter(String s) {prompt = s;}
	}
	
	/**
	 * 
	 * Liste des commandes et des accessibilit�.
	 *
	 */
	class RouteurCmdList {
		
		public LinkedList<RouteurCmdList> listeCommande = new LinkedList<RouteurCmdList>();
		
		RouteurCmdList()  {
			
		listeCommande.add(new RouteurCmdList(new String[]{"enable"},						new ConfigRouteur.Enable(), 				new StateRouter[]{StateRouter.DEFAULT_MODE}));
		listeCommande.add(new RouteurCmdList(new String[]{"configure","terminal"},			new ConfigRouteur.Configure_terminal(),		new StateRouter[]{StateRouter.ADMIN_MODE}));
		
		listeCommande.add(new RouteurCmdList(new String[]{"show","ip","route"},				new ConfigRouteur.Show_ip_route(),			new StateRouter[]{StateRouter.DEFAULT_MODE,StateRouter.ADMIN_MODE}));
		listeCommande.add(new RouteurCmdList(new String[]{"show","ip","interface","brief"},	new ConfigRouteur.Show_ip_interface_brief(),new StateRouter[]{StateRouter.DEFAULT_MODE,StateRouter.ADMIN_MODE}));

		listeCommande.add(new RouteurCmdList(new String[]{"hostname",""},					new ConfigRouteur.Hostname(),				new StateRouter[]{StateRouter.CONFIG_MODE}));
		listeCommande.add(new RouteurCmdList(new String[]{"description",""},				new ConfigRouteur.Description(),			new StateRouter[]{StateRouter.CONFIG_MODE}));
		listeCommande.add(new RouteurCmdList(new String[]{"ip","route","","",""},			new ConfigRouteur.Ip_route(),				new StateRouter[]{StateRouter.CONFIG_MODE}));
		listeCommande.add(new RouteurCmdList(new String[]{"no","ip","route","","",""},		new ConfigRouteur.No_ip_route(),			new StateRouter[]{StateRouter.CONFIG_MODE}));
		
		listeCommande.add(new RouteurCmdList(new String[]{"interface","fastethernet", ""},	new ConfigRouteur.Interface(),				new StateRouter[]{StateRouter.CONFIG_MODE}));
		listeCommande.add(new RouteurCmdList(new String[]{"ip","address","",""},			new ConfigRouteur.Ip_address(),				new StateRouter[]{StateRouter.INTERFACE_MODE}));
		listeCommande.add(new RouteurCmdList(new String[]{"no","ip","address"},				new ConfigRouteur.No_ip_address(),			new StateRouter[]{StateRouter.INTERFACE_MODE}));
		listeCommande.add(new RouteurCmdList(new String[]{"no","shutdown"},					new ConfigRouteur.No_shutdown(),			new StateRouter[]{StateRouter.INTERFACE_MODE}));
		listeCommande.add(new RouteurCmdList(new String[]{"shutdown"},						new ConfigRouteur.Shutdown(),				new StateRouter[]{StateRouter.INTERFACE_MODE}));

		listeCommande.add(new RouteurCmdList(new String[]{"exit"},							new ConfigRouteur.Exit(),   					StateRouter.values()));
		
// RIP
//				listeCommande.add(new RouteurCmdList(new String[]{"no","router","rip"},							new ConfigRouteur.No_router_rip(),			new StateRouter[]{StateRouter.CONFIG_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"router","rip"},								new ConfigRouteur.Router_rip(),				new StateRouter[]{StateRouter.CONFIG_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"version","2"},								new ConfigRouteur.Version(),				new StateRouter[]{StateRouter.ROUTER_RIP_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"auto-summary"},								new ConfigRouteur.Auto_summary(),			new StateRouter[]{StateRouter.ROUTER_RIP_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"no","auto-summary"},							new ConfigRouteur.No_auto_summary(),		new StateRouter[]{StateRouter.ROUTER_RIP_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"network",""},								new ConfigRouteur.Network(),				new StateRouter[]{StateRouter.ROUTER_RIP_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"no","network",""},							new ConfigRouteur.No_network(),				new StateRouter[]{StateRouter.ROUTER_RIP_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"default-information", "originate"},			new ConfigRouteur.Default_information(),	new StateRouter[]{StateRouter.ROUTER_RIP_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"no", "default-information", "originate"},	new ConfigRouteur.No_default_information(),	new StateRouter[]{StateRouter.ROUTER_RIP_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"debug","ip","route"},						new ConfigRouteur.Debug_ip_route(),			new StateRouter[]{StateRouter.CONFIG_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"no","debug","ip","route"},					new ConfigRouteur.No_debug_ip_route(),		new StateRouter[]{StateRouter.CONFIG_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"show","ip","protocols"},						new ConfigRouteur.Show_ip_protocols(),		new StateRouter[]{StateRouter.DEFAULT_MODE,StateRouter.ADMIN_MODE}));
		
// IPV6	
//				listeCommande.add(new RouteurCmdList(new String[]{"show","ipv6","route"},				new ConfigRouteur.Show_ipv6_route(),		new StateRouter[]{StateRouter.DEFAULT_MODE,StateRouter.ADMIN_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"show","ipv6","interface"},			new ConfigRouteur.Show_ipv6_interface(),	new StateRouter[]{StateRouter.DEFAULT_MODE,StateRouter.ADMIN_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"ipv6","enable"},						new ConfigRouteur.Ipv6_enable(),			new StateRouter[]{StateRouter.CONFIG_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"ipv6","unicast-routing"},			new ConfigRouteur.Ipv6_unicast_routing(),	new StateRouter[]{StateRouter.CONFIG_MODE}));				
//				listeCommande.add(new RouteurCmdList(new String[]{"ipv6","route",""},					new ConfigRouteur.Ipv6_route(),				new StateRouter[]{StateRouter.CONFIG_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"no","ipv6","route","","",""},		new ConfigRouteur.No_ipv6_route(),			new StateRouter[]{StateRouter.CONFIG_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"ipv6","address",""},					new ConfigRouteur.Ipv6_address(),			new StateRouter[]{StateRouter.INTERFACE_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"no","ipv6","address"},				new ConfigRouteur.No_ipv6_address(),		new StateRouter[]{StateRouter.INTERFACE_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"ipv6","rip","","enable"},			new ConfigRouteur.Ipv6_rip_enable(),		new StateRouter[]{StateRouter.INTERFACE_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"ipv6","nd","prefix",""},				new ConfigRouteur.Ipv6_nd_prefix(),			new StateRouter[]{StateRouter.INTERFACE_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"interface","tunnel",""},				new ConfigRouteur.Interface_tunnel(),		new StateRouter[]{StateRouter.CONFIG_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"ipv6","enable"},						new ConfigRouteur.Ipv6_enable(),			new StateRouter[]{StateRouter.TUNNEL_MODE}));

// Tunnel IPv6 to IPv4
//				listeCommande.add(new RouteurCmdList(new String[]{"tunnel","source",""},				new ConfigRouteur.Tunnel_source(),			new StateRouter[]{StateRouter.TUNNEL_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"no","tunnel","source",""},			new ConfigRouteur.No_tunnel_source(),		new StateRouter[]{StateRouter.TUNNEL_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"tunnel","destination",""},			new ConfigRouteur.Tunnel_destination(),		new StateRouter[]{StateRouter.TUNNEL_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"no","tunnel","destination",""},		new ConfigRouteur.No_tunnel_destination(),	new StateRouter[]{StateRouter.TUNNEL_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"tunnel","mode","ipv6ip"},			new ConfigRouteur.Tunnel_mode_ipv6ip(),		new StateRouter[]{StateRouter.TUNNEL_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"no","tunnel","mode","ipv6ip"},		new ConfigRouteur.No_tunnel_mode_ipv6ip(),	new StateRouter[]{StateRouter.TUNNEL_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"shutdown"},							new ConfigRouteur.Shutdown1(),				new StateRouter[]{StateRouter.TUNNEL_MODE}));
//				listeCommande.add(new RouteurCmdList(new String[]{"end"},	new ConfigRouteur.End(),	new StateRouter[]{StateRouter.ROUTER_RIP_MODE}));
				
		
	}		
		public String[] commande;
		public ActionCommande traitement;
		public boolean application;
		StateRouter[] state;
		
		/**Constructeur pour faire une nouvel commande
		 * 
		 * @param s	la commande
		 * @param enable l'action a faire
		 * @param state les �tats dans laquel la commande est disponible
		 */
		private RouteurCmdList (String[] s, ActionCommande enable, StateRouter[] state){
			commande = s;
			traitement = enable;
			this.state = state;
		}
	}

	RouteurCmdList list = new RouteurCmdList();
	
	private StateRouter currentState = StateRouter.DEFAULT_MODE;
	
	private elements.Routeur.InterfaceIP interfaceConfig = null;

	private Routeur routeur;
	private JConsole console;
	
	/**
	 * Cr�e une nouvelle interface de configuration pour un routeur.
	 * 
	 * @param r le routeur
	 */
	public ConfigRouteur(Routeur r) {
		routeur = r;
		console = new JConsole(routeur.getHostname(),this);
		prompt();
	}
	
	/**
	 * affiche un prompt dans la console suivant le mode
	 */
	public void prompt(){
		console.reponse(routeur.getHostname()+StateRouter.values()[currentState.ordinal()].prompt);
	}

	/**
	 * Description : Rechercher une correpsondance dans la table de commande
	 * 
	 * La recherche retourne une liste de commande qui ont une correspondance dans la table.
	 * Cette recherche g�re les alias, les param�tres et les �tats (mode Admin, mode Config...).
	 * 
	 * Cette fonction s'attend � recevoir un tableau de token. 
	 * Un token valide est un String ne contenant pas d'espace.
	 * 
	 * Dans le cas de param�tre contenant des espaces il faut les prot�ger avec "..."
	 * 
	 * Alias : un alias valide correspond au d�but ou au token complet.
	 *
	 * Exemple de cas : 
	 * e == enable 			OK
	 * ena == enable 		OK 
	 * enable == enable		OK
	 * ewwwnable == enable	KO
	 * 
	 * Param�tre : un param�tre est reconnu comme tel si le token de la table est vide ""
	 * Ainsi les param�tres qui sont de tokens quelconque n'influencent pas la recherche.
	 * 
	 * Dans le cas o� il n'y a pas de correspondance la m�thode retourne NULL.
	 * Dans le cas o� l'alias est ambig�e la m�thode retourne une liste avec plusieurs possibilit�s
	 * 
	 */
	private LinkedList<Integer> rechercheCommande(String[] cmdSplit) {

		int id = -1;
		
		boolean stateOk = false;
		boolean tokenOk = true;
		
		LinkedList<Integer> candidat = new LinkedList<Integer>();
		
		//D�but de la recherche
		for (int j = 0; j < list.listeCommande.size(); j++) {
			
			String[] temp = list.listeCommande.get(j).commande;
			
			//V�rifie que la commande est accessible dans l'�tat actuel du rooteur (Admin mode, Config mode, ..)
			for(StateRouter s : list.listeCommande.get(j).state) {
				
				if(currentState == s) {
					stateOk = true;
					break;
				}
			}
			
			if(!stateOk)
				continue;
			else
				stateOk = false;
			
			
			//V�rifie que la commande comporte le bon nombre de token
			if(temp.length != cmdSplit.length) {
				id = -1;
				continue;
			}
			
			//Analyse la commande
			for (int i = 0; i < cmdSplit.length; i++) {
				
				//Gestion des alias (Test si le token ressemble au token de la commande)
				tokenOk = true;
				for(int k=0; k < cmdSplit[i].length(); k++) {
						
					if(k >= temp[i].length()) {
						tokenOk = false;
						break;
					}
						
					if(!String.valueOf(cmdSplit[i].charAt(k)).equalsIgnoreCase(String.valueOf(temp[i].charAt(k)))) {
						tokenOk = false;
						break;
					}
				}
					
				//Gestion des param�tres  
				if(tokenOk || temp[i].isEmpty()) {
					id = j;
				}
				else {
					id = -1;
					break;
				}
			}
				
			//R�cup�re le r�sultat	
			if(id != -1) {
				candidat.add(id);
			}
			
		} //Fin de la recherche 

		
		return candidat;
	}
	
	@Override
	public void ordre(String nouvelleCmd) {	
		//Commande null
		if(nouvelleCmd == null) {
			prompt();
			return;
		}

		//Commande vide ("")
		if(nouvelleCmd.isEmpty()) {
			prompt();
			return;
		}
		
		//R�cup�re les tokens
		LinkedList<String> split = new LinkedList<String>();
		
		String[] para = nouvelleCmd.split("\"");
		
		for(int i=0; i < para.length; i=i+2) {
			
			para[i] = para[i].trim();
			String[] temp = para[i].split("\\s+");
			
			for(int j=0; j < temp.length; j++) {
				
				if(!temp[j].isEmpty())
					split.add(temp[j]);
			}
			
			if(i+1 < para.length)
				split.add(para[i+1]);
		}

		String[] cmdSplit = new String[split.size()];
		
		for(int i=0; i < split.size(); i++)
			cmdSplit[i] = split.get(i);
			
		if (cmdSplit == null || cmdSplit.length == 0) {
			console.reponse("Entry is incorrect !");
			prompt();
			return;
		}

		//Effectue la recherche
		LinkedList<Integer> candidat = rechercheCommande(cmdSplit);
		
		//Analyse des r�sultats de la recherche
		//---------------------------------------------------------------------------------------------------------------------------------------
		/* Premier cas : Il y a plusieurs r�sultats qui pourraient correspondre � la commande 
		 * 
		 * Cela se produit dans le cas suivant : 
		 * 
		 * Entr�e : show ip route
		 * 
		 * Possibilit�e retourn�e :
		 * show ip route
		 * show ipv6 route
		 * 
		 * le token ip peut correpondre au token ip et ipv6
		 * 
		 * Pour r�soudre ce cas : on test la longeur des caract�res pour d�terminer quel commande choisir.
		 * 
		 * ip == ip  	OK
		 * ip == ipv6	KO
		 * 
		 * Cela ne fonctionnerait pas dans le cas suivant : 
		 * 
		 * Entr�e : e
		 * 
		 * Possibilit�e retourn�e :
		 * enable
		 * exit
		 * 
		 * Ici le programme retourne l'erreur Ambigous command !
		 */
		
		int id_commande;
		if(candidat.size() > 1) {
			int[] temp = new int[candidat.size()];
			
			//Initialisation
			for(int i = 0; i < candidat.size(); i++)
				temp[i] = 0;
			
			//Recherche de correspondance
			for(int i = 0; i < candidat.size(); i++){
				for(int j = 0; j < cmdSplit.length; j++) {	
					if(cmdSplit[j].length() == list.listeCommande.get(candidat.get(i)).commande[j].length()) 
						temp[i]++;
				}
			}
			
			//Recherche la commande la plus appropri�e
			int max = 0;
			for(int i = 1; i < candidat.size(); i++) {		
				if(temp[max] < temp[i]) 
					max = i;
			}
			
			id_commande = candidat.get(max);
			
			//Recherche si le cas est toujours ambig�e
			for(int i = 1; i  < candidat.size(); i++) {
				
				if(temp[max] == temp[i ]) {
					console.reponse("Alias is ambigous!\n");
					prompt();
					return;
				}
			}
			
			try {
				list.listeCommande.get(id_commande).traitement.traiter(cmdSplit,id_commande);
			
			} catch(Exception exp){
					console.reponse("Abort... \n");
			}
			
		}
		
		//DEUXIEME CAS : il n'y a pas de correspondance
		else if (candidat.size() == 0) {
			
			if(!nouvelleCmd.equals("")) 
				console.reponse("Command not found !\n");
		}
		
		//TROSIEME CAS : il y a une et une seule correspondance
		else {
			id_commande = candidat.get(0);
			
			try {
				list.listeCommande.get(id_commande).traitement.traiter(cmdSplit,id_commande);
			
			} catch(Exception exp){
					console.reponse("Abort ... \n");
			}
		}
		prompt();
	}
	
	/**
	 * Commande enable
	 * Change le mode de configuration
	 */
	class Enable implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			currentState = StateRouter.ADMIN_MODE;
		}
	}

	/**
	 * Commande configure terminal
	 * Change le mode de configuration
	 */
	 class Configure_terminal implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			currentState = StateRouter.CONFIG_MODE;
		}
	}


	/**
	 * Commande hostname
	 * change le nom du routeur
	 */
	 class Hostname implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			String hostname = cmd[1];
			routeur.setHostname(hostname);
			Simulateur.frame.reseaux.repaint();
		}
	 }
	/**
	 * Commande description
	 * change la description du routeur
	 */
	 class Description implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			String description = cmd[1];
			routeur.setDescription(description);
		}
	}

	 /**
	 * Commande ip route
	 * ajoute une route statique
	 */
	 class Ip_route implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			try {
				IPv4 ip = new IPv4 (cmd[2], cmd[3]);
				IPv4 prochainSaut = new IPv4 (cmd[4]);
				routeur.nouvelleRoute(ip, prochainSaut, Methode.Static, 1);
			} catch (IPNonValide e) {
				console.reponse("IPv4 address not valid");
			} catch (MasqueNonValide e) {
				console.reponse("Mask IPv4 address not valid");
			}
		}
	}

	 /**
	 * Commande no ip route
	 * supprime une route statique
	 */
	 class No_ip_route implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			try {
				IPv4 ip = new IPv4 (cmd[3], cmd[4]);
				IPv4 prochainSaut = new IPv4 (cmd[5]);
				routeur.supprimeRoute(ip, prochainSaut);
			} catch (IPNonValide e) {
				console.reponse("IPv4 address not valid");
			} catch (MasqueNonValide e) {
				console.reponse("Mask IPv4 address not valid");
			}
		}
	}

	 /**
	 * Commande interface
	 * entre dans le menu d'une interface
	 */
	 class Interface implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			try
            {
                int noInterface = Integer.parseInt(((cmd[2].split("/"))[1]));
                elements.PC.Interface[] interfaces = routeur.getInterface();
                interfaceConfig = (InterfaceIP) interfaces[noInterface];
    			currentState = StateRouter.INTERFACE_MODE;
            }
            catch (Exception e)
            {
            	console.reponse("Unknow interface " + cmd[1] + cmd[2] + "\n");
            }
		}
	}
	 /**
	 * Commande ip address
	 * ajoute l'IP d'une interface
	 */
	 class Ip_address implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			try {
				IPv4 ip = new IPv4 (cmd[2], cmd[3]);
				interfaceConfig.setIp(ip, MethodeApprentissageIP.Manuel);
			} catch (IPNonValide e) {
				console.reponse("IPv4 address not valid");
			} catch (MasqueNonValide e) {
				console.reponse("Mask IPv4 address not valid");
			}
		}
	}
	 /**
	 * Commande no ip interface
	 * supprime l'IP d'une interface
	 */
	 class No_ip_address implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			interfaceConfig.supprimeIP();
		}
	}

	 /**
	  * Commande no shutdowm
	  * active une interface
	  */
	 class No_shutdown implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			interfaceConfig.setActive(true);
		}
	}
	 /**
	  * Commande shutdowm
	  * d�sactive une interface
	  */
	 class Shutdown implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			interfaceConfig.setActive(false);
		}
	}
	 /**
	  * Commande exit
	  * sort du mode courant
	  */
	 class Exit implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			switch(currentState) {
			case ADMIN_MODE: {
				currentState = StateRouter.DEFAULT_MODE;
				break;
			}
			case CONFIG_MODE: {
				currentState = StateRouter.ADMIN_MODE;
				break;
			}
			case INTERFACE_MODE: {
				currentState = StateRouter.CONFIG_MODE;
				break;
			}
			case TUNNEL_MODE: {
				currentState = StateRouter.CONFIG_MODE;
				break;
			}
			case ROUTER_RIP_MODE: {
				currentState = StateRouter.CONFIG_MODE;
				break;
			}
			default:
				break;
			}
		}
	}

	 /**
	  * Commande show ip route
	  * affiche les routes configur�es
	  */
	 class Show_ip_route implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			console.reponse(routeur.getTableRoutage() + "\n");
		}
	}

	 /**
	  * Commande show ip interface brief
	  * affiche un r�sum� des configurations
	  */
	 class Show_ip_interface_brief implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			String status, protocole, ip, methode;
			console.reponse("Interface\tIP-Address\tMethod\tStatus\t\t\tProtocol\n");
			for(InterfaceIP i : routeur.getInterfacesIP()){
				if (i.isActive() && i.getIp() != null){
					status = "up\t\t";
					if (i.getInterfaceDest() != null){
						protocole = "up";
					}
					else{
						protocole = "down";
					}
				}
				else{
					status = "administratively down";
					protocole = "down";
				}
				if (i.getIp() == null){
					ip = "\tunassigned";
				}
				else{
					ip = "\t" + i.getIp().toString();
					if (ip.length() < 9){
						ip += "\t";
					}
				}
				if (i.getMethodeApprentissageIP() == null){
					methode = "unset";
				}
				else{
					methode = i.getMethodeApprentissageIP().toString();
				}
				console.reponse(i.getNom()+"\t"+ip+"\t"+methode+"\t"+status+"\t"+protocole+"\n");
			}
		}
	}

	@Override
	public void signal(Signal s) {

	}
	
	// M�thode non impl�ment�e
	{
		//	 class Ipv6_enable implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class Ipv6_unicast_routing implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class Router_rip implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//			currentState = StateRouter.ROUTER_RIP_MODE;
		//		}
		//	}
		//
		//	 class Version implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//			routeur.setRouteurRIP(true);
		//		}
		//	}
		//
		//	 class Auto_summary implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class No_auto_summary implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class Network implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//			try {
		//				IPv4 ip = new IPv4 (cmd[1], 32);
		//				routeur.ajoutReseauRIP(ip);
		//			} catch (IPNonValide e) {
		//				console.reponse("IPv4 address not valid");
		//			}
		//		}
		//	}
		//
		//	 class No_network implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//			try {
		//				IPv4 ip = new IPv4 (cmd[1], 32);
		//				routeur.supprimeReseauRIP(ip);
		//			} catch (IPNonValide e) {
		//				console.reponse("IPv4 address not valid");
		//			}
		//		}
		//	}
		//	 
		//	 class Default_information implements ActionCommande{
		//
		//		@Override
		//		public void traiter(String[] cmd, int idCommande) {
		//			routeur.setTransmetRouteDefaut(true);
		//		}
		//		 
		//	 }
		//	 
		//	 class No_default_information implements ActionCommande{
		//
		//		@Override
		//		public void traiter(String[] cmd, int idCommande) {
		//			routeur.setTransmetRouteDefaut(false);
		//		}
		//		 
		//	 }
		//
		//	 class End implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//			currentState = StateRouter.ADMIN_MODE;
		//		}
		//	}
		//
		//	 class No_router_rip implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//			routeur.setRouteurRIP(false);
		//		}
		//	}
		//
		//	 class Ipv6_address implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class No_ipv6_address implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class Ipv6_rip_enable implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class Ipv6_nd_prefix implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class Ipv6_route implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class No_ipv6_route implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class Show_ipv6_interface implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class Help implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	 }
		//	 class Interface_tunnel implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//			currentState = StateRouter.TUNNEL_MODE;
		//		}
		//	}
		//
		//	 class Tunnel_source implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class No_tunnel_source implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class Tunnel_destination implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class No_tunnel_destination implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class Tunnel_mode_ipv6ip implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class No_tunnel_mode_ipv6ip implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class Shutdown1 implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class Debug_ip_route implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class No_debug_ip_route implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}
		//
		//	 class Show_ip_protocols implements ActionCommande {
		//		@Override
		//		public void traiter(String[] cmd, int idCommande) {
		//			console.reponse(routeur.getIpProtocols() + "\n");	
		//		}
		//	 }
		//	 class Show_ipv6_route implements ActionCommande {
		//
		//		public void traiter(String[] cmd, int idCommande) {
		//
		//		}
		//	}	
		//Ci-dessous le comportement des diff�rentes commandes possibles dans la
		//console d'un routeur.
		//
		//-   Mode normal (par d�faut)
		//
		//    -   enable: Permet de passer en mode administrateur.
		//
		//        -   configure terminal: Permet de passer en mode configuration.
		//
		//            -   hostname xxx: Permet de nommer le routeur.
		//
		//            -   description xxx: Permet d'ajouter une description du
		//                routeur.
		//
		//            -   ipv6 enable: Permet d'activer l'IPv6.
		//
		//            -   ipv6 unicast-routing: Permet d'activer le routage IPv6.
		//
		//            -   router rip: Permet d'activ� le protocole de routage RIP.
		//
		//                -   version 2: Permet d'activ� RIPv2.
		//
		//                -   auto-summary: Active le mode auto-summary
		//
		//                -   no auto-summary: D�sactive le mode auto-summary
		//
		//                -   network x.x.x.x: Permet d'activ� RIP sur le r�seau
		//                    IPv4 donner en param�tre.
		//
		//                -   no network x.x.x.x: Permet de d�sactiv� RIP sur le
		//                    r�seau IPv4 donner en param�tre.
		//
		//                -   end: Permet de retourner en mode configuration.
		//
		//            -   no router rip: Permet de d�sactiv� le protocole de
		//                routage RIP.
		//
		//            -   ip route x.x.x.x m.m.m.m i: Permet de configurer une
		//                route statique pour l'adresse IPv4 x.x.x.x avec le
		//                masque m.m.m.m sur l'interface de sortie i.
		//
		//            -   no ip route x.x.x.x m.m.m.m i: Permet d'oublier une
		//                route statique pour l'adresse IPv4 x.x.x.x avec le
		//                masque m.m.m.m sur l'interface de sortie i.
		//
		//            -   ipv6 route x:x:x:x:x:x/m i: Permet de configurer une
		//                route statique pour l'adresse IPv6 x:x:x:x:x:x avec le
		//                pr�fix m sur l'interface de sortie i.
		//
		//            -   no ipv6 route x.x.x.x m.m.m.m i: Permet d'oublier une
		//                route statique pour l'adresse IPv6 x:x:x:x:x:x avec le
		//                pr�fix m sur l'interface de sortie i.
		//
		//            -   interface xxx: Permet de passer en mode configuration
		//                d'interface.
		//
		//                -   ip address x.x.x.x m.m.m.m: Permet de configurer une
		//                    adresse IPv4 et un masque.
		//
		//                -   no ip address: Permet d'annuler la configuration de
		//                    l'adresse IPv4 et du masque.
		//
		//                -   ipv6 address x:x:x:x:x:x/m: Permet de configurer une
		//                    adresse IPv6 et son pr�fix.
		//
		//                -   no ipv6 address: Permet de d'annuler la
		//                    configuration de l'adresse IPv6 et de son pr�fix.
		//
		//                -   ipv6 rip XXX enable: Active le routage RIPng sur
		//                    l'interface.
		//
		//                -   ipv6 rip XXX enable: D�sactive le routage RIPng sur
		//                    l'interface.
		//
		//                -   ipv6 nd prefix x:x:x/64: Annonce au r�seau local de
		//                    s'auto-configurer.
		//
		//                -   no shutdown: Permet d'activer l'interface.
		//
		//                -   shutdown: Permet de d�sactiver l'interface.
		//
		//                -   exit: Permet de retourner en mode configuration
		//
		//            -   interface tunnel x: Permet de passer en mode
		//                configuration de tunnel.
		//
		//                -   ipv6 enable: Permet d'activ� l'IPv6.
		//
		//                -   tunnel source i: D�fini l'interface source du
		//                    tunnel.
		//
		//                -   no tunnel source i: Oublie l'interface source du
		//                    tunnel.
		//
		//                -   tunnel destination x.x.x.x: D�fini l'adresse IPv4 de
		//                    destination du tunnel.
		//
		//                -   no tunnel destination x.x.x.x: Oublie l'adresse IPv4
		//                    de destination du tunnel.
		//
		//                -   tunnel mode ipv6ip: Pour activer l'encapsulation
		//                    IPv6 -\> IPv4
		//
		//                -   no tunnel mode ipv6ip: Pour d�sactiver
		//                    l'encapsulation IPv6 -\> IPv4
		//
		//                -   no shutdown: Active l'interface.
		//
		//                -   Shutdown: D�sactive l'interface.
		//
		//                -   exit: Permet de retourner en mode configuration.
		//
		//            -   debug ip route: Active le d�bogage.
		//
		//            -   no debug ip route: D�sactive le d�bogage.
		//
		//            -   exit: Permet de retourner en mode administrateur.
		//
		//        -   show ip route: Permet d'afficher les routes IPv4.
		//
		//        -   show ipv6 route: Permet d'afficher les routes IPv6.
		//
		//        -   show ip interface: Permet d'afficher les informations
		//            relatives � IPv4 sur les diff�rentes interfaces.
		//
		//        -   show ipv6 interface: Permet d'afficher les informations
		//            relatives � IPv6 aux diff�rentes interfaces.
		//
		//        -   exit: Permet de retourner en mode normal.
		//
		//    -   show ip route: Permet d'afficher les routes IPv4.
		//
		//    -   show ipv6 route: Permet d'afficher les routes IPv6.
		//
		//    -   show ip interface: Permet d'afficher les informations relatives
		//        � IPv4 sur les diff�rentes interfaces.
		//
		//    -   show ipv6 interface: Permet d'afficher les informations
		//        relatives � IPv6 aux diff�rentes interfaces.
	}
}