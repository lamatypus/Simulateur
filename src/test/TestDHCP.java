package test;

import org.junit.Test;

import elements.ElementReseau;
import elements.PC;
import elements.Serveur;
import elements.config.ShellPC;

public class TestDHCP {

	@Test
	public void dhcp(){
		PC pc = new PC(1);
		Serveur serv = new Serveur(1);
		
		ElementReseau.connect(pc, serv, 0, 0);
		
		ShellPC sh1 = new ShellPC(pc);
		ShellPC sh2 = new ShellPC(serv);
		
		sh2.ordre("ip addr add 192.168.1.2/24 dev eth0");
		sh2.ordre("services start 1");
		
		sh1.ordre("services start 0");
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
