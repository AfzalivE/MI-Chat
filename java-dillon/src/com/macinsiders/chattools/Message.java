package com.macinsiders.chattools;
import java.util.Date;

public class Message {

	private long id;
	private Date datetime;
	private long userID;
	private int userRole;
	private int channelID;
	private String messageContent;
	private String userName;
	
	public Message(long id, Date datetime, long userID, int userRole, int channelID, String messageContent, String userName){
		
		this.id = id;
		this.datetime = datetime;
		this.userID = userID;
		this.userRole = userRole;
		this.channelID = channelID;
		this.messageContent = messageContent;
		this.userName = userName;
	}
	
	public long getId() {
		
		return id;
	}
	
	public void setId(long id) {
		
		this.id = id;
	}
	
	public Date getDatetime() {
		
		return datetime;
	}
	
	public void setDatetime(Date datetime) {
		
		this.datetime = datetime;
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
	
	public String getMessageContent() {
		
		return messageContent;
	}
	
	public void setMessageContent(String messageContent) {
		
		this.messageContent = messageContent;
	}

	public String getUserName() {
		
		return userName;
	}

	public void setUserName(String userName) {
		
		this.userName = userName;
	}
}
