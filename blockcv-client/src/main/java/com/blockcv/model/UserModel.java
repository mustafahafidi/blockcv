package com.blockcv.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric_ca.sdk.Attribute;

import com.blockcv.model.data.DataAccess;

public class UserModel implements Model {
	
    private static final Logger log = Logger.getLogger(UserModel.class);
	
	public enum UserType {
		WORKER,
		ORGANIZATION
	}
	
	private String id;
	private String email;
	private String password;
	private UserType userType;
	private Map<String,String> personalInfo;
	boolean loggedIn;
	
	private HFClient hfClient;
	

	public UserModel() {
		this.loggedIn = false;
	}
	
	
	public UserModel(String ID, String email, String password, UserType userType, Map<String,String> personalInfo,  HFClient pHfClient) {
		this.id = ID;
		this.email = email;
		this.password = password;
		this.userType = userType;
		this.personalInfo = personalInfo;
		this.loggedIn = true;
		this.hfClient = pHfClient;
	}
	
	public UserType getUserType() {
		return userType;
	}
	
	/* operations related methods */
	public static boolean tryLogin(String email, String pass)  {
		return DataAccess.getInstance().tryLogin(email, pass).isLoggedIn();			
	}
	
	public boolean tryLogin()  {
		UserModel user = DataAccess.getInstance().tryLogin(email, password);
		setData(user);
		return loggedIn;
	}

	public static boolean trySignup(String email, String pass, Map<String, String> personalInfo, UserType userType) {
		return DataAccess.getInstance().trySignup(email, pass, personalInfo, userType);
	}
	
	/* class related methods */
	public void reset() {
		this.email = "-";
		this.password = "-";
		this.userType = null;
		this.personalInfo = null;
		this.hfClient = null;
		this.loggedIn=false;
	}

	public void setData(UserModel um) {
		this.id = um.id;
		this.email = um.email;
		this.password = um.password;
		this.userType = um.userType;
		this.personalInfo = um.personalInfo;
		this.loggedIn = um.loggedIn;
		this.hfClient = um.hfClient;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public UserModel setEmail(String email) {
		this.email = email;
		return this;
	}
	public String getPassword() {
		return password;
	}
	public UserModel setPassword(String password) {
		this.password = password;
		return this;
	}
	public Map<String, String> getPersonalInfo() {
		return personalInfo;
	}
	public void setPersonalInfo(Map<String, String> personalInfo) {
		this.personalInfo = personalInfo;
	}
	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	public boolean isLoggedIn() {
		return loggedIn;
	}
	
	
	public HFClient getHfClient() {
		return hfClient;
	}

	public void setHfClient(HFClient hfClient) {
		this.hfClient = hfClient;
	}
	
	public static String generateUserId(String email) {
		return DigestUtils.md5Hex(email).toUpperCase();
	}
	
	@Override
	public String toString() {
		StringBuilder st = new StringBuilder();
		st.append("ID:"+id+" Email:"+email+"UserType: "+userType.name()+" logged:"+loggedIn);
		personalInfo.forEach((k,v) -> st.append("\n"+k+":"+v));
		return st.toString();
	}
	
}
