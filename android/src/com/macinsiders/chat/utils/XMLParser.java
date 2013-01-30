package com.macinsiders.chat.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLParser {

    private static void getPage(String xml) {
        NodeList nodes = getChildNodes(xml, "root", 0);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeName().equals("users")) {
                getUsers(node);
            }
            if (node.getNodeName().equals("infos")) {
                getInfo(node);
            }
            if (node.getNodeName().equals("messages")) {
                getMessages(node);
            }

        }
    }

    private static Document getDocument(String xml) {
        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder;

            docBuilder = docBuilderFactory.newDocumentBuilder();

            InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));

            Document doc = docBuilder.parse(is);

            return doc;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static NodeList getChildNodes(String xml, String tagName, int item) {
        return getDocument(xml).getElementsByTagName(tagName).item(item).getChildNodes();
    }

    public static void getUsers(Node node) {

        NodeList activeUsers = node.getChildNodes();

        List<User> users = new LinkedList<User>();

        for (int j = 0; j < activeUsers.getLength(); j++) {

            node = activeUsers.item(j);

            NamedNodeMap userAttributes = node.getAttributes();

            long userID = Long.parseLong(userAttributes.getNamedItem("userID").getTextContent());
            int userRole = Integer.parseInt(userAttributes.getNamedItem("userRole").getTextContent());
            int channelID = Integer.parseInt(userAttributes.getNamedItem("channelID").getTextContent());
            String userName = node.getTextContent();

            User user = new User(userID, userRole, channelID, userName);

            users.add(user);
        }

        return users;

    }

    public static void getInfo(Node node) {
        NodeList infoNodes = node.getChildNodes();

        long userID = 0;
        String userName = null;
        int userRole = 0;
        int channelID = 0;
        String channelName = null;

        for (int i = 0; i < infoNodes.getLength(); i++) {

            Node infoNode = infoNodes.item(i);
            String type = infoNode.getAttributes().getNamedItem("type").getNodeValue();

            String textContent = infoNode.getTextContent();

            userID = (!type.equals("userID")) ? userID : Long.parseLong(textContent);
            userName = (!type.equals("userName")) ? userName : infoNode.getTextContent();
            userRole = (!type.equals("userRole")) ? userRole : Integer.parseInt(textContent);
            channelID = (!type.equals("channelID")) ? userRole : Integer.parseInt(textContent);
            channelName = (!type.equals("channelName")) ? userName : infoNode.getTextContent();
        }

        return new Info(userID, userName, userRole, channelID, channelName);

    }

    public static void getMessages(Node node) {
        NodeList incomingMessages = node.getChildNodes();

        List<Message> messages = new LinkedList<Message>();

        for (int j = 0; j < incomingMessages.getLength(); j++) {

            node = incomingMessages.item(j);

            NamedNodeMap userAttributes = node.getAttributes();

            long messageID = Long.parseLong(userAttributes.getNamedItem("id").getTextContent());
            long userID = Integer.parseInt(userAttributes.getNamedItem("userID").getTextContent());
            int userRole = Integer.parseInt(userAttributes.getNamedItem("userRole").getTextContent());
            int channelID = Integer.parseInt(userAttributes.getNamedItem("channelID").getTextContent());

            Date datetime = null;

            try {

                datetime = dateParser.parse(userAttributes.getNamedItem("dateTime").getTextContent());

                String messageContent = null;
                String userName = null;

                if (node.getFirstChild().getNodeName().equals("username")) {

                    messageContent = StringEscapeUtils.unescapeHtml4((node.getLastChild().getTextContent()));
                    userName = node.getFirstChild().getTextContent();
                } else {

                    messageContent = node.getFirstChild().getTextContent();
                    userName = node.getLastChild().getTextContent();
                }

                Message message = new Message(messageID, datetime, userID, userRole, channelID, messageContent, userName);

                int beforeSize = messages.size();

                if (messageID > lastMessageID)
                    lastMessageID = messageID;

                // Insert sort the messages.
                for (int i = 0; i < messages.size(); i++) {

                    if (messageID < messages.get(i).getId()) {
                        messages.add(i, message);
                        break;
                    }
                }

                if (beforeSize == messages.size())
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
