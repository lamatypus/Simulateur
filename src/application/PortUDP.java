package application;

public enum PortUDP {
	DHCPCLIENT(68), 
	DHCPSERVEUR(67), 
	RIP(521);

	private int port;

	private PortUDP (int port) {
		this.port = port;
	}

	public int port() {
		return port;
	}
}
