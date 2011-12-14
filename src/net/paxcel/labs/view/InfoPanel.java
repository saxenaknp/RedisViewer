package net.paxcel.labs.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import net.paxcel.labs.controller.RedisConnectionController;
import redis.clients.jedis.Jedis;

public class InfoPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3337172547664352708L;
	private JTextPane output = new JTextPane();

	public InfoPanel(final String connectionId) {
		setLayout(new BorderLayout());
		output.setBorder(new EmptyBorder(new Insets(4, 4, 2, 4)));
		output.setEditable(false);
		add(new JScrollPane(output));
		Thread t = new Thread(new Runnable() {
			public void run() {
				while (true) {
					SimpleAttributeSet attributes = new SimpleAttributeSet();
					attributes.addAttribute(
							StyleConstants.CharacterConstants.Bold,
							Boolean.TRUE);
					attributes.addAttribute(StyleConstants.FontSize, 13);
					attributes.addAttribute(StyleConstants.Foreground,
							Color.BLUE.darker().darker());

					Jedis connection = null;
					try {
						connection = RedisConnectionController.getInstance()
								.getConnection(connectionId);
						String info = connection.info();
						output.getDocument().remove(0,
								output.getDocument().getLength());
						output.getDocument().insertString(
								output.getDocument().getLength(), info,
								attributes);
					} catch (Exception e) {
						try {
							output.getDocument().remove(0,
									output.getDocument().getLength());
						} catch (Exception e1) {
						}
						e.printStackTrace();
					} finally {
						if (connection != null) {
							RedisConnectionController
									.getInstance()
									.releaseConnection(connectionId, connection);
						}
					}
					try {
						Thread.sleep(5000);
					} catch (Exception e) {
					}
				}
			}
		});
		t.setDaemon(true);
		t.start();

	}
}
