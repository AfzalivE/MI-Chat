package com.afzaln.mi_chat.resource;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Page implements Resource {
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
    Info info;
    List<User> userList;
    List<Message> messageList;

    public Page(Document document) {
        // possible NPE
        try {
            NodeList mainNodes = document.getElementsByTagName("root").item(0).getChildNodes();

            for (int i = 0; i < mainNodes.getLength(); i++) {
                Node node = mainNodes.item(i);

                String nodeName = node.getNodeName();
                if (nodeName.equals("users")) {
                    this.userList = processUsers(node);
                } else if (nodeName.equals("messages")) {
                    this.messageList = processMessages(node);
                } else if (nodeName.equals("infos")) {
                    this.info = processInfo(node);
                }
            }
        } catch (NullPointerException e) {
            // Do nothing
        }

    }

    public List<User> getUserList() {
        return this.userList;
    }

    public List<Message> getMessageList() {
        return this.messageList;
    }

    public Info getInfo() {
        return this.info;
    }

    private Info processInfo(Node node) {
        NodeList infos = node.getChildNodes();

        long userId = 0;
        String userName = null;
        int userRole = 0;
        int channelId = 0;
        String channelName = null;
        boolean loggedIn = true;

        if (infos.getLength() < 1) return null;

        for (int i = 0; i < infos.getLength(); i++) {
            Node infoNode = infos.item(i);
            String typeAttrs = infoNode.getAttributes().getNamedItem("type").getNodeValue();

            if (typeAttrs.equals("logout")) {
                loggedIn = false;
                continue;
            }

            String textContent = infoNode.getTextContent();

            if (typeAttrs.equals("userID")) {
                userId = Long.parseLong(textContent);
                Log.d("TEST", textContent);
                continue;
            }

            if (typeAttrs.equals("userRole")) {
                userRole = Integer.parseInt(textContent);
                Log.d("TEST", textContent);
                continue;
            }

            if (typeAttrs.equals("channelID")) {
                channelId = Integer.parseInt(textContent);
                Log.d("TEST", textContent);
                continue;
            }

            if (typeAttrs.equals("userName")) {
                userName = textContent;
                Log.d("TEST", textContent);
                continue;
            }

            if (typeAttrs.equals("channelName")) {
                channelName = textContent;
                Log.d("TEST", textContent);
                continue;
            }
        }

        return new Info(userId, userRole, channelId, userName, channelName, loggedIn);
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

            User user = new User(userId, userRole, channelId, userName);

            userList.add(user);
        }

        return userList;
    }

    private List<Message> processMessages(Node node) {
        NodeList messages = node.getChildNodes();

        List<Message> messageList = new ArrayList<Message>();

        for (int i = 0; i < messages.getLength(); i++) {
            Node messageNode = messages.item(i);

            NamedNodeMap messageAttrs = messageNode.getAttributes();

            long messageId = Long.parseLong(messageAttrs.getNamedItem("id").getTextContent());
            int type = Message.NORMAL_TYPE;
            long dateTime = parseDate(messageAttrs.getNamedItem("dateTime").getTextContent());
            long userId = Long.parseLong(messageAttrs.getNamedItem("userID").getTextContent());
            int userRole = Integer.parseInt(messageAttrs.getNamedItem("userRole").getTextContent());
            int channelId = Integer.parseInt(messageAttrs.getNamedItem("channelID").getTextContent());
            NodeList childNodes = messageNode.getChildNodes();
            String userName = "";
            String messageText = "";

            for (int j = 0; j < childNodes.getLength(); j++) {
                Node childNode = childNodes.item(j);
                if (childNode.getNodeName().equals("username")) {
                    userName = childNode.getTextContent();
                }
                if (childNode.getNodeName().equals("text")) {
                    messageText = childNode.getTextContent();
                }
            }

            String[] imgLinkArr = StringUtils.substringsBetween(messageText, "[img]", "[/img]");
            String imgLinks = StringUtils.join(imgLinkArr, "|");

            // TODO optimize this mess
            if (imgLinkArr != null) {
                messageText = messageText.replace("[img]", "").replace("[/img]", "");
                for (int j = 0; j < imgLinkArr.length; j++) {
                    messageText = messageText.replace(imgLinkArr[j], "");
                }
                messageText = messageText.trim();
            }

            if (messageText.startsWith("/error")) {
                type = Message.ERROR_TYPE;
            } else if (messageText.startsWith("/")) {
                type = Message.ACTION_TYPE;
            }

            Message message = new Message(messageId, type, dateTime, userId, userRole, channelId, userName, messageText, imgLinks);

            messageList.add(message);

        }
        return messageList;
    }

    private long parseDate(String dateString) {
        try {
            Date date = mDateFormat.parse(dateString);
            long dateMillis = date.getTime();
            return dateMillis;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
