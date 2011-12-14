package net.paxcel.labs.beans;

public class ConnectionDetail {

	private String name;
	private String ip;
	private int port;
	private String password;
	private int databaseId;

	public ConnectionDetail() {
	}

	public ConnectionDetail(String name, String ip, int port, String password,
			int databaseId) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.password = password;
		this.databaseId = databaseId;
	}

	public String getName() {
		return name;
	}

	public void setName(String id) {
		this.name = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(int databaseId) {
		this.databaseId = databaseId;
	}

	@Override
	public String toString() {
		return "ConnectionDetail [id=" + name + ", ip=" + ip + ", port=" + port
				+ ", password=" + password + ", databaseId=" + databaseId + "]";
	}

}
