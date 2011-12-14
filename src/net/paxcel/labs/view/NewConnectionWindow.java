package net.paxcel.labs.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import net.paxcel.labs.RedisClient;
import net.paxcel.labs.beans.ConnectionDetail;
import net.paxcel.labs.controller.RedisConnectionController;

public class NewConnectionWindow extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6431757691055840206L;
	private JLabel ipLabel = new JLabel("Host");
	private JLabel nameLabel = new JLabel("Connection Name");
	private JLabel portLabel = new JLabel("Port");
	private JLabel passwordLabel = new JLabel("Password");
	private JLabel dbIndexLabel = new JLabel("Database Index");
	private JTextField ipText = new JTextField("localhost", 20);
	private JTextField nameText = new JTextField("", 20);

	private JTextField portText = new JTextField(new NumericDocument(), "6379",
			20);
	private JTextField passwordText = new JPasswordField(20);
	private JTextField dbIndexText = new JTextField(new NumericDocument(), "0",
			20);

	private JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

	private JPanel ipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel dbIndexPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	private JButton connect = new JButton("Connect");
	private JButton cancel = new JButton("Cancel");
	private RedisClient client;

	public NewConnectionWindow(RedisClient client) {
		super (client.getFrame(), "Create New Connection", true);
		this.client = client;
		ipLabel.setPreferredSize(new Dimension(100, 15));
		portLabel.setPreferredSize(new Dimension(100, 15));
		passwordLabel.setPreferredSize(new Dimension(100, 15));
		dbIndexLabel.setPreferredSize(new Dimension(100, 15));
		nameLabel.setPreferredSize(new Dimension(100, 15));

		ipText.selectAll();
		nameText.selectAll();
		portText.selectAll();
		passwordText.selectAll();
		dbIndexText.selectAll();
		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		namePanel.add(nameLabel);
		namePanel.add(nameText);
		ipPanel.add(ipLabel);
		ipPanel.add(ipText);
		portPanel.add(portLabel);
		portPanel.add(portText);
		dbIndexPanel.add(dbIndexLabel);
		dbIndexPanel.add(dbIndexText);
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordText);
		buttonPanel.add(connect);
		buttonPanel.add(cancel);
		getContentPane().add(namePanel);
		getContentPane().add(ipPanel);
		getContentPane().add(portPanel);
		getContentPane().add(dbIndexPanel);
		getContentPane().add(passwordPanel);
		getContentPane().add(buttonPanel);
		int i = 1;
		String name = "Connection " + i;
		boolean found = false;
		while (!found) {
			Set<String> connections = RedisConnectionController.getInstance()
					.getConnectionNames();
			if (connections.contains(name)) {
				name = "Connection " + (++i);
			} else {
				found = true;
			}
		}
		nameText.setText(name);
		setSize(400, 250);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		NewConnectionListener listener = new NewConnectionListener();
		connect.addActionListener(listener);
		cancel.addActionListener(listener);
		getRootPane().setDefaultButton(connect);
		setVisible(true);
	}

	private class NewConnectionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == cancel) {
				dispose();
				return;
			}
			if (e.getSource() == connect) {
				if (!valid()) {
					return;
				}
				ConnectionDetail connection = new ConnectionDetail(
						nameText.getText(), ipText.getText(),
						Integer.parseInt(portText.getText()),
						passwordText.getText(), Integer.parseInt(dbIndexText
								.getText()));

				try {
					RedisConnectionController.getInstance().addConnection(
							connection);
					client.onNewConnection(connection.getName());
					dispose();
					return;
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(NewConnectionWindow.this,
							"Failed to connect " + ex.getMessage());
					ex.printStackTrace();
					return;
				}
			}

		}
	}

	private boolean valid() {

		if (nameText.getText() == null || nameText.getText().length() < 1) {
			JOptionPane.showMessageDialog(this,
					"Required value (Connection Name) missing ");
			return false;
		}
		if (RedisConnectionController.getInstance().getConnectionDetail(
				nameText.getText()) != null) {
			JOptionPane.showMessageDialog(this, "Connection with this name "
					+ nameText.getText() + " already exists");
			return false;

		}
		if (ipText.getText() == null || ipText.getText().length() < 1) {
			JOptionPane.showMessageDialog(this, "Required value (IP) missing ");
			return false;
		}

		if (portText.getText() == null || portText.getText().length() < 1) {
			JOptionPane.showMessageDialog(this,
					"Required value (Port) missing ");
			return false;
		}

		return true;
	}

	private class NumericDocument extends PlainDocument {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7911622137307863172L;

		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			if (str == null)
				return;
			try {
				Integer.parseInt(str);
				super.insertString(offs, str, a);
			} catch (NumberFormatException e) {
			}
		}
	}
}
