package elements.config;

import java.util.LinkedList;

import standards.IPv4;
import application.*;
import elements.*;
import elements.ElementReseauIP.InterfaceIP;
import elements.ElementReseauIP.MethodeApprentissageIP;
import gui.JConsole;
import exception.*;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe qui contrôle une instance de la classe JConsole et la classe ExecutablePC
 * </p><p>
 * Description:<br>
 * Cette classe gère l'exécution des commandes sur la console.
 * Elle instancie la classe JConsole et la classe ExecutablePC.
 * 
 * La vue JConsole permet de taper une commande et d'afficher son résultat.
 * 
 * La classe ExecutablePC permet d'exécuter des applications sur le pc.
 * Attention, il n'y a qu'une seule application qui peut tourner à la fois.
 * Cette classe doit être instanciée pour chaque pc (classe elements.PC)
 * 
 * Les commandes disponibles sont spécifiées par la classe interne ListeCommandePC.
 * 
 * Pour avoir accès à la table de référence des commandes, il faut utiliser
 * la liste : enumCommande.
 * 
 * Cette classe gère :
 * 		
 * 		Un historique des commandes. Accessible par la flèche Haut.
 * 
 * 		Les paramètres contenant des espaces.
 * 		
 * 		Le signal Ctrl+C (stopper une application) et Ctrl+X (sortir du mode édition)
 * 
 * 		Le ShellPC gère le mode édition pour écrire la configuration du fichier DHCP,
 * 		par exemple.
 * 
 * 		Une aide pour taper des commandes. Lorsque l'utilisateur fait une faut de frappe,
 * 		le shell lui propose une correction en se basant sur la liste des commandes.
 * 
 * 		Le prompt >
 * 
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	14.06.2014
 ***********************************************************************/
public class ShellPC implements Configurable {

	/**
	 * Interface qui spécifie les méthodes à implémenter pour exécuter le
	 * comportement des commandes.
	 * 
	 * Les commandes doivent fournir une méthode traitement et une méthode
	 * pour afficher un message d'erreur.
	 *
	 * Date de dernière mise à jour : 3 mai 2014
	 */
	interface ActionCommande {
		public void traiter(String[] cmd, int idCommande);
		
		public String error(int id);
	}

	/**
	 * Liste des commandes existantes pour la configuration de l'élément réseau
	 * PC. Cette liste est utilisée par l'algorithme de recherche.
	 * 
	 * Pour l'élément PC une commande est constituée des éléments suivants : 
	 * 	
	 * 	-	un tableau de String qui contient chaque token de la commande. Un token valide est un token qui 
	 * 		ne contient pas d'esapce.
	 * 
	 * 		Si un token vide est spécifié ("") alors il s'agit d'un paramètre, une entrée quelconque pouvant comporter 
	 * 		des espaces. Les espaces doivent être protégés par des " ".
	 * 
	 * -	un objet implémentant l'interface ActionCommande.
	 * 
	 * -	un boolean qui spécifie si la commande ouvre une application.
	 * 
	 * Dernière mise à jour : 3 mai 2014
	 */
	class ListeCommandePC {

		public LinkedList<ListeCommandePC> listeCommande = new LinkedList<ShellPC.ListeCommandePC>();
		public String[] commande;
		public ActionCommande traitement;
		public boolean application;
		
