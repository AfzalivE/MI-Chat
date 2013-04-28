package com.afzal.mi_chat.resource;

import java.util.ArrayList;
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

    public Page(Document document) {
        NodeList mainNodes = document.getElementsByTagName("root").item(0).getChildNodes();

        for (int i = 0; i < mainNodes.getLength(); i++) {
            Node node = mainNodes.item(i);

            if (node.getNodeName().equals("users")) {
                this.userList = processUsers(node);
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

}
