package com.macinsiders.chattools;

public class TestClass {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		String username = "username";
		String password = "password";
		
		MacinsidersChatInterface ci = new MacinsidersChatInterface();
		
		if(!ci.login(username, password)){
			
			System.out.println("Aww... logging in failed :(");
			return;
		}
		
		//ci.sendMessage("This message is coming directly from my computer and not me.");
		
		while(true){
			
			for(Message m : ci.getIncomingMessages())
				System.out.println(m.getUserName() + " :\t" + m.getMessageContent());
			
			Thread.sleep(1000);
			
			ci.updateStatus();
		}
	}
}
