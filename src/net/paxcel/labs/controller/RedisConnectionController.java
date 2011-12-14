package net.paxcel.labs.controller;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import net.paxcel.labs.RedisException;
import net.paxcel.labs.beans.ConnectionDetail;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.SortingParams;

public class RedisConnectionController {

	private Map<String, RedisInfo> connectionInfo = new ConcurrentHashMap<String, RedisInfo>();

	private final static RedisConnectionController instance = new RedisConnectionController();

	public Jedis getConnection(String connectionId) throws RedisException {
		if (!connectionInfo.containsKey(connectionId)) {
			throw new RedisException(
					"Invalid Connection Id, Create Connection ID first to connect to REDIS");
		}
		Jedis connection = null;
		try {
			connection = connectionInfo.get(connectionId).getPool()
					.getResource();
			ConnectionDetail connectionDetails = connectionInfo.get(
					connectionId).getConnectionInfo();
			int index = connectionDetails.getDatabaseId();
			String out = null;
			if (connectionDetails.getPassword() != null
					&& connectionDetails.getPassword().length() > 0) {
				out = connection.auth(connectionDetails.getPassword());
				if (out == null || !out.equalsIgnoreCase("OK")) {
					throw new RedisException("Authentication failed to "
							+ connectionDetails.getIp() + ":"
							+ connectionDetails.getPort());
				}
			}
			if (connection.getDB() != index) {
				out = connection.select(index);
				if (out == null || !out.equalsIgnoreCase("OK")) {
					throw new RedisException("Failed to set db index "
							+ connectionDetails.getDatabaseId() + " at "
							+ connectionDetails.getIp() + ":"
							+ connectionDetails.getPort());
				}
			}
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RedisException("Error connecting ", e);
		}
	}

	public void releaseConnection(String connectionId, Jedis connection) {
		connectionInfo.get(connectionId).getPool().returnResource(connection);
	}

	public Map<String, RedisInfo> getAllConnections() {
		return connectionInfo;
	}

	public Set<String> getConnectionNames() {
		return connectionInfo.keySet();
	}

	public ConnectionDetail getConnectionDetail(String id) {
		RedisInfo info = connectionInfo.get(id);
		if (info != null) {
			return info.getConnectionInfo();
		}
		return null;
	}

	public void addConnection(ConnectionDetail connection)
			throws RedisException {
		try {
			Config config = new Config();
			config.minIdle = 10;
			config.testOnBorrow = true;
			config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_GROW;
			JedisPool pool = new JedisPool(config, connection.getIp(),
					connection.getPort());
			RedisInfo info = new RedisInfo(pool, connection);

			if (connection.getPassword() != null
					&& connection.getPassword().length() > 0) {
				String out = pool.getResource().auth(connection.getPassword());
				if (out == null || !out.equalsIgnoreCase("OK")) {
					throw new RedisException("Authentication failed to "
							+ connection.getIp() + ":" + connection.getPort());
				}
			}
			pool.getResource().connect();
			connectionInfo.put(connection.getName(), info);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RedisException("Error adding connection - " + e.getMessage(), e);
		}
	}

	public static RedisConnectionController getInstance() {
		return instance;

	}

	public void clearConnections() {
		if (connectionInfo.size() < 1) {
			return;
		}
		System.out.println("Destroying Redis Connections....");
		Set<String> keys = connectionInfo.keySet();
		for (String key : keys) {
			System.out.println("Destroying connection to connection id " + key);
			connectionInfo.get(key).getPool().destroy();
		}
		System.out.println("Destroyed Redis Connections....");
	}

	static class RedisInfo {
		private JedisPool pool;
		private ConnectionDetail connectionInfo;

		public RedisInfo(JedisPool pool, ConnectionDetail connectionInfo) {
			this.pool = pool;
			this.connectionInfo = connectionInfo;
		}

		public ConnectionDetail getConnectionInfo() {
			return connectionInfo;
		}

		public JedisPool getPool() {
			return pool;
		}

	}

	public static void main(String args[]) throws RedisException {
		RedisConnectionController controller = new RedisConnectionController();
		// controller.addConnection("first", "localhost", 6379);
		System.out.println(controller.getConnection("first").set("temp", "1"));
		System.out.println(controller.getConnection("first").get("temp"));
		System.out.println(controller.getConnection("first").incr("temp"));
		System.out.println(controller.getConnection("first").get("temp"));

		controller.getConnection("first").lpush("1",
				System.currentTimeMillis() + "");
		controller.getConnection("first").lpush("1",
				System.currentTimeMillis() + "");
		controller.getConnection("first").lpush("1",
				System.currentTimeMillis() + "");
		controller.getConnection("first").lpush("1",
				System.currentTimeMillis() + "");
		controller.getConnection("first").lrange("1", 0, -1);
		SortingParams params = new SortingParams();
		params.desc();
		System.out.println(controller.getConnection("first").sort("1", params,
				"2"));
		System.out
				.println(controller.getConnection("first").lrange("2", 0, -1));

	}

}
