package com.stamacoding.rsaApp.server.session;

public class Session {
	private long id;
	private LoginState state = LoginState.NONE;
	
	public Session(long id) {
		setId(id);
	}
	
	public Session(long id, LoginState state) {
		setId(id);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	

	public LoginState getState() {
		return state;
	}

	public void setState(LoginState state) {
		this.state = state;
	}
}