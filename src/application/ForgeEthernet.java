package application;

import paquets.PaquetEthernet;
import standards.MAC;
import elements.config.ShellPC;
import exception.MACNonValide;

/**********************************************************************
 * <p>
 * But:<br>
 * Application de test de la couche liaison
 * </p><p>
 * Description:<br>
 * Permet d'envoyer une trame ethernet sur le réseau. On peut forger 
 * l'adresse de destination.
 * 
 * </p>
 *
 * @author		Raphaël Buache
 * @author     	Magali Frölich
 * @author     	Cédric Rudareanu
 * @author     	Yann Malherbe
 * @version    	1.0
 * @modify	   	18.06.2014
 ***********************************************************************/
public class ForgeEthernet extends ExecutablePC{

	/**Ouvre l'application sur un terminal.
	 * L'argument de commande est soit "broadcast" soit une adresse MAC
	 * xx:xx:xx:xx:xx:xx
	 * 
	 * @param s		le terminal
	 * @param args	les arguments de commande
	 */
	public ForgeEthernet(ShellPC s, String[] args) {
		super(s);
		
		try {
			MAC tmp;
			
			if(args[1].equals("broadcast"))
				tmp = MAC.broadcast();
			else
				tmp = new MAC(args[1]);
			
			if(s.getPC().getInterface()[0].getInterfaceDest() == null){
				sortieConsole("interface dest not set\n");
			}
			else{
				PaquetEthernet p = new PaquetEthernet(s.getPC(), "Forge ethernet".getBytes(), null);
				p.setMacSource(s.getPC().getInterface()[0].getMac());
				p.setMacDest(tmp);
				p.envoie();

			}
			
		} catch (MACNonValide e) {
			sortieConsole("MAC invalid\n");
		}

			exit();
		
	}

	@Override
	public void entreeConsole(String entree) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controleC() {
		// TODO Auto-generated method stub
		
	}
}
