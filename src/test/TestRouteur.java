package test;

import static org.junit.Assert.*;

import org.junit.Test;

import elements.ElementReseau;
import elements.PC;
import elements.Routeur;
import elements.config.ConfigRouteur;
import elements.config.ShellPC;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe représentant les tests pour le routeur
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler des cas afin d'observer les comportements
 * des routeurs
 * </p>
 * 
 * @author Raphaël Buache
 * @author Magali Frölich
 * @author Cédric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public class TestRouteur {

	/**
	 * Test l'ajout et la suppression de route statique
	 * 
	 * Résultat attendu: 2 show ip route sont effectuer le premier devrait
	 * affiché une route de 1.1.1.1/24 vers 2.2.2.2 et le deuxième devrait être
	 * vide
	 */
	@Test
	public void ajoutSupprRoute() {

		Routeur r1 = new Routeur(4);
		ConfigRouteur c1 = new ConfigRouteur(r1);

		c1.ordre("enable");
		c1.ordre("configure terminal");
		c1.ordre("ip route 1.1.1.1 255.255.255.0 2.2.2.2");
		c1.ordre("exit");
		c1.ordre("exit");
		c1.ordre("show ip route");
		c1.ordre("enable");
		c1.ordre("configure terminal");
		c1.ordre("no ip route 1.1.1.1 255.255.255.0 2.2.2.2");
		c1.ordre("exit");
		c1.ordre("exit");
		c1.ordre("show ip route");
		assertTrue(true);
	}

	/**
	 * Test l'attribution d'une adresse IP aux interface
	 * 
	 * Résultat attendu: 2 show ip route et 2 show ip interface brief on lieu
	 * dans la première partie on devrait observer que : fa 0/0 à l'adresse IP
	 * 192.168.1.1/24 fa 0/1 à l'adresse IP 10.192.168.1/8 fa 0/2 à l'adresse IP
	 * 176.92.83.1/16 et que les routes suivantes ont été ajoutée C
	 * 192.168.1.1/24 vers fa 0/0 L 192.168.1.1/32 vers fa 0/0 C 10.192.168.1/8
	 * vers fa 0/1 L 10.192.168.1/32 vers fa 0/1 C 176.92.83.1/16 vers fa 0/2 L
	 * 176.92.83.1/32 vers fa 0/2
	 * 
	 * Dans la deuxième partie les information relative à l'interface fa 0/0
	 * doivent avoir disparu soit:
	 * 
	 * fa 0/1 à l'adresse IP 10.192.168.1/8 fa 0/2 à l'adresse IP 176.92.83.1/16
	 * et pour les routes: C 10.192.168.1/8 vers fa 0/1 L 10.192.168.1/32 vers
	 * fa 0/1 C 176.92.83.1/16 vers fa 0/2 L 176.92.83.1/32 vers fa 0/2
	 * 
	 */
	@Test
	public void ajoutIp() {
		Routeur r1 = new Routeur(4);
		ConfigRouteur c1 = new ConfigRouteur(r1);

		c1.ordre("enable");
		c1.ordre("configure terminal");
		c1.ordre("interface FastEthernet 0/0");
		c1.ordre("ip address 192.168.1.1 255.255.255.0");
		c1.ordre("exit");
		c1.ordre("interface FastEthernet 0/1");
		c1.ordre("ip address 10.192.168.1 255.0.0.0");
		c1.ordre("exit");
		c1.ordre("interface FastEthernet 0/2");
		c1.ordre("ip address 176.92.83.1 255.255.0.0");
		c1.ordre("exit");
		c1.ordre("exit");
		c1.ordre("exit");
		c1.ordre("show ip interface brief");
		c1.ordre("show ip route");
		c1.ordre("enable");
		c1.ordre("configure terminal");
		c1.ordre("interface FastEthernet 0/0");
		c1.ordre("no ip address");
		c1.ordre("exit");
		c1.ordre("exit");
		c1.ordre("exit");
		c1.ordre("show ip interface brief");
		c1.ordre("show ip route");
		assertTrue(true);
	}

	/**
	 * Test des commandes shutdown et no shutdown
	 * 
	 * Résultat attendu: 2 show ip interface brief Le premier doit affiché fa
	 * 0/0 192.168.1.1 Manual up down fa 0/1 10.192.168.1 Manual up down
	 * 
	 * Le second doit affiché fa 0/0 unassigned unset down down fa 0/1
	 * 10.192.168.1 Manual up down
	 */
	@Test
	public void shutNoshut() {
		Routeur r1 = new Routeur(4);
		Routeur r2 = new Routeur(4);
		ElementReseau.connect(r1, r2, 0, 0);
		ConfigRouteur c1 = new ConfigRouteur(r1);

		c1.ordre("enable");
		c1.ordre("configure terminal");
		c1.ordre("interface FastEthernet 0/0");
		c1.ordre("ip address 192.168.1.1 255.255.255.0");
		c1.ordre("no shutdown");
		c1.ordre("exit");
		c1.ordre("interface FastEthernet 0/1");
		c1.ordre("ip address 10.192.168.1 255.0.0.0");
		c1.ordre("no shutdown");
		c1.ordre("exit");
		c1.ordre("exit");
		c1.ordre("exit");
		c1.ordre("show ip interface brief");
		c1.ordre("enable");
		c1.ordre("configure terminal");
		c1.ordre("interface FastEthernet 0/0");
		c1.ordre("no ip address");
		c1.ordre("exit");
		c1.ordre("interface FastEthernet 0/1");
		c1.ordre("shutdown");
		c1.ordre("exit");
		c1.ordre("exit");
		c1.ordre("exit");
		c1.ordre("show ip interface brief");
		assertTrue(true);
	}

	/**
	 * Test le ping entre deux PCs de réseau différent à travers 1 routeur
	 * 
	 * Résultat attendu: Le PC1 devrait recevoir des réponses ping
	 */
	@Test
	public void pingTravers1Routeur() {
		PC pc1 = new PC(1);
		PC pc2 = new PC(1);
		Routeur r1 = new Routeur(2);

		ElementReseau.connect(pc1, r1, 0, 0);
		ElementReseau.connect(pc2, r1, 0, 1);

		ConfigRouteur c1 = new ConfigRouteur(r1);
		ShellPC sh1 = new ShellPC(pc1);
		ShellPC sh2 = new ShellPC(pc2);

		sh1.ordre("ip addr add 192.168.1.2/24 dev eth0");
		sh1.ordre("ip route add default via 192.168.1.1");
		sh1.ordre("ifconfig");

		sh2.ordre("ip addr add 10.1.1.2/8 dev eth0");
		sh2.ordre("ip route add default via 10.1.1.1");
		sh2.ordre("ifconfig");

		c1.ordre("enable");
		c1.ordre("configure terminal");
		c1.ordre("interface FastEthernet 0/0");
		c1.ordre("ip address 192.168.1.1 255.255.255.0");
		c1.ordre("no shutdown");
		c1.ordre("exit");
		c1.ordre("interface FastEthernet 0/1");
		c1.ordre("ip address 10.1.1.1 255.0.0.0");
		c1.ordre("no shutdown");
		c1.ordre("exit");
		c1.ordre("exit");
		c1.ordre("exit");
		c1.ordre("show ip interface brief");
		c1.ordre("show ip route");

		sh1.ordre("ping 10.1.1.2");

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test le ping entre un PC et un routeur
	 * 
	 * Résultat attendu: Le PC1 devrait recevoir des réponses ping de la part du
	 * routeur
	 */
	@Test
	public void ping1Routeur() {
		PC pc1 = new PC(1);
		PC pc2 = new PC(1);
		Routeur r1 = new Routeur(2);

		ElementReseau.connect(pc1, r1, 0, 0);
		ElementReseau.connect(pc2, r1, 0, 1);

		ConfigRouteur c1 = new ConfigRouteur(r1);
		ShellPC sh1 = new ShellPC(pc1);
		ShellPC sh2 = new ShellPC(pc2);

		sh1.ordre("ip addr add 192.168.1.2/24 dev eth0");
		sh1.ordre("ip route add default via 192.168.1.1");
		sh1.ordre("ifconfig");

		sh2.ordre("ip addr add 10.1.1.2/8 dev eth0");
		sh2.ordre("ip route add default via 10.1.1.1");
		sh2.ordre("ifconfig");

		c1.ordre("enable");
		c1.ordre("configure terminal");
		c1.ordre("interface FastEthernet 0/0");
		c1.ordre("ip address 192.168.1.1 255.255.255.0");
		c1.ordre("no shutdown");
		c1.ordre("exit");
		c1.ordre("interface FastEthernet 0/1");
		c1.ordre("ip address 10.1.1.1 255.0.0.0");
		c1.ordre("no shutdown");
		c1.ordre("exit");
		c1.ordre("exit");
		c1.ordre("exit");
		c1.ordre("show ip interface brief");
		c1.ordre("show ip route");

		sh1.ordre("ping 10.1.1.1");

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test le ping entre deux PCs de réseau différent à travers 2 routeurs
	 * 
	 * Résultat attendu: Le PC1 devrait recevoir des réponses ping
	 */
	@Test
	public void pingTravers2Routeurs() {
		PC pc1 = new PC(1);
		PC pc2 = new PC(1);
		Routeur r1 = new Routeur(2);
		Routeur r2 = new Routeur(2);

		ElementReseau.connect(pc1, r1, 0, 0);
		ElementReseau.connect(r1, r2, 1, 1);
		ElementReseau.connect(pc2, r2, 0, 0);

		ConfigRouteur c1 = new ConfigRouteur(r1);
		ConfigRouteur c2 = new ConfigRouteur(r2);
		ShellPC sh1 = new ShellPC(pc1);
		ShellPC sh2 = new ShellPC(pc2);

		sh1.ordre("ip addr add 192.168.1.2/24 dev eth0");
		sh1.ordre("ip route add default via 192.168.1.1");
		sh1.ordre("ifconfig");

		sh2.ordre("ip addr add 10.1.1.2/8 dev eth0");
		sh2.ordre("ip route add default via 10.1.1.1");
		sh2.ordre("ifconfig");

		c1.ordre("enable");
		c1.ordre("configure terminal");
		c1.ordre("interface FastEthernet 0/0");
		c1.ordre("ip address 192.168.1.1 255.255.255.0");
		c1.ordre("no shutdown");
		c1.ordre("exit");
		c1.ordre("interface FastEthernet 0/1");
		c1.ordre("ip address 1.1.1.1 255.255.255.0");
		c1.ordre("no shutdown");
		c1.ordre("exit");
		c1.ordre("ip route 10.1.1.1 255.0.0.0 1.1.1.2");
		c1.ordre("exit");
		c1.ordre("exit");
		c1.ordre("show ip interface brief");
		c1.ordre("show ip route");

		c2.ordre("enable");
		c2.ordre("configure terminal");
		c2.ordre("interface FastEthernet 0/0");
		c2.ordre("ip address 10.1.1.1 255.0.0.0");
		c2.ordre("no shutdown");
		c2.ordre("exit");
		c2.ordre("interface FastEthernet 0/1");
		c2.ordre("ip address 1.1.1.2 255.255.255.0");
		c2.ordre("no shutdown");
		c2.ordre("exit");
		c2.ordre("ip route 192.168.1.1 255.255.255.0 1.1.1.1");
		c2.ordre("exit");
		c2.ordre("exit");
		c2.ordre("show ip interface brief");
		c2.ordre("show ip route");

		sh1.ordre("ping 10.1.1.2");

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test le ping entre deux PCs de réseau différent à travers 2 routeurs
	 * 
	 * Résultat attendu: Le PC1 devrait recevoir des réponses ping
	 */
	@Test
	public void routeurChoisiEntrePlusieursRouteurs() {
		PC pc1 = new PC(1);
		PC pc2 = new PC(1);
		PC pc3 = new PC(1);
		PC pc4 = new PC(1);
		Routeur r1 = new Routeur(4);

		ElementReseau.connect(pc1, r1, 0, 0);
		ElementReseau.connect(pc2, r1, 0, 1);
		ElementReseau.connect(pc3, r1, 0, 2);
		ElementReseau.connect(pc4, r1, 0, 3);

		ConfigRouteur c1 = new ConfigRouteur(r1);

		ShellPC sh1 = new ShellPC(pc1);
		ShellPC sh2 = new ShellPC(pc2);
		ShellPC sh3 = new ShellPC(pc3);
		ShellPC sh4 = new ShellPC(pc4);

		sh1.ordre("ip addr add 1.1.1.2/24 dev eth0");
		sh1.ordre("ip route add default via 1.1.1.1");
		sh1.ordre("ifconfig");

		sh2.ordre("ip addr add 2.2.2.2/24 dev eth0");
		sh2.ordre("ip route add default via 2.2.2.1");
		sh2.ordre("ifconfig");
		
		sh3.ordre("ip addr add 3.3.3.3/24 dev eth0");
		sh3.ordre("ip route add default via 3.3.3.1");
		sh3.ordre("ifconfig");
		
		sh4.ordre("ip addr add 4.4.4.4/24 dev eth0");
		sh4.ordre("ip route add default via 4.4.4.1");
		sh4.ordre("ifconfig");

		c1.ordre("enable");
		c1.ordre("configure terminal");
		
		c1.ordre("interface FastEthernet 0/0");
		c1.ordre("ip address 1.1.1.1 255.255.255.0");
		c1.ordre("no shutdown");
		c1.ordre("exit");
		
		c1.ordre("interface FastEthernet 0/1");
		c1.ordre("ip address 2.2.2.1 255.255.255.0");
		c1.ordre("no shutdown");
		c1.ordre("exit");
		
		c1.ordre("interface FastEthernet 0/2");
		c1.ordre("ip address 3.3.3.1 255.255.255.0");
		c1.ordre("no shutdown");
		c1.ordre("exit");
		
		c1.ordre("interface FastEthernet 0/3");
		c1.ordre("ip address 4.4.4.1 255.255.255.0");
		c1.ordre("no shutdown");
		c1.ordre("exit");

		c1.ordre("exit");
		c1.ordre("exit");
		c1.ordre("show ip interface brief");
		c1.ordre("show ip route");

		sh1.ordre("ping 4.4.4.4");

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
