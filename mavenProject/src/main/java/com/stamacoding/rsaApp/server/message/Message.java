package com.stamacoding.rsaApp.server.message;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import com.stamacoding.rsaApp.log.logger.Logger;
import com.stamacoding.rsaApp.rsa.RSA;
import com.stamacoding.rsaApp.server.message.data.LocalData;
import com.stamacoding.rsaApp.server.message.data.ProtectedData;
import com.stamacoding.rsaApp.server.message.data.ServerData;

/**
 *  An instance of this class represents a message with all its different attributes.
 */
public class Message implements Serializable{
	
	/** Auto-generated for serialization */
	private static final long serialVersionUID = 7382792471329161848L;
	
	/** The message's {@link LocalData} */
	private final LocalData localData;
	
	/** The message's {@link ProtectedData} */
	private ProtectedData protectedData;
	
	/** The message's {@link ProtectedData} as encrypted byte array */
	private byte[] encryptedProtectedData;
	
	/** The message's {@link ServerData} */
	private ServerData serverData;
	
	/** The message's {@link ServerData} as encrypted byte array */
	private byte[] encryptedServerData;
	
	
	/**
	 * Creates a fully decoded message. All parameters are not allowed to be {@code null}.
	 * @param localData the message's local data
	 * @param protectedData the message's protected data
	 * @param serverData the message's server data
	 */
	public Message(LocalData localData, ProtectedData protectedData, ServerData serverData) {
		if(localData == null) Logger.error(this.getClass().getSimpleName(),  new IllegalArgumentException("LocalData localData is not allowed to be null!"));
		
		this.localData = localData;
		setProtectedData(protectedData);
		setServerData(serverData);
	}
	
	/**
	 * Creates a fully encrypted message. All parameters are not allowed to be {@code null}.
	 * @param localData the message's local data (cannot be encoded)
	 * @param encryptedProtectedData the message's protected data as encrypted byte array
	 * @param encryptedServerData the message's server data as encrypted byte array
	 */
	public Message(LocalData localData, byte[] encryptedProtectedData, byte[] encryptedServerData) {
		if(localData == null) Logger.error(this.getClass().getSimpleName(),   new IllegalArgumentException("LocalData localData is not allowed to be null!"));
		
		this.localData = localData;
		setEncryptedProtectedData(encryptedProtectedData);
		setEncryptedServerData(encryptedServerData);
	}
	
	/**
	 * Creates a semi-encrypted message. Only the protected data is encrypted. All parameters are not allowed to be {@code null}.
	 * @param localData the message's local data
	 * @param encryptedProtectedData the message's protected data as encrypted byte array
	 * @param serverData the message's server data
	 */
	public Message(LocalData localData, byte[] encryptedProtectedData, ServerData serverData) {
		if(localData == null) Logger.error(this.getClass().getSimpleName(), new IllegalArgumentException("LocalData localData is not allowed to be null!"));
		
		this.localData = localData;
		setEncryptedProtectedData(encryptedProtectedData);
		setServerData(serverData);
	}

	/**
	 * Gets the message's {@link ProtectedData}.
	 *
	 * @return the message's protected data
	 */
	public ProtectedData getProtectedData() {
		return protectedData;
	}
	
	/**
	 * Sets the message's {@link ProtectedData}.
	 * @param serverData the message's {@link ProtectedData}
	 */
	private void setProtectedData(ProtectedData protectedData) {
		if(protectedData == null) Logger.error(this.getClass().getSimpleName(), new IllegalArgumentException("ProtectedData protectedData is not allowed to be null!"));
		
		this.protectedData = protectedData;
	}


	/**
	 * Gets the message's {@link ServerData}.
	 *
	 * @return the message's server data
	 */
	public ServerData getServerData() {
		return serverData;
	}
	
	/**
	 * Sets the message's {@link ServerData}.
	 * @param serverData the message's {@link ServerData}
	 */
	private void setServerData(ServerData serverData) {
		if(serverData == null) Logger.error(this.getClass().getSimpleName(),  new IllegalArgumentException("ServerData serverData is not allowed to be null!"));
		
		this.serverData = serverData;
	}
	
	/**
	 * Gets the message's {@link LocalData}.
	 *
	 * @return the message's {@link LocalData}
	 */
	public LocalData getLocalData() {
		return localData;
	}

	/**
	 * Gets the message's {@link ProtectedData} as encrypted byte array.
	 * @return the message's {@link ProtectedData} as encrypted byte array
	 */
	public byte[] getEncryptedProtectedData() {
		return encryptedProtectedData;
	}

	/**
	 * Sets the message's encrypted byte array that represents the {@link #protectedData}.
	 * @param encryptedProtectedData the message's encrypted byte array that represents the {@link #protectedData}
	 */
	private void setEncryptedProtectedData(byte[] encryptedProtectedData) {
		if(encryptedProtectedData == null) Logger.error(this.getClass().getSimpleName(), new IllegalArgumentException("byte[] encryptedProtectedData is not allowed to be null!"));
		
		this.encryptedProtectedData = encryptedProtectedData;
	}

