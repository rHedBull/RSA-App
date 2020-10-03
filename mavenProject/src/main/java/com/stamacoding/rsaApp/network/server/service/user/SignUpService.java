package com.stamacoding.rsaApp.network.server.service.user;

import java.io.IOException;

import com.stamacoding.rsaApp.logger.L;
import com.stamacoding.rsaApp.network.global.answerCodes.AnswerCodes;
import com.stamacoding.rsaApp.network.global.service.ServerSocketService;
import com.stamacoding.rsaApp.network.global.user.User;
import com.stamacoding.rsaApp.network.server.Config;
import com.stamacoding.rsaApp.network.server.Server;
import com.stamacoding.rsaApp.network.server.manager.UserManager;
import com.stamacoding.rsaApp.network.server.service.message.SendService;
import com.stamacoding.rsaApp.security.Security;
import com.stamacoding.rsaApp.security.rsa.Key;

public class SignUpService extends ServerSocketService {
	
	/** The only instance of this class */
	private volatile static SignUpService singleton = new SignUpService();

	/**
	 *  Creates an instance of this class. Gets automatically called once at the start to define the service's {@link #singleton}. Use {@link SendService#getInstance()} to get the
	 *  only instance of this class.
	 *  The server's port is set to {@link Server#SEND_PORT}.
	 */
	private SignUpService() {
		super(Config.SIGNUP_PORT);
	}
	
	/**
	 * Gets the only instance of this class.
	 * @return the only instance of this class
	 */
	public static SignUpService getInstance() {
		return singleton;
	}
	
	@Override
	public void onAccept() {
		try {
			L.t(getClass(), "Reading encrypted user data...");
			// Reading user
			int userLength = getInputStream().readInt();
			if(userLength > 0) {
				
				byte[] encryptedUser = new byte[userLength];
				getInputStream().readFully(encryptedUser, 0, userLength);
				
				L.t(this.getClass(), "Decrypting client's user data...");
				User unregisteredUser = (User) Security.decryptF(encryptedUser);
				
				// Reading public key
				L.t(this.getClass(), "Reading client's public key...");
				int keyLength = getInputStream().readInt();
				if(keyLength > 0) {
					byte[] publicKey = new byte[keyLength];
					getInputStream().readFully(publicKey, 0, keyLength);
					
					L.t(this.getClass(), "Decrypting client's public key...");
					Key publicKeyClient = (Key) Security.decryptF(publicKey);
					
					L.d(this.getClass(), "Client wants to register as: " + unregisteredUser.toString());
					L.d(this.getClass(), "The user's public key is: " + publicKeyClient.toString());
					
					if(UserDatabaseService.getInstance().isUsernameAvailable(unregisteredUser.getName())) {
						// Hash password
						unregisteredUser.getPassword().setSalt(Security.generatePasswordSalt(), false);
						
						UserManager.getInstance().add(unregisteredUser);
						storePublicKey(unregisteredUser.getName(), publicKeyClient);

						L.i(this.getClass(), "Registered new user (0): " + unregisteredUser.toString());
						L.d(this.getClass(), "Currently registered users:\n" + UserDatabaseService.getInstance().toString());
						
						getOutputStream().writeInt(AnswerCodes.SignUp.SIGNED_UP);
					}else {
						L.d(this.getClass(), "Username is already in use!");
						
						L.t(getClass(), "Sending error code to client...");
						getOutputStream().writeInt(AnswerCodes.SignUp.USERNAME_UNAVAILABLE);
						L.t(getClass(), "Sent error code to client!");
					}
				}else {
					L.e(this.getClass(), "Received invalid data (public key)");
					
					L.t(getClass(), "Sending error code to client...");
					getOutputStream().writeInt(AnswerCodes.SignUp.INVALID_DATA_FROM_CLIENT);
					L.t(getClass(), "Sent error code to client!");
				}
			}else {
				L.e(this.getClass(), "Received invalid data (user)");
				
				L.t(getClass(), "Sending error code to client...");
				getOutputStream().writeInt(AnswerCodes.SignUp.INVALID_DATA_FROM_CLIENT);
				L.t(getClass(), "Sent error code to client!");
			}
		} catch (IOException e) {
			L.e(this.getClass(), "Connection error", e);
		}

	}

	private void storePublicKey(String username, Key publicKeyClient) {
		L.t(getClass(), "Storing public key...");
		// TODO
	}
}
