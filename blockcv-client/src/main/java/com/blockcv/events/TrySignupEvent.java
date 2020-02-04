package com.blockcv.events;

import java.util.Map;

import com.blockcv.model.UserModel.UserType;

public class TrySignupEvent {
	
	private final String email;
	private final String password;
	private final String repeat;
	private final Map<String,String> info;
	private final UserType userType;
	
	public TrySignupEvent(String e, String p, String r, Map<String,String> i, UserType ut) {
		email = e;
		password = p;
		repeat = r;
		info = i;
		userType = ut;
	}
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}
	public String getRepeat() {
		return repeat;
	}
	public Map<String,String> getInfo() {
		return info;
	}
	public UserType getUserType() {
		return userType;
	}
}