	/**
	 * Gets the message's {@link ServerData} as encrypted byte array.
	 * @return the message's {@link ServerData} as encrypted byte array
	 */
	public byte[] getEncryptedServerData() {
		return encryptedServerData;
	}

	/**
	 * Sets the message's encrypted byte array that represents the {@link #serverData}.
	 * @param encryptedServerData the message's encrypted byte array that represents the {@link #serverData}
	 */
	private void setEncryptedServerData(byte[] encryptedServerData) {
		if(encryptedServerData == null) Logger.error(this.getClass().getSimpleName(), new IllegalArgumentException("byte[] encryptedServerData is not allowed to be null!"));
		
		this.encryptedServerData = encryptedServerData;
	}

	/**
	 * Encrypts the {@link #serverData}. {@link Message#getServerData()} returns {@link null} now while
	 * {@link Message#getEncryptedServerData()} returns a byte array.
	 */
	public void encryptServerData() {
		if(getServerData() == null) {
			Logger.warning(this.getClass().getSimpleName(), "ServerData is already encrypted!");
			return;
		}
		
		setEncryptedServerData(RSA.encryptF(getServerData()));
		this.serverData = null;
	}
	
	/**
	 * Decrypts the {@link #serverData}. {@link Message#getEncryptedServerData()} returns {@link null} now while
	 * {@link Message#getServerData()} returns a reference to an object.
	 */
	public void decryptServerData() {
		if(getServerData() != null) {
			Logger.warning(this.getClass().getSimpleName(), "ServerData is already decrypted!");
			return;
		}
		
		setServerData((ServerData) RSA.decryptF(getEncryptedServerData()));
		this.encryptedServerData = null;
	}
	
	/**
	 * Encrypts the {@link #protectedData}. {@link Message#getProtectedData()} returns {@link null} now while
	 * {@link Message#getEncryptedProtectedData()} returns a byte array.
	 */
	public void encryptProtectedData() {
		if(getProtectedData() == null) {
			Logger.warning(this.getClass().getSimpleName(), "ProtectedData is already encrypted!");
			return;
		}
		
		setEncryptedProtectedData(RSA.encryptF(getProtectedData()));
		this.protectedData = null;
	}
	
	/**
	 * Decrypts the {@link #protectedData}. {@link Message#getEncryptedProtectedData()} returns {@link null} now while
	 * {@link Message#getProtectedData()} returns a reference to an object.
	 */
	public void decryptProtectedData() {
		if(getProtectedData() != null) {
			Logger.warning(this.getClass().getSimpleName(), "ProtectedData is already decrypted!");
			return;
		}
		
		setProtectedData((ProtectedData) RSA.decryptF(getEncryptedProtectedData()));
		this.encryptedProtectedData = null;
	}
	
	/**
	 * Returns the message as string.
	 * @return the message as string
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(getLocalData().getId());
		sb.append("] (");
		if(getServerData() != null) {
			sb.append(getServerData().getSending());
			sb.append(") => (");
			sb.append(getServerData().getReceiving());
			sb.append("): \"");
		}else{
			sb.append("?) => (?): \"");
		}
		if(getProtectedData() != null) {
			sb.append(getProtectedData().getTextMessage());
			sb.append("\" (created at ");
			sb.append(new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(getProtectedData().getDate()));
			sb.append(")");
		}else {
			sb.append("????????\" (created at ????)");
		}
		sb.append(" [");
		sb.append(getLocalData().getSendState().toString());
		sb.append("]");
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj instanceof Message) {
			Message m2 = (Message) obj;
			if(this.getLocalData() == null) {
				if(m2.getLocalData() != null) return false;
			}else {
				if(!this.getLocalData().equals(m2.getLocalData())) return false;
			}
			
			if(this.getProtectedData() == null) {
				if(m2.getProtectedData() != null) return false;
			}else {
				if(!this.getProtectedData().equals(m2.getProtectedData())) return false;
			}
			
			if(this.getServerData() == null) {
				if(m2.getServerData() != null) return false;
			}else {
				if(!this.getServerData().equals(m2.getServerData())) return false;
			}
			
			if(!Arrays.equals(m2.getEncryptedProtectedData(), this.getEncryptedProtectedData())) return false;
			if(!Arrays.equals(m2.getEncryptedServerData(), this.getEncryptedServerData())) return false;
			
			return true;
		}
		return false;
	}
	
	public static void main(String[] args){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(int i=0; i<=3000000; i++) {
					Logger.debug("hhd", "test");
					try {
						Thread.sleep(12);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(int i=0; i<=3000000; i++) {
					Logger.debug("hhd", "test");
					try {
						Thread.sleep(12);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(int i=0; i<=3000000; i++) {
					Logger.debug("hhd", "test");
					try {
						Thread.sleep(12);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(int i=0; i<=3000000; i++) {
					Logger.debug("hhd", "test");
					try {
						Thread.sleep(12);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
