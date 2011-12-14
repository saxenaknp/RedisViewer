package net.paxcel.labs.view;

import java.awt.BorderLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import net.paxcel.labs.controller.RedisMonitor;

public class MonitorPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3337172547664352708L;
	private JTextPane output = new JTextPane();

	public MonitorPanel(final String connectionId) {
		setLayout(new BorderLayout());

		output.setEditable(false);
		output.setBorder(new EmptyBorder(new Insets(4, 4, 2, 4)));
		add(new JScrollPane(output));
		Thread t = new Thread(new Runnable() {

			public void run() {
				new RedisMonitor(connectionId, output).startMonitoring();
			}
		});
		t.setDaemon(true);
		t.start();

	}

}
