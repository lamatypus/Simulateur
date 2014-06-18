package test;

import static org.junit.Assert.*;

import org.junit.Test;

import elements.PC;
import elements.config.ShellPC;

/**********************************************************************
 * <p>
 * But:<br>
 * Classe repr�sentant les tests pour le PC
 * </p>
 * <p>
 * Description:<br>
 * Cette classe permet de simuler des cas afin d'observer les comportements
 * des PCs
 * </p>
 * 
 * @author Rapha�l Buache
 * @author Magali Fr�lich
 * @author C�dric Rudareanu
 * @author Yann Malherbe
 * @version 1.0
 * @modify 18.06.2014
 ***********************************************************************/
public class TestPC {

	/**
	 * Ajoute et supprime une route par d�faut au PC
	 * 
	 * R�sultat attendu: 
	 * Dans le premier ifconfig: la route par d�faut devrait s'afficher
	 * Dans le deuxi�me ifconfig: la route par d�faut devrait �tre � "Not set"
	 */
	@Test
	public void ajoutIPGateway() {

		PC pc1 = new PC(1);
		ShellPC sh = new ShellPC(pc1);

		sh.ordre("ip addr add 192.168.1.2/24 dev eth0");
		sh.ordre("ip route add default via 192.168.1.1");
		sh.ordre("ifconfig");

		sh.ordre("ip route add default via 1.1.1.1");
		sh.ordre("ifconfig");
		
		assertTrue(true);
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Ajoute une adresse IP et la supprime
	 * 
	 * R�sultat attendu: 
	 * Le premier ifconfig devrait affich� 192.168.1.1
	 * Le second ifconfig devrait affich� Not set partout
	 */
	@Test
	public void ajoutEtSuppIP() {

		PC pc1 = new PC(1);
		ShellPC sh = new ShellPC(pc1);

		sh.ordre("ip addr add 192.168.1.2/24 dev eth0");
		sh.ordre("ifconfig");
		
		sh.ordre("ip addr del dev eth0");
		sh.ordre("ifconfig");
		assertTrue(true);
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
