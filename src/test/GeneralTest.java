package test;

import static org.junit.Assert.*;


import org.junit.Test;

import elements.PC;
import elements.Switch;

public class GeneralTest {

	/*
	 * Test RootBID pour le protocole STP !!!
	 */
	@Test
	public void definirSwitchRootSTP() {
		
		PC pc1 = new PC(1);
		PC pc2 = new PC(1);
		
		Switch sw1 = new Switch(4);
		Switch sw2 = new Switch(4);
		Switch sw3 = new Switch(4);
		Switch sw4 = new Switch(4);
		Switch sw5 = new Switch(4);
		
		// pc1[0] <-> sw1[0]
		pc1.getInterface()[0].setInterfaceDest(sw1.getInterface()[0]);
		sw1.getInterface()[0].setInterfaceDest(pc1.getInterface()[0]);
		
		// sw2[3] <-> sw1[1]
		sw2.getInterface()[3].setInterfaceDest(sw1.getInterface()[1]);
		sw1.getInterface()[1].setInterfaceDest(sw2.getInterface()[3]);

		// sw2[1] <-> sw3[0]
		sw2.getInterface()[1].setInterfaceDest(sw3.getInterface()[0]);
		sw3.getInterface()[0].setInterfaceDest(sw2.getInterface()[1]);

		//sw2[2] <-> pc2[0]
		pc2.getInterface()[0].setInterfaceDest(sw2.getInterface()[2]);
		sw2.getInterface()[2].setInterfaceDest(pc2.getInterface()[0]);

		
		//sw1[2] <-> sw3[2]
		sw1.getInterface()[2].setInterfaceDest(sw3.getInterface()[2]);
		sw3.getInterface()[2].setInterfaceDest(sw1.getInterface()[2]);
		
		//sw4[2] <-> sw5[0]
		sw4.getInterface()[2].setInterfaceDest(sw5.getInterface()[0]);
		sw5.getInterface()[0].setInterfaceDest(sw4.getInterface()[2]);
		
		//sw4[0] <-> sw3[1]
		sw4.getInterface()[0].setInterfaceDest(sw3.getInterface()[1]);
		sw3.getInterface()[1].setInterfaceDest(sw4.getInterface()[0]);

		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Resultat :");
		
		int i1 = -1, i2 = -1, i3 = -1, i4 = -1, i5 = -1;
		
		if(sw1.getTableSTP().getRootPort() != null) i1 = sw1.getTableSTP().getRootPort().getNumero();
		if(sw2.getTableSTP().getRootPort() != null) i2 = sw2.getTableSTP().getRootPort().getNumero();
		if(sw3.getTableSTP().getRootPort() != null) i3 = sw3.getTableSTP().getRootPort().getNumero();
		if(sw4.getTableSTP().getRootPort() != null) i4 = sw4.getTableSTP().getRootPort().getNumero();
		if(sw5.getTableSTP().getRootPort() != null) i5 = sw5.getTableSTP().getRootPort().getNumero();
		
		System.out.println("sw1 " + sw1.getTableSTP().getBid() + " \t: root ["  + i1 + "] " + sw1.getTableSTP().getRootBID() + " source " + sw1.getTableSTP().getSourceBID() + " metric " + sw1.getTableSTP().getMetric());
		System.out.println("sw2 " + sw2.getTableSTP().getBid() + " \t: root ["  + i2 + "] " + sw2.getTableSTP().getRootBID() + " source " + sw2.getTableSTP().getSourceBID() + " metric " + sw2.getTableSTP().getMetric());
		System.out.println("sw3 " + sw3.getTableSTP().getBid() + " \t: root ["  + i3 + "] " + sw3.getTableSTP().getRootBID() + " source " + sw3.getTableSTP().getSourceBID() + " metric " + sw3.getTableSTP().getMetric());
		System.out.println("sw4 " + sw4.getTableSTP().getBid() + " \t: root ["  + i4 + "] " + sw4.getTableSTP().getRootBID() + " source " + sw4.getTableSTP().getSourceBID() + " metric " + sw4.getTableSTP().getMetric());
		System.out.println("sw5 " + sw5.getTableSTP().getBid() + " \t: root ["  + i5 + "] " + sw5.getTableSTP().getRootBID() + " source " + sw5.getTableSTP().getSourceBID() + " metric " + sw5.getTableSTP().getMetric());
		
		for(int i=0; i < sw1.getInterface().length; i++)
			System.out.println("sw1["+ i + "] = " + sw1.getTableSTP().getStatePort(sw1.getInterface()[i]));
		for(int i=0; i < sw1.getInterface().length; i++)
			System.out.println("sw2["+ i + "] = " + sw2.getTableSTP().getStatePort(sw2.getInterface()[i]));		
		for(int i=0; i < sw1.getInterface().length; i++)
			System.out.println("sw3["+ i + "] = " + sw3.getTableSTP().getStatePort(sw3.getInterface()[i]));		
		for(int i=0; i < sw1.getInterface().length; i++)
			System.out.println("sw4["+ i + "] = " + sw4.getTableSTP().getStatePort(sw4.getInterface()[i]));		
		
		assertEquals(sw1.getTableSTP().getRootBID(), sw2.getTableSTP().getRootBID());
		assertEquals(sw2.getTableSTP().getRootBID(), sw3.getTableSTP().getRootBID());
		assertEquals(sw3.getTableSTP().getRootBID(), sw4.getTableSTP().getRootBID());
		assertEquals(sw4.getTableSTP().getRootBID(), sw5.getTableSTP().getRootBID());
		
	}
	
}
