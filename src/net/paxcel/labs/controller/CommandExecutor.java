package net.paxcel.labs.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.paxcel.labs.beans.Command;
import net.paxcel.labs.beans.Command.CommandType;
import net.paxcel.labs.beans.Command.KeyType;
import redis.clients.jedis.Jedis;

public class CommandExecutor {

	private List<Command> commands = new ArrayList<Command>();
	private String connectionId;

	public CommandExecutor(String connectionId) {
		this.connectionId = connectionId;
		Thread t = new Thread(new RedisCommandExecutor());
		t.setDaemon(true);
		t.start();
	}

	public void addCommand(Command command) {
		synchronized (commands) {
			this.commands.add(command);
			commands.notify();
		}
	}

	private class RedisCommandExecutor implements Runnable {

		public void run() {
			while (true) {
				if (CommandExecutor.this.commands.size() < 1){
					synchronized (CommandExecutor.this.commands) {
						try {
							CommandExecutor.this.commands.wait();
						} catch (InterruptedException e) {
						}
					}
				}
				Command[] commands = new Command[CommandExecutor.this.commands
						.size()];
				synchronized (CommandExecutor.this.commands) {
					commands = CommandExecutor.this.commands.toArray(commands);
					CommandExecutor.this.commands.clear();
					CommandExecutor.this.commands.notify();
				}
				for (Command command : commands) {
					System.out.println("Executing command "
							+ command.getCommandName());
				}
			}

		}
	}

	public void executeCommand(Command command) {
		Jedis connection = null;
		try {
			connection = RedisConnectionController.getInstance().getConnection(
					connectionId);
			if (command.getCommandType() == CommandType.KEYS) {
				Set<String> keys = connection.keys(command
						.getCommandParameters().get(0));
				command.setOutput(keys);
			} else {
				String key = command.getCommandParameters().get(0);
				String type = connection.type(key);
				command.setOutput(getValue(connection, key, type, command));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				RedisConnectionController.getInstance().releaseConnection(
						connectionId, connection);
			}
		}
	}

	public Object getValue(Jedis connection, String key, String type,
			Command command) throws Exception {
		if (type.equalsIgnoreCase("list")) {
			command.setKeyType(KeyType.list);
			command.setCommandName("lrange " + key + " 0 -1");
			return connection.lrange(key, 0, -1);
		}
		if (type.equalsIgnoreCase("string")) {
			command.setKeyType(KeyType.string);
			command.setCommandName("get " + key);
			return connection.get(key);
		}

		if (type.equalsIgnoreCase("hash")) {
			command.setCommandName("hgetall " + key);
			command.setKeyType(KeyType.hash);
			return connection.hgetAll(key);
		}

		if (type.equalsIgnoreCase("set")) {
			command.setCommandName("smembers " + key);
			command.setKeyType(KeyType.set);
			return connection.smembers(key);
		}

		if (type.equalsIgnoreCase("zset")) {
			command.setKeyType(KeyType.zset);
			command.setCommandName("zrange " + key + " 0 -1");
			return connection.zrangeWithScores(key, 0, -1);
		}

		return null;
	}
}