		ListeCommandePC() {
			listeCommande.add(new ListeCommandePC(new String[] { "ifconfig" }, new ShellPC.Ifconfig(), false));

			listeCommande.add(new ListeCommandePC(new String[] { "ip", "addr", "add", "", "dev", "" }, new ShellPC.Ip_addr_add(), false));
			listeCommande.add(new ListeCommandePC(new String[] { "ip", "addr", "del", "dev", "" }, new ShellPC.Ip_addr_del(), false));
			listeCommande.add(new ListeCommandePC(new String[] { "ip", "route", "add", "default", "via", "" }, new ShellPC.Ip_route_add(), false));
			listeCommande.add(new ListeCommandePC(new String[] { "ip", "route", "del", "default" }, new ShellPC.Ip_route_del(), false));
 
			listeCommande.add(new ListeCommandePC(new String[] { "ethernet","" }, new ShellPC.Ethernet(), true));
			listeCommande.add(new ListeCommandePC(new String[] { "ping" }, new ShellPC.Ping(), true));
			
			listeCommande.add(new ListeCommandePC(new String[] { "chat" }, new ShellPC.Chat(), true));
			listeCommande.add(new ListeCommandePC(new String[] { "udp" }, new ShellPC.Udp(), true));
			
			listeCommande.add(new ListeCommandePC(new String[] { "services" }, new ShellPC.ServicesManage(), true));
			
			// IPv6
			//listeCommande.add(new ListeCommandePC(new String[] { "ip", "-6", "addr", "add", "", "dev", "" }, new ShellPC.Ip_addr_add_6(), false));
			//listeCommande.add(new ListeCommandePC(new String[] { "ip", "-6", "addr", "del", "", "dev", "" }, new ShellPC.Ip_addr_del_6(), false));
			//listeCommande.add(new ListeCommandePC(new String[] { "ip", "-6","route", "add", "default", "via", "" }, new ShellPC.Ip_route_add_6(), false));
			//listeCommande.add(new ListeCommandePC(new String[] { "ip", "-6", "route", "del", "default", "via", "" }, new ShellPC.Ip_route_del_6(), false));
			//listeCommande.add(new ListeCommandePC(new String[] { "ping6", "" }, new ShellPC.Ping6(), true));
			//listeCommande.add(new ListeCommandePC(new String[] { "help" }, new ShellPC.Help(), false));

		}

		/**
		 * Constructeur d'une commande
		 * 
		 * @param cmd la commande
		 * @param t	l'action à faire
		 * @param app oui, si c'est une application
		 */
		protected ListeCommandePC(String[] cmd, ActionCommande t, boolean app) {
			commande = cmd;
			traitement = t;
			application = app;
		}
		
		/**
		 * Affiche le message d'erreur "USAGE :" suivit de la commande dont les paramètres "" 
		 * sont remplacés par la chaîne [parametre]
		 * 
		 * @param i
		 * @return
		 */
		public String defaultErr(int i) {
			
			String err = "USAGE : ";
			
			for(String s : enumCommande.listeCommande.get(i).commande) {
				
				if(s.isEmpty())
					err += " [parametre]";
				else
					err += " " + s;
			}
			
			return err;
		}
	}

	// Enum pour la liste des commandes.
	protected ListeCommandePC enumCommande = new ListeCommandePC();

	LinkedList<String> historique = new LinkedList<String>();

	private PC pc;
	protected JConsole console;
	private ExecutablePC applicationActive = null;
	private boolean appRunning = false;
	private Services service = null;

	/**
	 * Ouvre la fenêtre JConsole. 
	 */
	public ShellPC(PC pc) {

		this.pc = pc;
		console = new JConsole(pc.getInfo(), this);
		prompt();
	}

	/**
	 * 
	 * @return le pc auquel appartient la console
	 */
	public PC getPC() { return pc; } 
	
	/**
	 * Utilisée par les applications pour afficher des messages dans la JConsole.
	 */
	public void sortieConsole(String sortie) {
		console.reponse(sortie);
	}

	/**
	 * Utilisé par les applications pour indiquer que le programme est fini
	 */
	public void exitApplication() {
		appRunning = false;
		applicationActive = null;
	}
	
	/**
	 * Affiche un prompt
	 */
	protected void prompt(){
		console.reponse("> ");
	}
	
	@Override
	public void signal(Signal s){
		
		if(appRunning && s.equals(Signal.CTRL_C)){
			applicationActive.controleC();
			prompt();
		}
		else if(s.equals(Signal.CTRL_X)){
			if(service != null){
				service.setConfig(console.getTextEdition());
				console.setConsole();
			}
			service = null;
		}
	}


