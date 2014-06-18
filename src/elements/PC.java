package elements;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import application.DHCPclient;
import application.Services;
import elements.config.ShellPC;
import elements.util.TableRoutage.Methode;
import elements.util.TableRoutage.Route;
import exception.IPNonValide;
import exception.ProtocoleNonValide;
import paquets.*;
import protocoles.ProtocoleARP;
import protocoles.ProtocoleEthernet;
import protocoles.ProtocoleICMP;
import protocoles.ProtocoleIP;
import protocoles.ProtocoleTCP;
import protocoles.ProtocoleUDP;
import standards.IPv4;

@SuppressWarnings("serial")
public class PC extends ElementReseauIP {
	private static int globalId = 1;
	public int id;
	protected LinkedList<Services> services = new LinkedList<Services>();

	/**
	 * Créer un PC avec un nombre d'interface IP
	 * 
	 * @param nbInterface: nombre d'interface
	 */
	public PC(int nbInterface) {
		super(nbInterface, "eth");
		incId();

		services.add(new DHCPclient(this));

	}

	/**
	 * Créer un PC utilisé pour l'interface graphique
	 * 
	 * @param nbInterface: nombre d'interface
	 * @param ghost
	 */
	public PC(int nbInterface, boolean ghost) {
		super(nbInterface, ghost);
	}

	/**
	 * Set l'id du PC et incrémente l'id global
	 */
	protected void incId() {
		id = globalId++;
	}

	/**
	 * Permet d'obtenir le nom du PC et son numéro
	 */
	@Override
	public String getInfo() {
		return "PC-" + String.valueOf(id);
	}

	/**
	 * Permet de définir les interfaces IP de sortie en fonction de l'adresse IP
	 * de destination
	 */
	@Override
	public InterfaceIP[] getInterfaceSortie(IPv4 dest) {
		InterfaceIP[] interfaceIP = (InterfaceIP[]) getInterface();
		LinkedList<InterfaceIP> interfaceSortie = new LinkedList<InterfaceIP>();

		// Si dans un sous-réseau
		for (InterfaceIP inter : interfaceIP) {
			if (inter.getIp() != null
					&& inter.getIp().estDansSousReseau(dest) != -1) {
				interfaceSortie.add(inter);
			}
		}
		// Si gateway
		if (interfaceSortie.size() == 0 && getDefaultGateway() != null) {
			interfaceSortie.add(getDefaultGateway().getInterfaceSortie());
		}
		try {
			if (dest.compare(IPv4.getGeneralBroadcast())
					|| dest.compare(new IPv4("0.0.0.0"))) {
				return interfaceIP;
			}
		} catch (IPNonValide e) {
		}
		return interfaceSortie.toArray(new InterfaceIP[interfaceSortie.size()]);
	}

	/**
	 * Recoit le paquet sur son interface (i) en appellant le protocole Ethernet
	 * 
	 * @param p: le paquet
	 * @param i: l'interface
	 */
	@Override
	public void recoitCoucheLiaison(Paquet p, Interface i) {
		LOGGER.info(getInfo() + " = Recoit couche liaison");
		ProtocoleEthernet.recoit(i, this, (PaquetEthernet) p);
	}

	/**
	 * Recoit le paquet sur son interface (i) en appellant le bon protocole de
	 * la couche réseau
	 * 
	 * @param p: le paquet
	 * @param i: l'interface IP
	 */
	@Override
	public void recoitCoucheReseau(Paquet p, InterfaceIP i) {
		try {
			LOGGER.info(getInfo() + " = Recoit couche réseau");

			if (p instanceof PaquetIP) {
				ajouteEntree(((PaquetEthernet) p).getMacSource(),
						((PaquetIP) p).getIpSource());
				ProtocoleIP.recoit(this, (PaquetIP) p);
			}

			else if (p instanceof PaquetARP) {
				ProtocoleARP.recoit(i, this, (PaquetARP) p);
			}
		} catch (ProtocoleNonValide e) {
		}
	}

