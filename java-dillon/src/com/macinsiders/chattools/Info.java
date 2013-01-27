package com.macinsiders.chattools;

public class Info {

	private long userID;
	private String userName;
	private int userRole;
	private int channelID;
	private String channelName;
	
	public Info(long userID, String userName, int userRole, int channelID, String channelName){
		
		this.userID = userID;
		this.userName = userName;
		this.userRole = userRole;
		this.channelID = channelID;
		this.channelName = channelName;
	}
	
	public long getUserID() {
		return userID;
	}
	
	public void setUserID(long userID) {
		this.userID = userID;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
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
	
	public String getChannelName() {
		return channelName;
	}
	
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
}
