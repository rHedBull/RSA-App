package com.stamacoding.rsaApp.network.client.services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.stamacoding.rsaApp.logger.L;
import com.stamacoding.rsaApp.network.client.ClientConfig;
import com.stamacoding.rsaApp.network.global.service.Service;
import com.stamacoding.rsaApp.network.global.session.LoginState;
import com.stamacoding.rsaApp.network.global.session.Session;
import com.stamacoding.rsaApp.network.global.user.Password;
import com.stamacoding.rsaApp.network.global.user.User;
import com.stamacoding.rsaApp.network.server.services.LoginService;
import com.stamacoding.rsaApp.network.server.services.SignUpService;
import com.stamacoding.rsaApp.security.Security;

// TODO ServerServices (Login, Signup, Ping) needed, documentation needed, Config should be edited
public class SessionService extends Service{
	private final Session session = new Session(-1);

	/** The only instance of this class */
	private volatile static SessionService singleton = new SessionService();

	/**
	 *  Creates an instance of this class. Gets automatically called once at the start to define the service's {@link #singleton}. Use {@link ChatDatabaseService#getInstance()} to get the
	 *  only instance of this class.
	 */
	private SessionService() {
		if(ClientConfig.REGISTERED) getSession().setState(LoginState.SIGNED_IN);
	}
	
	/**
	 * Gets the only instance of this class.
	 * @return the only instance of this class
	 */
	public static SessionService getInstance() {
		return singleton;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		while(getSession().getState() == LoginState.NONE) {
			L.d(this.getClass(), "Trying to sign up (" + ClientConfig.USER_NAME + ", ************)");
			getSession().setState(signup());
			
			if(getSession().getState() == LoginState.SIGNED_IN) {
				L.i(this.getClass(), "Signed up successfully (" + ClientConfig.USER_NAME + ", ************)");
				ClientConfig.REGISTERED = true;
				ClientConfig.save();
			}else {
				L.w(this.getClass(), "Failed to sign up (" + ClientConfig.USER_NAME + ", ************)");
				try {
					Thread.sleep(ClientConfig.RETRY_SIGNUP_INTERVAL);
				} catch (InterruptedException e) {
					L.e(this.getClass(), "Failed to wait for retrying to sign up. The used thread was interrupted.", e);
				}
			}
		}
		
		while(getSession().getState() != LoginState.LOGGED_IN) {
			L.d(this.getClass(), "Trying to log in (" + ClientConfig.USER_NAME + ", ************)");
			getSession().setState(login());
			
			if(getSession().getState() == LoginState.LOGGED_IN) {
				L.i(this.getClass(), "Logged in successfull (" + ClientConfig.USER_NAME + ", ************)");
			}else {
				L.w(this.getClass(), "Failed to log in (" + ClientConfig.USER_NAME + ", ************)");
				try {
					Thread.sleep(ClientConfig.RETRY_LOGIN_INTERVAL);
				} catch (InterruptedException e) {
					L.e(this.getClass(), "Failed to wait for retrying to sign up. The used thread was interrupted.", e);
				}
			}
		}
	}
	
	@Override
	public void onRepeat() {
		super.onRepeat();
		ping();
	}

	private void ping() {
		try {
			Socket connectionToServer = new Socket(ClientConfig.SERVER_IP, ClientConfig.PING_PORT);
			
			L.d(this.getClass(), "Ping");
			DataOutputStream out = new DataOutputStream(connectionToServer.getOutputStream());
			out.writeLong(getSession().getId());
			out.close();
			
			connectionToServer.close();
			
			try {
				Thread.sleep(ClientConfig.PING_INTERVAL);
			} catch (InterruptedException e) {
				L.e(this.getClass(), "Failed to wait for pinging again.", e);
			}
		} catch (IOException e) {
			L.e(this.getClass(), "Connection error", e);
		}
	}

	private LoginState signup() {
		try {
			Socket connectionToServer = new Socket(ClientConfig.SERVER_IP, ClientConfig.SIGNUP_PORT);
			L.d(this.getClass(), "Connected to server successfully");
			
			L.d(this.getClass(), "Encrypting user information (" + ClientConfig.USER_NAME + ", ************)");
			byte[] you = Security.encryptF(new User(ClientConfig.USER_NAME, new Password(ClientConfig.USER_PASSWORD)));
			
			DataOutputStream out = new DataOutputStream(connectionToServer.getOutputStream());
			DataInputStream in = new DataInputStream(connectionToServer.getInputStream());
			
			L.d(this.getClass(), "Sending user information");
			out.writeInt(you.length);
			out.write(you);
			out.flush();
			
			L.d(this.getClass(), "Retrieving answer");
			int answer = in.readInt();
			
			L.d(this.getClass(), "Close connection to server");
			out.close();
			in.close();
			connectionToServer.close();
			
			switch(answer) {
			case SignUpService.AnswerCodes.USERNAME_UNAVAILABLE:
				L.e(this.getClass(), "Failed to sign up! Your username is already in use!");
				return LoginState.NONE;
			case SignUpService.AnswerCodes.INVALID_DATA_FROM_CLIENT:
				L.e(this.getClass(), "Failed to sign up! Received invalid data from client!");
				return LoginState.NONE;
			case SignUpService.AnswerCodes.SIGNED_UP:
				L.d(this.getClass(), "Signed-up successfully!");
				return LoginState.SIGNED_IN;
			}	
		} catch (IOException e) {
			L.e(this.getClass(), "Connection error", e);
		}
		
		return LoginState.NONE;
	}
	
	private LoginState login() {
		try {
			Socket connectionToServer = new Socket(ClientConfig.SERVER_IP, ClientConfig.LOGIN_PORT);
			L.d(this.getClass(), "Connected to server successfully");
			
			L.d(this.getClass(), "Encrypting user information (" + ClientConfig.USER_NAME + ", ************)");
			byte[] you = Security.encryptF(new User(ClientConfig.USER_NAME, new Password(ClientConfig.USER_PASSWORD)));
			
			DataOutputStream out = new DataOutputStream(connectionToServer.getOutputStream());
			DataInputStream in = new DataInputStream(connectionToServer.getInputStream());
			
			L.d(this.getClass(), "Sending user information");
			out.writeInt(you.length);
			out.write(you);
			out.flush();
			
			L.d(this.getClass(), "Retrieving answer");
			long answer = in.readLong();
			
			L.d(this.getClass(), "Close connection to server");
			out.close();
			in.close();
			connectionToServer.close();
			
			if(answer < 0) {
				int answerCode = (int) answer;
				switch(answerCode) {
				case LoginService.AnswerCodes.INVALID_DATA_FROM_CLIENT:
					L.e(this.getClass(), "Failed to log in! Server received invalid data from client.");
					return LoginState.SIGNED_IN;
				case LoginService.AnswerCodes.WRONG_USERNAME_PASSWORD:
					L.e(this.getClass(), "Failed to log in! Invalid password or username.");
					return LoginState.SIGNED_IN;
				}
			}else {
				getSession().setId(answer);
				return LoginState.LOGGED_IN;
			}
		} catch (IOException e) {
			L.e(this.getClass(), "Connection error", e);
		}
		
		return LoginState.SIGNED_IN;
	}

	public Session getSession() {
		return session;
	}
}

