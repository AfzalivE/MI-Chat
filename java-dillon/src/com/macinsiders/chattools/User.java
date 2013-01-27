package com.macinsiders.chattools;

public class User {

	private long userID;
	private int userRole;
	private int channelID;
	private String userName;
	
	public User(long userID, int userRole, int channelID, String userName){
		
		this.userID = userID;
		this.userRole = userRole;
		this.channelID = channelID;
		this.userName = userName;
	}
	
	public long getUserID() {
		
		return userID;
	}
	
	public void setUserID(long userID) {
		
		this.userID = userID;
	}
	
	public int getUserRole() {
		
		return userRole;
	}
	
	public void setUserRole(int userRole) {
		
		this.userRole = userRole;
	}
	
	public int getChannelID() {
		
		return channelID;
	}
	
	public void setChannelID(int channelID) {
		
		this.channelID = channelID;
	}
	
	public String getUserName() {
		
		return userName;
	}
	
	public void setUserName(String userName) {
		
		this.userName = userName;
	}
}
