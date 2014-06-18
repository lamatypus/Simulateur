package test;

import org.junit.Test;

import elements.ElementReseau;
import elements.PC;
import elements.Routeur;
import elements.config.ConfigRouteur;
import elements.config.ShellPC;

public class RIP {
	
	@Test
	public void transmetRouteSimple(){
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
		c1.ordre("router rip");
		c1.ordre("version 2");
		c1.ordre("network 1.1.1.1");
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
		c2.ordre("router rip");
		c2.ordre("version 2");
		c2.ordre("network 1.1.1.2");
		c2.ordre("exit");
		c2.ordre("exit");
		c2.ordre("show ip interface brief");
		c2.ordre("show ip route");

		//sh1.ordre("ping 10.1.1.2");
		
		while (true){
			try {
				Thread.sleep(3000);
				c1.ordre("show ip route");
				c2.ordre("show ip route");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