	/**
	 * Rechercher une correpsondance dans la table de commande
	 * 
	 * Cette fonction s'attend à recevoir un tableau de token. 
	 * Un token valide est un String ne contenant pas d'espace.
	 * 
	 * Dans le cas de paramètre contenant des espaces il faut les protéger avec "..."
	 * 
	 * Paramètre : un paramètre est reconnu comme tel si le token de la table est vide ""
	 * Ainsi les paramètres qui sont de tokens quelconque n'influencent pas la recherche.
	 * 
	 * Une application est identifée à l'aide d'un et un seul token valide puis le reste des tokens sont considérés
	 * comme des paramètres. Il peut y en avoir 0 ou n.
	 * 
	 * 
	 * La recherche retourne l'id d'une commande qui présente dans la table.
	 * Cette recherche gère les paramètres et les applications.
	 * 
	 * Dans le cas où la recherche n'est pas concluante, la méthode retourne 
	 * la valeur -1 et l'id de la commande la plus proche. 
	 * Le deuxième id peut éventuellement être à -1 dans le cas où il n'y aucune correspondance.
	 * 
	 * Dans notre cas, on utilise l'erreur pour afficher le message USAGE : ..
	 * 
	 * @param cmdSplit tableau de token
	 * @return l'id de la commande et la commande la plus proche.
	 */
	private Integer[] rechercheCommande(String[] cmdSplit) {

		int id = -1;
		int error = -1;
		
		//Début de la recherche
		for (int j = 0; j < enumCommande.listeCommande.size(); j++) {
			
			String[] temp = enumCommande.listeCommande.get(j).commande;
			
			//Analyse la commande
			for (int i = 0; i < cmdSplit.length; i++) {
				
				//Gestion des paramètres
				if(i < temp.length && (cmdSplit[i].equalsIgnoreCase(temp[i]) || temp[i].isEmpty())) {
					id = j;
					error = j;
					if(enumCommande.listeCommande.get(j).application){
						break;
					}
				}
				else {
					id = -1;
					break;
				}
			}
				
			//Récupère le résultat	
			if(id != -1) {
				
				if(temp.length != cmdSplit.length && !enumCommande.listeCommande.get(j).application)
					id = -1;
				break;
			}
			
		} //Fin de la recherche 
		
		return new Integer[]{id,error};
	}

	/**
	 * Description : La commande passée par la console est analysée pour trouver
	 * une correspondance dans l'enum. Cette méthode est appellée par la JConsole.
	 * 
	 * Dans le cas où application est lancée, l'entrée de la console est redirigée vers l'application en cours.
	 * 
	 * Cette méthode s'occupe d'afficher le prompt. Le caractère > dans la console à la fin de chaque entrée.
	 * 
	 * @param cmdSplit tableau de token
	 * @return l'id de la commande et la commande la plus proche.
	 */
	@Override
	public void ordre(String nouvelleCmd) {
		
		//Si une application est lancée, l'entrée de la console est redirigée vers l'application en cours.
		if(appRunning) {
			applicationActive.entreeConsole(nouvelleCmd);
			return;
		}
		
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
		
		//Récupère les tokens
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

		//Recherche d'une correspondance
		Integer[] resultat = rechercheCommande(cmdSplit);
		int id_commande = resultat[0];
		int error = resultat[1];
		
		//Analyse des résultats de la recherche
		//----------------------------------------------------------------------------------------------------------------
		
		//PREMIER CAS : il n'y a pas de correspondance
		if (id_commande == -1) {
			
			if(!nouvelleCmd.equals("")) {
			
				if(error != -1)
					console.reponse(enumCommande.listeCommande.get(error).traitement.error(error));
				else
					console.reponse("Command not found !");
			}
		}
		
		//DEUXIEME CAS : il y a une correspondance
		else {
			try {
				enumCommande.listeCommande.get(id_commande).traitement.traiter(cmdSplit,id_commande);
			
			} catch(Exception exp){
					console.reponse("Abort ... ");
			}
		}
		
		if(!appRunning) {
			console.reponse("\n");
			prompt();
		}
	}
	
	/**
	 * Commande: ifconfig
	 * affiche la configuration des interfaces réseaux
	 */
	class Ifconfig implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			String output = "";
			int id = 0;
			for (ElementReseauIP.InterfaceIP i : pc.getInterfacesIP()) {
				
				String gateway = "Not set";
				if (pc.getDefaultGateway() != null){
					gateway = pc.getDefaultGateway().getProchainSaut().toString();
				}
				
				output += "eth" + id + "\n\t"
				+ "Link encap.........: Ethernet\n\t"
				+ "MAC address........: " + i.getMac() + "\n\t";

				if (i.getIp() != null) {
					output += "IPv4 Address.......: " + i.getIp() + "\n\t" 
							+ "Subnet Mask........: " + i.getIp().getMasque() + "\n\t"
							+ "Broadcast Address..: " + i.getIp().getBroadcast() + "\n\t"
							+ "Default Gateway....: " + gateway + "\n\t";
				} else {	
					output += "IPv4 Address.......: " + "Not set" + "\n\t"
							+ "Subnet Mask........: " + "Not set" + "\n\t"
							+ "Broadcast Address..: " + "Not set" + "\n\t" 
							+ "Default Gateway....: " + gateway + "\n\t";
				}
				
				output += "\n";
				id++;
			}