	/**
	 * Recoit le paquet en appellant le bon protocole de la couche transport
	 * 
	 * @param p: le paquet
	 */
	@Override
	public void recoitCoucheTransport(Paquet p) {
		LOGGER.info(getInfo() + " = Recoit couche transport");
		switch (((PaquetIP) p).getProtocole()) {
		case ICMP:
			ProtocoleICMP.recoit(this, (PaquetICMP) p);
			break;
		case TCP:
			ProtocoleTCP.recoit(this, (PaquetTCP) p);
			break;
		case UDP:
			ProtocoleUDP.recoit(this, (PaquetUDP) p);
			break;
		default:
			break;
		}
	}

	/**
	 * Permet d'obtenir toute les informations du PC (nom, mac, ip masque,
	 * gateway)
	 */
	@Override
	public String allInfo() {
		String infos = "<h3>" + getInfo() + "</h3>";
		infos += toHtml();
		return infos;
	}

	/**
	 * Permet de configurer un PC avec un shell
	 */
	@Override
	public void config() {
		new ShellPC(this);
	}

	/**
	 * Permet de créer un nouveau PC identique à l'actuel
	 */
	@Override
	public ElementReseau newElem() {
		return new PC(nbInterfaces);
	}

	/**
	 * Permet de configurer l'adresse IP de destination de la route par défaut
	 * 
	 * @param ip: l'adresse IP
	 */
	public void setDefaultGateway(IPv4 ip) {
		try {
			Route ancienne = getDefaultGateway();
			if (ancienne != null) {
				supprimeRoute(new IPv4("0.0.0.0", 0),
						ancienne.getProchainSaut());
			}
			if (ip != null){
				nouvelleRoute(new IPv4("0.0.0.0", 0), ip, Methode.Static, 1);
			}
		} catch (IPNonValide e) {
		}
	}

	/**
	 * Permet d'obtenir la route par défaut
	 * 
	 * @return la route
	 */
	public Route getDefaultGateway() {
		try {
			return getRoute(new IPv4("0.0.0.0"));
		} catch (IPNonValide e) {
		}
		return null;
	}

	/**
	 * Permet d'obtenir la liste des services disponible
	 * 
	 * @return la liste des services
	 */
	public LinkedList<Services> getServices() {
		return services;
	}
	
	/**
	 * Permet d'obtenir les informations des interfaces en format Html 
	 * pour pouvoir être affichée
	 * 
	 * @return les infos des interfaces
	 */
	public String toHtml(){
		String output = "";
		int id = 0;
		for (InterfaceIP i : getInterfacesIP()) {
			
			String gateway = "Not set";
			if (getDefaultGateway() != null){
				gateway = getDefaultGateway().getProchainSaut().toString();
			}
			
			output += "<hr><b>eth" + id + "</b><br/>"
					+ "Link encap.............: Ethernet<br/>"
					+ "MAC address.........: " + i.getMac() + "<br/>";
			

			if (i.getIp() != null) {
				output += "IPv4 Address.........: " + i.getIp() + "<br/>"
						+ "Subnet Mask..........: " + i.getIp().getMasque() + "<br/>"
						+ "Broadcast Address..: " + i.getIp().getBroadcast() + "<br/>"
						+ "Default Gateway.....: " + gateway + "<br/>";
			} else {			
				output += "IPv4 Address.........: " + "Not set" + "<br/>"
						+ "Subnet Mask..........: " + "Not set" + "<br/>"
						+ "Broadcast Address..: " + "Not set" + "<br/>" 
						+ "Default Gateway.....: " + gateway + "<br/>";
			}
			
			output += "<br/>";
			id++;
		}
		return output;
	}

	/**
	 * Permet d'enregistrer les informations du PC
	 * 
	 * @param out: le flux de sortie
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeInt(globalId);
		out.writeInt(id);
	}

	/**
	 * Permet de lire les informations du PC
	 * 
	 * @param in: le flux d'entrée
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		globalId = in.readInt();
		id = in.readInt();
	}

	/**
	 * Permet de reseter les id
	 */
	public static void reset() {
		globalId = 1;
	}
}
