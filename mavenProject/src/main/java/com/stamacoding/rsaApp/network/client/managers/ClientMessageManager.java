package com.stamacoding.rsaApp.network.client.managers;

import com.stamacoding.rsaApp.logger.L;
import com.stamacoding.rsaApp.network.client.services.ClientReceiveService;
import com.stamacoding.rsaApp.network.global.message.AbstractMessageManager;
import com.stamacoding.rsaApp.network.global.message.Message;

/**
 * Manages all stored, received, sending and sent messages. Provides methods to filter messages by their attributes.
 */
public class ClientMessageManager extends AbstractMessageManager{
	
	/** The only instance of this class */
	private volatile static ClientMessageManager singleton = new ClientMessageManager();

	/**
	 *  Creates an instance of this class. Gets automatically called once at the start to define the service's {@link #singleton}. Use {@link ClientReceiveService#getInstance()} to get the
	 *  only instance of this class.
	 */
	private ClientMessageManager() {
	}
	
	/**
	 * Gets the only instance of this class.
	 * @return the only instance of this class
	 */
	public static ClientMessageManager getInstance() {
		return singleton;
	}
	
	/**
	 * Gets a message from the {@link ClientMessageManager} that should get stored or updated in the client's chat database.
	 * Returns {@code null} if there is no message to store or update.
	 * @return the message that should get stored or updated
	 */
	public Message pollToStoreOrUpdate() {
		for(int i=0; i<getCurrentlyManagedMessages().size(); i++) {
			Message m = null;
			try {m = getCurrentlyManagedMessages().get(i);}catch(IndexOutOfBoundsException e){};
			
			if(m == null || m.getLocalData() == null) return null;
			if(m.getLocalData().isToStore() || m.getLocalData().isToUpdate()) {
				L.d(this.getClass(), "Found message to store or update: " + m.toString());
				if(!m.getLocalData().isToSend()) {
					getCurrentlyManagedMessages().remove(m);
					L.t(this.getClass(), "Removed message from message manager");
				}else {
					L.t(this.getClass(), "Didn't remove message from message manager because it also has to get sent");
				}
				return m;
			}
		}
		return null;
	}
	
	/**
	 * Gets a message from the {@link ClientMessageManager} that should get sent.
	 * Returns {@code null} if there is no message to store or update.
	 * @return the message that should get sent
	 */
	public Message pollToSend() {
		for(int i=0; i<getCurrentlyManagedMessages().size(); i++) {
			Message m = null;
			try {m = getCurrentlyManagedMessages().get(i);}catch(IndexOutOfBoundsException e){};
			if(m == null || m.getLocalData() == null) continue;
			if(m.getLocalData().isToSend()) {
				L.d(this.getClass(), "Found message to send: " + m.toString());
				if(!m.getLocalData().isToStore() && !m.getLocalData().isToUpdate()) {
					getCurrentlyManagedMessages().remove(m);
					L.t(this.getClass(), "Removed message from message manager");
				}else {
					L.t(this.getClass(), "Didn't remove message from message manager because it also has to be stored/updated");
				}
				return m;
			}
		}
		return null;
	}
}
