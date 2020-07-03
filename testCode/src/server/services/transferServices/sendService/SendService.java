package server.services.transferServices.sendService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.stamacoding.rsaApp.log.logger.Logger;

import server.config.NetworkConfig;
import server.config.Type;
import server.services.Service;
import server.services.transferServices.TransferMessage;

/**
 * {@link Service} sending all messages, that are in the {@link SendQueue}.
 */
public class SendService extends Service{
	/**
	 * the object's only instance (<b>see</b><a href="https://de.wikibooks.org/wiki/Muster:_Java:_Singleton"> singleton pattern</a>).
	 */
	private static volatile SendService singleton = new SendService();

	/**
	 * the object's private constructor (<b>see</b><a href="https://de.wikibooks.org/wiki/Muster:_Java:_Singleton"> singleton pattern</a>)
	 */
	private SendService() {
		super("send");
	}
	
	/**
	 * Gets the object's only instance (<b>see</b><a href="https://de.wikibooks.org/wiki/Muster:_Java:_Singleton"> singleton pattern</a>).
	 * @return the object's only instance
	 */
	public static SendService getInstance() {
		return singleton;
	}
	
	/**
	 * Runs the {@link SendService}.
	 * @see #runServer()
	 * @see #runClient()
	 */
	@Override
	public void run() {
		super.run();
		if(NetworkConfig.TYPE == Type.CLIENT) {
			runClient();
		}else if(NetworkConfig.TYPE == Type.SERVER) {
			runServer();
		}
	}

	/**
	 * Creates a {@link ServerSocket} waiting for clients requesting their messages from the server. 
	 * <ol>
	 * 	<li>If a client connects to the sever using a {@link Socket} and queries his messages, he tells the server his unique id.</li>
	 * 	<li>After that using this id the server searches for messages in the {@link SendQueue} that concern the client.</li>
	 * 	<li>If there are any messages the server sends them to the client using the {@link Socket}.</li>
	 * </ol>
	 */
	private void runServer() {
		ServerSocket sendServer = null;
		try {
			sendServer = new ServerSocket(NetworkConfig.Server.SEND_PORT);
			Logger.debug(this.getClass().getSimpleName(), "Successfully started send server");
		} catch (IOException e) {
			e.printStackTrace();
			Logger.error(this.getClass().getSimpleName(), "Failed to start send server");
		}
		while(!requestedShutDown()) {
			try {
				Socket connectionFromClient = sendServer.accept();
				Logger.debug(this.getClass().getSimpleName(), "Received client request");
				if(!SendQueue.isEmpty()) {
					DataInputStream inputStream = new DataInputStream(connectionFromClient.getInputStream());
					
					byte clientId = inputStream.readByte();
					
					ArrayList<TransferMessage> messagesToSendAsList = SendQueue.pollMessages(clientId);
					
					int messageCount = messagesToSendAsList.size();
					byte[] messagesToSend = TransferMessage.messageListToByteArray(messagesToSendAsList);
					Logger.debug(this.getClass().getSimpleName(), "Searching for messages that belong to the client (" + clientId + ")");
					DataOutputStream outputStream = new DataOutputStream(connectionFromClient.getOutputStream());
					
					// Send one message to the receiver TODO send multiple messages to the receiver
					if(messageCount > 0) {
						outputStream.writeInt(messagesToSend.length);
						outputStream.write(messagesToSend);
						
						Logger.debug(this.getClass().getSimpleName(), "Successfully sent " + messageCount + " message(s) to a client");
					}{
						Logger.debug(this.getClass().getSimpleName(), "Found no message!");
					}

				}
				connectionFromClient.close();
			} catch (IOException e) {
				e.printStackTrace();
				Logger.error(this.getClass().getSimpleName(), "Failed to send message to a client");
			}
		}
	}

	/**
	 * Sends a message from a client to the server if the {@link SendQueue} contains any entry.
	 *  <ol>
	 * 	<li>If the {@link SendQueue} is not empty, the oldest message gets polled.</li>
	 * 	<li>After that the client connect to the server using a {@link Socket}.</li>
	 * 	<li>Then the message gets sent.</li>
	 * </ol>
	 */
	private void runClient() {
		while(!requestedShutDown()) {
			// If there is a message to be sent
			if(!SendQueue.isEmpty()) {
				Logger.debug(this.getClass().getSimpleName(), "SendQueue is not empty");
				TransferMessage message = SendQueue.poll();
				byte[] messageAsByteArray = TransferMessage.messageToByteArray(message);
					
				Socket connectionToServer = null;
				try {
					connectionToServer = new Socket(NetworkConfig.Server.IP, NetworkConfig.Server.RECEIVE_PORT);
					Logger.debug(this.getClass().getSimpleName(), "Successfully conntected to the receive server");
				} catch (IOException e) {
					e.printStackTrace();
					Logger.error(this.getClass().getSimpleName(), "Failed to connect to receive server");
				}
				try {
					DataOutputStream outputStream = new DataOutputStream(connectionToServer.getOutputStream());
					
					// Send message to the receiver
					outputStream.writeInt(messageAsByteArray.length);
					outputStream.write(messageAsByteArray);
						
					Logger.debug(this.getClass().getSimpleName(), "Successfully sent message to the receive server");
					
					// Close connection to the receiver
					connectionToServer.close();
				} catch (IOException e) {
					e.printStackTrace();
					Logger.error(this.getClass().getSimpleName(), "Failed to send message to the receive server");
				}
			}
		}
	}
}