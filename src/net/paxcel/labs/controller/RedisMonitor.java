package net.paxcel.labs.controller;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisMonitor;

public class RedisMonitor {

	private Jedis connection;
	private JTextPane pane;
	private boolean paused = false;
	private String connectionId;

	SimpleAttributeSet attributes = new SimpleAttributeSet();
	SimpleAttributeSet statusAttributes = new SimpleAttributeSet();
	SimpleAttributeSet errorAttributes = new SimpleAttributeSet();

	public RedisMonitor(String connectionId, final JTextPane pane) {
		this.connectionId = connectionId;
		this.pane = pane;
		attributes.addAttribute(StyleConstants.FontSize, 14);
		errorAttributes.addAttribute(StyleConstants.FontSize, 14);
		errorAttributes.addAttribute(StyleConstants.Foreground, Color.RED);
		errorAttributes.addAttribute(StyleConstants.Bold, Boolean.TRUE);

		statusAttributes.addAttribute(StyleConstants.FontSize, 14);
		statusAttributes.addAttribute(StyleConstants.Foreground,
				Color.BLUE.darker());
		statusAttributes.addAttribute(StyleConstants.Bold, Boolean.TRUE);

	}

	public void startMonitoring() {
		try {
			paused = false;
			connection = RedisConnectionController.getInstance().getConnection(
					connectionId);
			insertData("Started Monitoring \n", statusAttributes);
			connection.monitor(new JedisMonitor() {
				@Override
				public void onCommand(String command) {
					insertData(command + "\n", attributes);
				}
			});
		} catch (Exception e) {
			insertData(
					e.getMessage()
							+ " to "
							+ RedisConnectionController.getInstance()
									.getConnectionDetail(connectionId).getIp()
							+ ": "
							+ RedisConnectionController.getInstance()
									.getConnectionDetail(connectionId)
									.getPort() + "\n", errorAttributes);
			connection = null;
			try {
				Thread.sleep(5000);
			} catch (Exception e1) {
			}
			startMonitoring();
		}
	}

	public void insertData(String command, AttributeSet as) {
		try {
			if (!paused) {
				pane.getDocument().insertString(pane.getDocument().getLength(),
						command, as);
				pane.setCaretPosition(pane.getDocument().getLength());
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	public void pauseMonitoring() {
		paused = true;
		RedisConnectionController.getInstance().releaseConnection(connectionId,
				connection);
	}
}
