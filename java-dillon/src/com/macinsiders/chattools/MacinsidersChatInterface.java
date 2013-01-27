package com.macinsiders.chattools;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MacinsidersChatInterface {

	public static final SimpleDateFormat dateParser = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
	
	private HttpContext userState = null;
	private boolean loggedIn = false;
	
	private long lastMessageID = 0;
	
	private List<User> users = null;
	private List<Message> messages = null;
	private Info info = null;
	
	public boolean login(String username, String password){
		
		String passwordMD5 = DigestUtils.md5Hex(password);
		
		Map<String, String> parameters = new HashMap<String, String>();
		
		parameters.put("url", "http://www.macinsiders.com/login.php?do=login");
		parameters.put("type", HTTPExecutor.POST_INTERFACE);
		
		List<NameValuePair> formParameters = new LinkedList<NameValuePair>();
		
		formParameters.add(new BasicNameValuePair("vb_login_username", username));
		formParameters.add(new BasicNameValuePair("vb_login_password", ""));
		formParameters.add(new BasicNameValuePair("cookieuser", "1"));
		formParameters.add(new BasicNameValuePair("s", ""));
		formParameters.add(new BasicNameValuePair("do", "login"));
		formParameters.add(new BasicNameValuePair("vb_login_md5password", passwordMD5));
		formParameters.add(new BasicNameValuePair("vb_login_md5password_utf", passwordMD5));
		
		HttpEntity entity = null;
		
		Map<String, Object> response = null;
		
		try {
			entity = new UrlEncodedFormEntity(formParameters);

			response = HTTPExecutor.execute(parameters, null, entity, userState);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			
			e.printStackTrace();
			return false;
		}
		
		CookieStore cookies = (CookieStore)((HttpContext) response.get(HTTPExecutor.RespState)).getAttribute(ClientContext.COOKIE_STORE);
		
		// If the 'bbpassword' cookie exists, then login was successful.
		for(Cookie c : cookies.getCookies())
			if(c.getName().toLowerCase().equals("bbpassword")){
				
				userState = (HttpContext) response.get(HTTPExecutor.RespState);
				loggedIn = true;
				break;
			}
		
		if(loggedIn == false)
			return false;
		
		parameters.put("url", "http://www.macinsiders.com/chat/");
		parameters.put("type", HTTPExecutor.GET_INTERFACE);
		parameters.put("ajax", "true");
		parameters.put("lastID", "0");
		parameters.put("getInfos", "userID,userName,userRole,channelID,channelName");
		parameters.put("channelID", "0");
		
		try {
			response = HTTPExecutor.execute(parameters, null, null, userState);
			updateStatus((String) response.get(HTTPExecutor.RespBody));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public void updateStatus(String xmlUpdate){
		
		if(!loggedIn)
			return;
		
		Document root = generateRootNode(xmlUpdate);
		
		NodeList dataNodeSet = root.getElementsByTagName("root").item(0).getChildNodes();
		
		for(int i = 0; i < dataNodeSet.getLength(); i++){
			
			Node dataComponent = dataNodeSet.item(i);
			
			if(dataComponent.getNodeName().equals("users"))
				users = processUsers(dataComponent);
			else if(dataComponent.getNodeName().equals("messages"))
				messages = processMessages(dataComponent);
			else if(dataComponent.getNodeName().equals("infos"))
				info = processInfo(dataComponent);
		}
	}

	private Document generateRootNode(String xml) {
		
		try {
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			
			db = dbf.newDocumentBuilder();
			
			InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			
			Document doc = db.parse(is);
			
			return doc;
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void updateStatus(){
		
		if(!loggedIn)
			return;
		
		Map<String, String> parameters = new HashMap<String, String>();
		
		parameters.put("url", "http://www.macinsiders.com/chat/?ajax=true&lastID=" + Long.toString(lastMessageID));
		parameters.put("type", HTTPExecutor.GET_INTERFACE);
		
		Map<String, Object> response = null;
		
		try {
			
			response = HTTPExecutor.execute(parameters, null, null, userState);
			updateStatus((String) response.get(HTTPExecutor.RespBody));
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public List<User> getUserlist(){
		
		return users;
	}
	
	public List<Message> getIncomingMessages(){
		
		return messages;
	}
	
	public void sendMessage(String message){
		
		if(!loggedIn)
			return;
		
		Map<String, String> parameters = new HashMap<String, String>();
		
		parameters.put("url", "http://www.macinsiders.com/chat/?ajax=true");
		parameters.put("type", HTTPExecutor.POST_INTERFACE);
		
		List<NameValuePair> formParameters = new LinkedList<NameValuePair>();
		
		formParameters.add(new BasicNameValuePair("lastID", info.getUserName()));
		formParameters.add(new BasicNameValuePair("text", message));
		
		HttpEntity entity = null;
		
		Map<String, Object> response = null;
		
		try {
			entity = new UrlEncodedFormEntity(formParameters);

			response = HTTPExecutor.execute(parameters, null, entity, userState);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// The response of the post contains update information, so send that for processing.
		updateStatus((String) response.get(HTTPExecutor.RespBody));
	}
	
	public Info getUserInfo(){
		
		return info;
	}
	
	private Info processInfo(Node infoNodeSet){
		
		NodeList infoNodes = infoNodeSet.getChildNodes();
		
		long userID = 0;
		String userName = null;
		int userRole = 0;
		int channelID = 0;
		String channelName = null;
		
		for(int i = 0; i < infoNodes.getLength(); i++){
			
			Node infoNode = infoNodes.item(i);
			String type = infoNode.getAttributes().getNamedItem("type").getNodeValue();
			
			String textContent = infoNode.getTextContent();
			
			userID = (!type.equals("userID"))? userID : Long.parseLong(textContent);
			userName = (!type.equals("userName"))? userName : infoNode.getTextContent();
			userRole = (!type.equals("userRole"))? userRole : Integer.parseInt(textContent);
			channelID = (!type.equals("channelID"))? userRole : Integer.parseInt(textContent);
			channelName = (!type.equals("channelName"))? userName : infoNode.getTextContent();
		}
		
		return new Info(userID, userName, userRole, channelID, channelName);
	}
	
	private List<User> processUsers(Node userNode){
		
		NodeList activeUsers = userNode.getChildNodes();
		
		List<User> users = new LinkedList<User>();
		
		for(int j = 0; j < activeUsers.getLength(); j++){
			
			userNode = activeUsers.item(j);
			
			NamedNodeMap userAttributes = userNode.getAttributes();
			
			long userID = Long.parseLong(userAttributes.getNamedItem("userID").getTextContent());
			int userRole = Integer.parseInt(userAttributes.getNamedItem("userRole").getTextContent());
			int channelID = Integer.parseInt(userAttributes.getNamedItem("channelID").getTextContent());
			String userName = userNode.getTextContent();
			
			User user = new User(userID, userRole, channelID, userName);
			
			users.add(user);
		}
		
		return users;
	}
	
	private List<Message> processMessages(Node messageNode) {

		NodeList incomingMessages = messageNode.getChildNodes();
		
		List<Message> messages = new LinkedList<Message>();
		
		for(int j = 0; j < incomingMessages.getLength(); j++){
			
			messageNode = incomingMessages.item(j);
			
			NamedNodeMap userAttributes = messageNode.getAttributes();
			
			long messageID = Long.parseLong(userAttributes.getNamedItem("id").getTextContent());
			long userID = Integer.parseInt(userAttributes.getNamedItem("userID").getTextContent());
			int userRole = Integer.parseInt(userAttributes.getNamedItem("userRole").getTextContent());
			int channelID = Integer.parseInt(userAttributes.getNamedItem("channelID").getTextContent());
			
			Date datetime = null;
			
			try {
				
				datetime = dateParser.parse(userAttributes.getNamedItem("dateTime").getTextContent());
				
				String messageContent = null;
				String userName = null;
				
				if(messageNode.getFirstChild().getNodeName().equals("username")){
					
					messageContent = StringEscapeUtils.unescapeHtml4((messageNode.getLastChild().getTextContent()));
					userName = messageNode.getFirstChild().getTextContent();
				}
				else {
					
					messageContent = messageNode.getFirstChild().getTextContent();
					userName = messageNode.getLastChild().getTextContent();
				}
				
				Message message = new Message(messageID, datetime, userID, userRole, channelID, messageContent, userName);
				
				int beforeSize = messages.size();
				
				if(messageID > lastMessageID)
					lastMessageID = messageID;
				
				// Insert sort the messages.
				for(int i = 0; i < messages.size(); i++){
					
					if(messageID < messages.get(i).getId()){
						messages.add(i, message);
						break;
					}
				}
				
				if(beforeSize == messages.size())
					messages.add(message);
				
			} catch (DOMException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return messages;
	}
}
