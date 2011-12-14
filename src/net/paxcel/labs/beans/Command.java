package net.paxcel.labs.beans;

import java.util.List;

public class Command {

	public enum CommandType {
		GET_VALUE, KEYS;
	}

	public enum KeyType {
		list, string, set, hash, zset;
	}

	private KeyType keyType;

	private String commandName;
	private List<String> commandParameters;
	private Object output;
	private CommandType commandType;

	public Command(CommandType type, String command, List<String> params) {
		this.commandName = command;
		this.commandParameters = params;
		this.commandType = type;
	}

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	public List<String> getCommandParameters() {
		return commandParameters;
	}

	public void setCommandParameters(List<String> commandParameters) {
		this.commandParameters = commandParameters;
	}

	public Object getOutput() {
		return output;
	}

	public void setOutput(Object output) {
		this.output = output;
	}

	public CommandType getCommandType() {
		return commandType;
	}

	public void setCommandType(CommandType commandType) {
		this.commandType = commandType;
	}

	public KeyType getKeyType() {
		return keyType;
	}

	public void setKeyType(KeyType keyType) {
		this.keyType = keyType;
	}

	@Override
	public String toString() {
		return "Command [keyType=" + keyType + ", commandName=" + commandName
				+ ", commandParameters=" + commandParameters + ", output="
				+ output + ", commandType=" + commandType + "]";
	}

}
