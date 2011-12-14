package net.paxcel.labs.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import net.paxcel.labs.beans.Command;
import net.paxcel.labs.beans.Command.KeyType;
import redis.clients.jedis.Tuple;

public class OutputPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 166998404477695278L;
	private JTextPane output = new JTextPane();
	private static final String DASHED = "_______________________________________________________________________";

	public OutputPanel() {
		setLayout(new BorderLayout());
		output.setBorder(new EmptyBorder(new Insets(4, 4, 2, 4)));
		output.setEditable(false);
		add(new JScrollPane(output));
	}

	public void updateOutput(Command command) {
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		attributes.addAttribute(StyleConstants.CharacterConstants.Bold,
				Boolean.TRUE);
		attributes.addAttribute(StyleConstants.FontSize, 13);
		attributes.addAttribute(StyleConstants.Foreground, Color.BLACK.darker()
				.darker());

		try {
			output.getDocument().remove(0, output.getDocument().getLength());
			StringBuffer data = new StringBuffer("Key - "
					+ command.getCommandParameters().get(0) + "\n");
			data.append("Type - " + command.getKeyType().toString() + "\n");
			data.append("Command Executed - " + command.getCommandName()
					+ "\n\n\n");
			data.append("Output \n");
			output.getDocument().insertString(0, data.toString(), attributes);
			StringBuffer outputData = new StringBuffer(DASHED + "\n");
			int i = 1;
			JTable list = null;
			DefaultTableModel tableModel = new DefaultTableModel() {

				@Override
				public boolean isCellEditable(int row, int column) {
					// all cells not editable
					return false;
				}
			};
			if (command.getKeyType() == KeyType.list) {
				List<String> values = (List<String>) command.getOutput();
				outputData.append("#\tValue\n");
				for (String value : values) {
					outputData.append(i + ".\t" + value + "\n");
					i++;
				}
			}
			if (command.getKeyType() == KeyType.string) {
				String value = (String) command.getOutput();
				outputData.append(value + "\n");
			}
			if (command.getKeyType() == KeyType.hash) {
				Map<String, String> values = (Map<String, String>) command
						.getOutput();
				Set<String> keys = values.keySet();
				String outData[][] = new String[values.size()][3];
				for (String key : keys) {
					outData[i - 1][0] = "" + i;
					outData[i - 1][1] = "" + key;
					outData[i - 1][2] = "" + values.get(key);
					i++;
				}

				list = new JTable(outData, new Object[] { "#", "Hash Key",
						"Value" });
				list.setEnabled(false);
				TableColumn col = list.getColumnModel().getColumn(0);
				int width = 20;
				col.setPreferredWidth(width);
				col.setMaxWidth(width);
				
				JScrollPane p = new JScrollPane(list);
				output.insertComponent(p);
				output.setCaretPosition(0);
				return;
			}
			if (command.getKeyType() == KeyType.set) {
				Set<String> values = (Set<String>) command.getOutput();
				outputData.append("#\n");
				for (String value : values) {
					outputData.append(i + ".\t" + value + "\n");
					i++;
				}
			}
			if (command.getKeyType() == KeyType.zset) {
				Set<Tuple> values = (Set<Tuple>) command.getOutput();

				String outData[][] = new String[values.size()][3];
				for (Tuple tuple : values) {
					outData[i - 1][0] = "" + i;
					outData[i - 1][1] = "" + tuple.getElement();
					outData[i - 1][2] = "" + tuple.getScore();
					i++;
				}
				list = new JTable(outData,
						new Object[] { "#", "Value", "Score" });
				list.setEnabled(false);
				TableColumn col = list.getColumnModel().getColumn(0);
				int width = 20;
				col.setPreferredWidth(width);
				col.setMaxWidth(width);
				width = 100;
				col = list.getColumnModel().getColumn(2);
				col.setPreferredWidth(width);
				col.setMaxWidth(width);
				JScrollPane p = new JScrollPane(list);
				output.insertComponent(p);
				output.setCaretPosition(0);
				return;
			}

			outputData.append(DASHED);
			output.getDocument().insertString(output.getDocument().getLength(),
					outputData.toString(), attributes);
			output.setCaretPosition(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
