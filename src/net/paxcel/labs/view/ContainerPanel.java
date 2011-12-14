package net.paxcel.labs.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;

public class ContainerPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2832616420852578370L;
	private CommandPanel commandPanel;
	private InfoPanel infoPanel;
	private MonitorPanel monitorPanel;
	private OutputPanel outputPanel;
	private JSplitPane commandOutput = new JSplitPane(
			JSplitPane.HORIZONTAL_SPLIT);
	JSplitPane outputInfo = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private JSplitPane outputMonitor = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

	private String connectionId;

	public ContainerPanel(String connectionId) {
		super(new BorderLayout());
		this.connectionId = connectionId;
		initialize();

	}

	private void initialize() {
		outputPanel = new OutputPanel();
		commandPanel = new CommandPanel(connectionId, outputPanel);
		monitorPanel = new MonitorPanel(connectionId);
		infoPanel = new InfoPanel(connectionId);
		add(commandOutput, BorderLayout.CENTER);
		commandOutput.add(commandPanel);
		commandOutput.add(outputMonitor);
		outputMonitor.add(outputInfo);
		outputMonitor.add(monitorPanel);
		outputMonitor.setDividerSize(5);
		commandOutput.setDividerSize(5);
		outputInfo.setDividerSize(5);
		commandOutput.setResizeWeight(.15D);
		outputMonitor.setResizeWeight(.7D);
		outputInfo.setResizeWeight(.8D);
		outputInfo.add(new JScrollPane(outputPanel));
		outputInfo.add(new JScrollPane(infoPanel));
		JPanel status = new JPanel();
		status.setPreferredSize(new Dimension(600, 30));
		status.setBorder(new CompoundBorder(
				new BevelBorder(BevelBorder.LOWERED), new BevelBorder(
						BevelBorder.RAISED)));
		add(status, BorderLayout.SOUTH);
	}
}
