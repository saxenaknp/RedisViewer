package net.paxcel.labs.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import net.paxcel.labs.StorageException;
import net.paxcel.labs.beans.ConnectionDetail;

public class ConnectionSettingStorage {

	private List<ConnectionDetail> connections = new ArrayList<ConnectionDetail>();
	private String storageFile = "connection.properties";

	private static final ConnectionSettingStorage storage = new ConnectionSettingStorage();

	private ConnectionSettingStorage() {
		loadConnections();
	}

	public static ConnectionSettingStorage getInstance() {
		return storage;
	}

	public List<ConnectionDetail> getAllConnectionSettings() {
		return connections;
	}

	public void storeConnectionSetting(ConnectionDetail detail)
			throws StorageException {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					storageFile), true));
			String record = detail.getName() + "," + detail.getIp() + ","
					+ detail.getPort() + "," + detail.getPassword() + ","
					+ detail.getDatabaseId();
			writer.write(record);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (Exception e) {
			throw new StorageException("Failed to store ", e);
		}
		connections.add(detail);
	}

	private void loadConnections() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					storageFile)));
			System.out.println("Loaded Connections from " + new File (storageFile).getAbsolutePath());
			//FileReader out = new FileReader(new File(storageFile));
			String record = null;
			while ((record = reader.readLine()) != null) {
				String recordData[] = record.split(",");
				try {
					ConnectionDetail detail = new ConnectionDetail(
							recordData[0], recordData[1],
							Integer.parseInt(recordData[2]), recordData[3],
							Integer.parseInt(recordData[4]));
					connections.add(detail);
				} catch (Exception e) {
					// invalid record
				}

			}
			reader.close();
		} catch (Exception e) {
		}

	}
}
