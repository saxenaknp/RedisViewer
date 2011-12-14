package net.paxcel.labs.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import net.paxcel.labs.beans.Command;
import net.paxcel.labs.beans.Command.CommandType;
import net.paxcel.labs.controller.CommandExecutor;

public class CommandPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8873730604484078643L;

	private JTextField commandText;
	private JButton filter;
	private JList keys;
	private JScrollPane pane;
	private JLabel commandLabel;
	private DefaultListModel model = new DefaultListModel();
	private String connectionId;
	private OutputPanel output;
	private CommandExecutor executor;

	public CommandPanel(String connectionId, OutputPanel output) {
		setLayout(new BorderLayout());
		this.connectionId = connectionId;
		this.output = output;
		initialize();
	}

	private void initialize() {
		commandText = new JTextField(16);
		filter = new JButton("Filter");
		commandLabel = new JLabel("Keys");
		commandText.setText("*");
		keys = new JList(model);
		keys.setOpaque(false);
		keys.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JPanel top = new JPanel();
		top.add(commandLabel);
		top.add(commandText);
		top.add(filter);
		JPanel outKeys = new JPanel();
		executor = new CommandExecutor(connectionId);
		keys.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				List<String> params = new ArrayList<String>();
				int index = keys.getSelectedIndex();
				if (index < 0) {
					return;
				}
				params.add(model.get(index).toString());
				Command command = new Command(CommandType.GET_VALUE, null,
						params);
				executor.executeCommand(command);
				output.updateOutput(command);

			}
		});

		pane = new JScrollPane(keys);
		keys.setCellRenderer(new KeysCellRenderer());
		outKeys.add(pane);
		add(top, BorderLayout.NORTH);
		add(outKeys, BorderLayout.CENTER);
		// keys.setPreferredSize(new Dimension(270, 600));
		pane.setPreferredSize(new Dimension(270, 550));
		filter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				updateKeys();
			}
		});
		commandText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateKeys();
			}
		});
//		updateKeys();
	}

	private void updateKeys() {
		model.clear();
		List<String> params = new ArrayList<String>();
		String param = commandText.getText();
		if (param == null || param.length() < 1) {
			param = "*";
			commandText.setText("*");
		}
		params.add(param);
		Command command = new Command(CommandType.KEYS, "keys ", params);
		executor.executeCommand(command);
		Set<String> keys = (Set<String>) command.getOutput();

		resetKeys(keys);

	}

	public void resetKeys(Set<String> keys) {

		for (String key : keys) {
			model.addElement(key);
		}
		this.keys.invalidate();
	}

	private class KeysCellRenderer extends DefaultListCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JComponent c = (JComponent) super.getListCellRendererComponent(
					list, value, index, isSelected, cellHasFocus);
			c.setOpaque(false);
			if (isSelected) {
				c.setBorder(new BevelBorder(BevelBorder.RAISED, Color.GRAY,
						Color.LIGHT_GRAY.brighter().brighter()));
			} else {
				c.setBorder(new EmptyBorder(1, 1, 1, 1));
			}
			c.setForeground(Color.BLACK);
			return c;
		}

	}

}
