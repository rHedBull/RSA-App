package com.stamacoding.rsaApp.server.user;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import com.stamacoding.rsaApp.log.logger.Logger;

public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4715340391545299952L;
	
	
	private long id = -1;
	private String name = null;
	private String password = null;
	private boolean updateRequested = false;
	
	public User(String name, String password) {
		setName(name);
		setPassword(password);
	}
	
	public User(long id, String name, String password) {
		setId(id);
		setName(name);
		setPassword(password);
	}
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(name == null) Logger.error(this.getClass().getSimpleName(), new IllegalArgumentException("String name is not allowed to be null!"));
		
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if(password == null) Logger.error(this.getClass().getSimpleName(), new IllegalArgumentException("String password is not allowed to be null!"));
		
		this.password = password;
	}

	public void setId(long id) {
		if(id == -1) Logger.debug(this.getClass().getSimpleName(), "User is set as unstored (id == -1)");
		if(id < -1) Logger.error(this.getClass().getSimpleName(), new IllegalArgumentException("int id (" + id +  ") should be greater than -2 !"));
		
		this.id = id;
	}

	public boolean isUpdateRequested() {
		return updateRequested;
	}

	public void setUpdateRequested(boolean updateRequested) {
		this.updateRequested = updateRequested;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}
	
	public boolean isStored() {
		return (getId() >= 0);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(getId());
		sb.append("] ( \"");
		sb.append(getName());
		sb.append("\" § \"");
		sb.append(getPassword());
		sb.append("\" ) [");
		sb.append(isUpdateRequested());
		sb.append("]");
		return sb.toString();
	}
}
