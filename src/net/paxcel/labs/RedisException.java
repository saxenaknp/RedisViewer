package net.paxcel.labs;

public class RedisException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6844905180959992465L;

	public RedisException() {
		this(null);
	}

	public RedisException(String ex) {
		this(ex, null);
	}

	public RedisException(String ex, Throwable t) {
		super(ex, t);
	}
}
