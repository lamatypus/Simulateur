package protocoles;

import paquets.Paquet;
import paquets.PaquetSTP;
import elements.ElementReseau;
import elements.Switch;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe qui implémente le protocole STP : Spanning Tree Protocol
 * </p><p>
 * Description:<br>
 * Ce protocole permet de supprimer les boucles générés par les éléments Switch.
 * 
 * Pour cela, il transforme le graphe (connexions d'un sous-réseau) en un arbre en 
 * désactivant certains liens. 
 * 
 * Pour enregistrer la configuration des switchs dans le réseau le procole modifie 
 * la table STP des switchs.
 * 
 * 
 * La méthode annoncerRoot() envoie un messaeg BPDU sur toutes les interfaces 
 * pour annoncer le switch ROOT.
 * 
 * Ces messages permmettent de définir l'arbre. Ils sont envoyés par tous les switchs
 * toutes les 2 secondes.
 * 
 * Si un message modifie le BID root d'un switch alors il le communique immédiatement
 * en rappelant la méthode annoncerRoot().
 * 
 * Cette méthode ne crée pas de boucle car les messages sont transmis uniquement s'il 
 * y a une modification de la configuration d'un switch sinon ils ne sont plus retransmis.
 * 
 * PHASE 1 :
 * Tous les switchs s'annoncent comme étant root.
 * Il définisse le switch root en se basant sur leur BID.
 * Il définisse les interfaces root et désignée pour dessiner l'arbre.
 * 
 * PHASE 2 :
 * Les autres interfaces connectées au switch sont bloquées.
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
public class ProtocoleSTP {
	static int num = 0;

	/**
	 * Permet de savoir si le paquet est pour ce protocole
	 * 
	 * @param p: le paquet
	 * @return true si le paquet est pour ce protocole
	 */
	public static boolean monPaquet(Paquet p) {
		return PaquetSTP.class.equals(p.getClass());
	}

	/**
	 * Permet d'envoyer un paquet STP depuis un élément réseau en passant par le
	 * protocole Ethernet
	 * 
	 * @param e: l'élément réseau
	 * @param p: le paquet STP
	 */
	public static void envoie(ElementReseau e, PaquetSTP p) {
		ProtocoleEthernet.envoie(e, p);
	}
	
	/**
	 * Réception d'un paquet BPDU
	 * 
	 * Le protocole indique à la table STP que l'interface reçoit des paquets STP.
	 * Le switch e est connecté à un autre switch.
	 * 
	 * Le protocole set le BID de destination pour l'interface i
	 * 
	 * Le protocole set l'interface comme DESIGNATED si le message a été envoyé 
	 * par une interface root.
	 * 
	 * Le protocole set l'interface comme BLOCKING si le message a été envoyé par
	 * une interface bloquée.
	 * 
	 * Le procotcole set un nouveau S root si le switch root reçu est plus petit
	 * que son bid root. Dans ce cas, il appelle la méthode annonceRoot().
	 * 
	 * sinon le procotole calcule le meilleure chemin pour arriver au switch root et
	 * sauve ces modifications si modifications il y a.
	 * 
	 * 
	 * @param e: l'élément réseau IP
	 * @param p: le paquet STP
	 * @param i: l'interface 
	 */
	public static void recoit(ElementReseau e , PaquetSTP p, ElementReseau.Interface i) {
		
		if(e.getClass() != Switch.class || p.getClass() != PaquetSTP.class){
			return;
		}

		Switch element = (Switch) e;
		PaquetSTP bpdu = (PaquetSTP) p;

		// Définir interface active
		element.getTableSTP().setActiveInterfaceSTP(i, true);

		// Définir bid destination et check Backup port
		element.getTableSTP().setBIDdestination(i, bpdu.getBIDsource());

		if (element.getTableSTP().setBackupPort(i))
			return;

		// Définir designated port
		if (bpdu.isRootPort())
			element.getTableSTP().setDesignatedPort(i);
		
		//Définir port bloqué
//		if(bpdu.isBlockingPort()) {
//			element.getTableSTP().setBlockingPort(i);
//		}
		
		//Définir le BID root 
		if(bpdu.getBIDroot() < element.getTableSTP().getRootBID()) {
			element.getTableSTP().setRootBID(bpdu.getBIDroot());
			element.getTableSTP().setRootPort(i);
			element.getTableSTP().setMetric(bpdu.getMetric());
			element.getTableSTP().setSourceBID(bpdu.getBIDsource());

			// Envoyer sur toutes les interfaces
			annoncerRoot(element, bpdu.getMetric() + 1);
			
		}
		
		/* Définir l'interface qui mène au switch ROOT en fonction de la métric 
		   et des bids autres switch menant au switch root */
		else if(bpdu.getBIDroot() == element.getTableSTP().getRootBID()) {
			
			if(bpdu.getMetric() < element.getTableSTP().getMetric()) {
				element.getTableSTP().setRootPort(i);
				element.getTableSTP().setMetric(bpdu.getMetric());
				element.getTableSTP().setSourceBID(bpdu.getBIDsource());

			} else if (bpdu.getMetric() == element.getTableSTP().getMetric()) {

				if (bpdu.getBIDsource() < element.getTableSTP().getSourceBID()) {
					element.getTableSTP().setRootPort(i);
					element.getTableSTP().setSourceBID(bpdu.getBIDsource());
				}
			}
		}

	}

	/**
	 * Envoyer sur toutes les interfaces, la config. de l'interface émettrice du switch selon la table STP.
	 * 
	 * @param element: le switch
	 * @param metric: la métrique
	 */
	public static void annoncerRoot(Switch element, int metric) {	
		for(ElementReseau.Interface sortie : element.getInterface()) {
			
			if(sortie.getInterfaceDest() != null) {
				
				PaquetSTP paquet = new PaquetSTP(element, new String("BID source : " + element.getTableSTP().getBid() + " BID root : " + element.getTableSTP().getRootBID() + " metric : " + element.getTableSTP().getMetric()).getBytes());
				
				paquet.setBIDsource(element.getTableSTP().getBid());
				paquet.setBIDdestination(element.getTableSTP()
						.getBIDdestination(sortie));

				paquet.setBIDroot(element.getTableSTP().getRootBID());
				paquet.setMetric(metric);

				paquet.setMacSource(sortie.getMac());
				paquet.setMacDest(sortie.getInterfaceDest().getMac());

				if (sortie == element.getTableSTP().getRootPort())
					paquet.setRootPort(true);
				else
					paquet.setRootPort(false);

				paquet.setBlockingPort(!element.getTableSTP().isActiveBySTP(
						sortie));

				// Envoyer le paquet
				paquet.envoie();
			}
		}
	}
}
