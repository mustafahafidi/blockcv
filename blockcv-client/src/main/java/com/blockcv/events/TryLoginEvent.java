package com.blockcv.events;

public class TryLoginEvent {
	
	private final String email;
	private final String password;
	
	public TryLoginEvent(String e, String p) {
		email = e;
		password = p;
	}
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}
}
