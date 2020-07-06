package server.services;

/**
 * The message's send state.
 *
 */
public enum SendState {
	/** Message should get sent. **/
	PENDING, 
	/** Message is sent successfully. **/
	SENT
}