			console.reponse(output);
		}

		@Override
		public String error(int id) {
			return enumCommande.listeCommande.get(id).defaultErr(id);
		}
	}

	/**
	 * Commande: ip addr add
	 * ajoute une IP sur une interface
	 */
	class Ip_addr_add implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {

			if (enumCommande.listeCommande.get(idCommande).commande.length != cmd.length) {
				console.reponse("missing arguments !");
				return;
			}

			if (cmd[3] == null || cmd[5] == null) {
				console.reponse("empty arguments !");
				return;
			}

			if (cmd[3].isEmpty() || cmd[5].isEmpty()) {
				console.reponse("empty arguments !");
				return;
			}

			String interfaceName = cmd[5];

			// Récupérer l'interface sélectionnée
			
			
			interfaceName = interfaceName.substring(3, interfaceName.length());
			int id = -1;
			try {
				id = Integer.parseInt(interfaceName);
				
			} catch (NumberFormatException e) {
				console.reponse("Interface must have a number !");
				e.printStackTrace();
				return;
			}	
			
			if (id == -1 || id >= pc.getInterface().length) {
				console.reponse("bad name for interface !");
				return;
			}
			
			String addressIP[] = cmd[3].split("/");

			// Créer une nouvelle adresse IP
			if (addressIP == null) {
				console.reponse("Address ip or mask missing !");
				return;
			}

			if (addressIP.length != 2) {
				console.reponse("Address ip or mask missing !");
				return;
			}
			
			if (addressIP[0].isEmpty() || addressIP[1].isEmpty()) {
				console.reponse("Address ip or mask missing !");
				return;
			}
			try {
				((InterfaceIP[])(pc.getInterface()))[id].setIp(new IPv4(addressIP[0]), MethodeApprentissageIP.Manuel);
				((InterfaceIP[])(pc.getInterface()))[id].getIp().setMasque(Integer.parseInt(addressIP[1]));
			
			} catch (IPNonValide e) {
				console.reponse("Address ip not valid !");
			
			} catch (NumberFormatException e) {
				console.reponse("Mask must be a number !");
				e.printStackTrace();
			
			} catch (MasqueNonValide e) {
				console.reponse("Mask not valid !");
				e.printStackTrace();
			}
		}

		@Override
		public String error(int id) {
			return enumCommande.listeCommande.get(id).defaultErr(id);
		}
	}
	
	/**
	 * Commande: ip addr del
	 * supprime la configuration IP d'une interface
	 */
	class Ip_addr_del implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {

			if (enumCommande.listeCommande.get(idCommande).commande.length != cmd.length) {
				console.reponse("missing arguments !");
				return;
			}

			if (cmd[4] == null) {
				console.reponse("empty arguments !");
				return;
			}

			if (cmd[4].isEmpty()) {
				console.reponse("empty arguments !");
				return;
			}
			
			String interfaceName = cmd[4];
			
			// Récupérer l'interface sélectionnée
			interfaceName = interfaceName.substring(3, interfaceName.length());
			int id = -1;
			try {
				id = Integer.parseInt(interfaceName);
				((InterfaceIP[])(pc.getInterface()))[id].setIp(null, null);
			} catch (NumberFormatException e) {
				console.reponse("Interface must have a number !");
				e.printStackTrace();
				return;
			}	
		}

		@Override
		public String error(int id) {
			return enumCommande.listeCommande.get(id).defaultErr(id);
		}
	}

	/**
	 * Commande: ip route add default gateway
	 * ajoute une route par défaut
	 */
	class Ip_route_add implements ActionCommande {
		public void traiter(String[] cmd, int idCommande) {
			String adresse = cmd[5];
			
			try {
				pc.setDefaultGateway(new IPv4 (adresse));
			} catch (IPNonValide e) {
				e.printStackTrace();
			}
		}

		@Override
		public String error(int id) {
			return enumCommande.listeCommande.get(id).defaultErr(id);
		}
	}

	/**
	 * Commande: ip route del default gateway
	 * Supprime une route par défaut
	 */
	class Ip_route_del implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			pc.setDefaultGateway(null);
		}

		@Override
		public String error(int id) {
			return enumCommande.listeCommande.get(id).defaultErr(id);
		}
	}




	/**
	 * Commande: ping
	 * appel l'application ping
	 */
	class Ping implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			if(cmd.length > 1){
				appRunning = true;
				applicationActive = new PingApp(ShellPC.this, cmd[1]);
			}
			else{
				console.reponse(error(idCommande));
			}
		}

		@Override
		public String error(int id) {
			return "Usage : ping [ip address]";
		}
	}

	/**
	 * Commande: chat
	 * appel l'application de chat
	 */
	class Chat implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			appRunning = true;
			applicationActive = new CustomChat(ShellPC.this, cmd);
		}

		@Override
		public String error(int id) {
			return null;
		}
	}

	/**
	 * Commande: udp
	 * appel application de test udp
	 */
	class Udp implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			applicationActive = new ForgeUDP(ShellPC.this, cmd);
		}

		@Override
		public String error(int id) {
			return enumCommande.listeCommande.get(id).defaultErr(id);
		}
	}
	/**
	 * Commande: ethernet
	 * appel application de test ethernet
	 */
	class Ethernet implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			applicationActive = new ForgeEthernet(ShellPC.this, cmd);
		}

		@Override
		public String error(int id) {
			return enumCommande.listeCommande.get(id).defaultErr(id);
		}
	}
	
	/**
	 * Commande: services
	 * permet de gérer les services de la machine
	 */
	class ServicesManage implements ActionCommande {

		public void traiter(String[] cmd, int idCommande) {
			if(cmd.length == 1){
				int i= 0;
				for(Services s : pc.getServices()){
					console.reponse("id:" + i + " -> " + s.getName()+"\n");
					i++;
				}
			}
			else if(cmd.length == 3){
				int id = -1;
				try{
					id = Integer.parseInt(cmd[2]);
				}
				catch(NumberFormatException e){
					console.reponse(error(idCommande));
					return;
				}
				if(id > pc.getServices().size()){
					console.reponse(error(idCommande));
					return;
				}
					
				switch(cmd[1]){
				case "start" : 
					pc.getServices().get(id).demarreService();
					break;
				case "stop" :
					pc.getServices().get(id).stopService();
					break;
				case "config" : 
					if(pc.getServices().get(id).configurable()){
						console.setEdition();
						console.setTextEdition(pc.getServices().get(id).getConfig());
						service = pc.getServices().get(id);
					}
					else{
						console.reponse("Not configurable!");
					}
					break;
				}
			}
			else{
				console.reponse(error(idCommande));
			}
		}

		@Override
		public String error(int id) {
			return enumCommande.listeCommande.get(id).defaultErr(id);
		}
	}
	
	
	public enum Signal{
		CTRL_C,
		CTRL_X;
	}
	
//	class Ip_addr_6 implements ActionCommande {
//
//		public void traiter(String[] cmd, int idCommande) {
//			 console.reponse(error);
//		}
//	}
//
//	class Ip_route_6 implements ActionCommande {
//
//		public void traiter(String[] cmd, int idCommande) {
//			console.reponse(error);
//		}
//	}
//
//	class Ip_addr_add_6 implements ActionCommande {
//
//		public void traiter(String[] cmd, int idCommande) {
//			console.reponse(error);
//		}
//	}
//
//	class Ip_addr_del_6 implements ActionCommande {
//
//		public void traiter(String[] cmd, int idCommande) {
//			console.reponse(error);
//		}
//	}
//
//	class Ip_route_add_6 implements ActionCommande {
//
//		public void traiter(String[] cmd, int idCommande) {
//			console.reponse(error);
//		}
//	}
//
//	class Ip_route_del_6 implements ActionCommande {
//
//		public void traiter(String[] cmd, int idCommande) {
//			console.reponse(error);
//		}
//	}
	
//	class Ping6 implements ActionCommande {
//
//		public void traiter(String[] cmd, int idCommande) {
//			console.reponse(error);
//		}
//	}
}