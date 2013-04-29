package com.afzal.mi_chat.resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Page implements Resource {
    Info info;
    List<User> userList;
    List<Message> messageList;

    public Page() {

    }

    public List<User> getUserList() {
        return this.userList;
    }

    public List<Message> getMessageList() {
        return this.messageList;
    }

    public Page(Document document) {
        NodeList mainNodes = document.getElementsByTagName("root").item(0).getChildNodes();

        for (int i = 0; i < mainNodes.getLength(); i++) {
            Node node = mainNodes.item(i);

            String nodeName = node.getNodeName();
            if (nodeName.equals("users")) {
                this.userList = processUsers(node);
            } else if (nodeName.equals("messages")) {
                this.messageList = processMessages(node);
            } else if (nodeName.equals("infos")) {

            }
        }

    }

    private List<User> processUsers(Node node) {
        NodeList users = node.getChildNodes();

        List<User> userList = new ArrayList<User>();

        for (int i = 0; i < users.getLength(); i++) {
            Node userNode = users.item(i);

            NamedNodeMap userAttrs = userNode.getAttributes();

            long userId = Long.parseLong(userAttrs.getNamedItem("userID").getTextContent());
            int userRole = Integer.parseInt(userAttrs.getNamedItem("userRole").getTextContent());
            int channelId = Integer.parseInt(userAttrs.getNamedItem("channelID").getTextContent());
            String userName = userNode.getTextContent();

            User user = new User (userId, userRole, channelId, userName);

            userList.add(user);
        }

        return userList;
    }

    private List<Message> processMessages(Node node) {
        NodeList messages = node.getChildNodes();

        List<Message> messageList = new ArrayList<Message>();

        for (int i= 0; i < messages.getLength(); i++) {
            Node messageNode = messages.item(i);

            NamedNodeMap messageAttrs = messageNode.getAttributes();

            long messageId = Long.parseLong(messageAttrs.getNamedItem("id").getTextContent());
            long dateTime = parseDate(messageAttrs.getNamedItem("dateTime").getTextContent());
            long userId = Long.parseLong(messageAttrs.getNamedItem("userID").getTextContent());
            int userRole = Integer.parseInt(messageAttrs.getNamedItem("userRole").getTextContent());
            int channelId = Integer.parseInt(messageAttrs.getNamedItem("channelID").getTextContent());
            NodeList childNodes = messageNode.getChildNodes();
            String userName = new String();
            String messageText = new String();

            for (int j = 0; j < childNodes.getLength(); j++) {
                Node childNode = childNodes.item(j);
                if (childNode.getNodeName().equals("username")) {
                    userName = childNode.getTextContent();
                }
                if (childNode.getNodeName().equals("text")) {
                    messageText = childNode.getTextContent();
                }
            }

            Message message = new Message(messageId, dateTime, userId, userRole, channelId, userName, messageText);

            messageList.add(message);

        }
        return messageList;
    }

    private long parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
        try {
            Date date = dateFormat.parse(dateString);
            long dateMillis = date.getTime();
            return dateMillis;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
