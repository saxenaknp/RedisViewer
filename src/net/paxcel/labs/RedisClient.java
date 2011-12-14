package net.paxcel.labs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.paxcel.labs.beans.ConnectionDetail;
import net.paxcel.labs.controller.RedisConnectionController;
import net.paxcel.labs.storage.ConnectionSettingStorage;
import net.paxcel.labs.view.ContainerPanel;
import net.paxcel.labs.view.NewConnectionWindow;

public class RedisClient {

	private static final String BASE_STRING = "REDIS Client - ";
	private static final String COMPANY = " [Paxcel Technologies Pvt Ltd]";
	private JFrame client = new JFrame(BASE_STRING + COMPANY);
	private JTabbedPane pane = new JTabbedPane();
	private JMenuBar menuBar = new JMenuBar();
	private JMenu connections = new JMenu("Connections");

	public RedisClient() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		JMenu connection = new JMenu("Connection");
		JMenuItem newConnection = new JMenuItem("New Connection", KeyEvent.VK_N);
		newConnection.addActionListener(new NewConnectionEvent());

		menuBar.add(connection);
		JMenuItem exit = new JMenuItem("Exit", KeyEvent.VK_X);
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.dispose();
			}
		});
		
		connection.add(newConnection);
		newConnection.setAccelerator(KeyStroke.getKeyStroke('N',
				KeyEvent.CTRL_DOWN_MASK));
		connection.add(connections);
		connection.add(exit);
		client.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		client.setExtendedState(JFrame.MAXIMIZED_BOTH);
		client.setSize(new Dimension(600, 400));
		client.setLocationRelativeTo(null);
		client.getContentPane().add(pane, BorderLayout.CENTER);
		client.setJMenuBar(menuBar);
		List<ConnectionDetail> connections = ConnectionSettingStorage
				.getInstance().getAllConnectionSettings();
		if (connections != null) {
			for (ConnectionDetail con : connections) {
				try {
					RedisConnectionController.getInstance().addConnection(con);
					addNewConnectionMenu(con);
				} catch (Exception e) {
				}

			}
		}

		client.setVisible(true);
	}

	public static void main(String args[]) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			public void run() {
				RedisConnectionController.getInstance().clearConnections();
			}
		}));
		new RedisClient();

	}

	private class NewConnectionEvent implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			new NewConnectionWindow(RedisClient.this);
		}
	}

	public void addNewConnectionMenu(ConnectionDetail connection) {
		JMenuItem item = new JMenuItem(connection.getName());
		item.setToolTipText("IP " + connection.getIp() + ", Port "
				+ connection.getPort() + ", Database Index "
				+ connection.getDatabaseId());
		connections.add(item);
		item.addActionListener(new NewTabEvent(connection.getName()));
	}

	private class NewTabEvent implements ActionListener {
		String connectionId;

		public NewTabEvent(String name) {
			this.connectionId = name;
		}

		public void actionPerformed(ActionEvent e) {
			ContainerPanel container = new ContainerPanel(connectionId);
			ConnectionDetail details = RedisConnectionController.getInstance()
					.getConnectionDetail(connectionId);
			if (details != null) {
				pane.addTab(connectionId, null, container,
						"IP " + details.getIp() + ", Port " + details.getPort()
								+ ", Database Index " + details.getDatabaseId());
			} else {
				JOptionPane.showMessageDialog(null,
						"Failed to create connection");
				return;
			}
			pane.setSelectedIndex(pane.getTabCount() - 1);
		}
	}

	public void onNewConnection(String connectionId) {
		ContainerPanel container = new ContainerPanel(connectionId);
		ConnectionDetail details = RedisConnectionController.getInstance()
				.getConnectionDetail(connectionId);
		if (details != null) {
			pane.addTab(connectionId, null, container, "IP " + details.getIp()
					+ ", Port " + details.getPort() + ", Database Index "
					+ details.getDatabaseId());
		} else {
			JOptionPane.showMessageDialog(null, "Failed to create connection");
			return;
		}
		addNewConnectionMenu(details);
		try {
			ConnectionSettingStorage.getInstance().storeConnectionSetting(
					details);
		} catch (StorageException e) {
			JOptionPane.showMessageDialog(client,
					"Unable to store connetion settings. " + e.getMessage());
		}

		pane.setSelectedIndex(pane.getTabCount() - 1);
	}

	public JFrame getFrame() {
		return client;
	}
}
