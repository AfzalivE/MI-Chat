package com.macinsiders.chat.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class XMLParser {
    private static final String TAG = XMLParser.class.getSimpleName();

    private static NodeList mInfoNodes;
    private static NodeList mUsersNodes;
    private static NodeList mMessagesNodes;

    public XMLParser(String xml) {
        NodeList nodes = getChildNodes(xml, "root", 0);
        Log.d(TAG, "populating nodes");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeName().equals("infos")) {
                Log.d(TAG, "Populating info nodes");
                mInfoNodes = node.getChildNodes();
            }
            if (node.getNodeName().equals("users")) {
                Log.d(TAG, "Populating users nodes");
                mUsersNodes = node.getChildNodes();
            }
            if (node.getNodeName().equals("messages")) {
                Log.d(TAG, "Populating message nodes");
                mMessagesNodes = node.getChildNodes();
            }
        }
    }

    public NodeList getMessagesNode() {
        return mMessagesNodes;
    }

    public NodeList getUserNode() {
        return mUsersNodes;
    }

    public NodeList getInfoNode() {
        return mInfoNodes;
    }

    private static NodeList getChildNodes(String xml, String tagName, int item) {
        return getDocument(xml).getElementsByTagName(tagName).item(item).getChildNodes();
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

//    public static List<User> getUsers(Node node) {
//
//        NodeList activeUsers = node.getChildNodes();
//
//        List<User> users = new LinkedList<User>();
//
//        for (int j = 0; j < activeUsers.getLength(); j++) {
//
//            node = activeUsers.item(j);
//
//            NamedNodeMap userAttributes = node.getAttributes();
//
//            long userID = Long.parseLong(userAttributes.getNamedItem("userID").getTextContent());
//            int userRole = Integer.parseInt(userAttributes.getNamedItem("userRole").getTextContent());
//            int channelID = Integer.parseInt(userAttributes.getNamedItem("channelID").getTextContent());
//            String userName = node.getTextContent();
//
//            User user = new User(userID, userRole, channelID, userName);
//
//            users.add(user);
//        }
//
//        return users;
//
//    }
//
//    public static Info getInfo(Node node) {
//        NodeList infoNodes = node.getChildNodes();
//
//        long userID = 0;
//        String userName = null;
//        int userRole = 0;
//        int channelID = 0;
//        String channelName = null;
//
//        for (int i = 0; i < infoNodes.getLength(); i++) {
//
//            Node infoNode = infoNodes.item(i);
//            String type = infoNode.getAttributes().getNamedItem("type").getNodeValue();
//
//            String textContent = infoNode.getTextContent();
//
//            userID = (!type.equals("userID")) ? userID : Long.parseLong(textContent);
//            userName = (!type.equals("userName")) ? userName : infoNode.getTextContent();
//            userRole = (!type.equals("userRole")) ? userRole : Integer.parseInt(textContent);
//            channelID = (!type.equals("channelID")) ? userRole : Integer.parseInt(textContent);
//            channelName = (!type.equals("channelName")) ? userName : infoNode.getTextContent();
//        }
//
//        return new Info(userID, userName, userRole, channelID, channelName);
//
//    }

}
