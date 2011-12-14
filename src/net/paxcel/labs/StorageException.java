package net.paxcel.labs;

public class StorageException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6844905180959992465L;

	public StorageException() {
		this(null);
	}

	public StorageException(String ex) {
		this(ex, null);
	}

	public StorageException(String ex, Throwable t) {
		super(ex, t);
	}
}