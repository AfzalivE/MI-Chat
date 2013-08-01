package com.afzaln.mi_chat.utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.ConfigurationFactory;
import org.kefirsf.bb.TextProcessor;
import org.kefirsf.bb.conf.Code;
import org.kefirsf.bb.conf.Configuration;
import org.kefirsf.bb.conf.Constant;
import org.kefirsf.bb.conf.NamedValue;
import org.kefirsf.bb.conf.Pattern;
import org.kefirsf.bb.conf.Scope;
import org.kefirsf.bb.conf.Template;
import org.kefirsf.bb.conf.Text;

import java.util.Arrays;

/**
 * Created by afzaln on 2013-05-26.
 */
public class ParserUtils {

    private static TextProcessor mProcessor;

    private static Configuration getConfiguration() {
        Configuration cfg = ConfigurationFactory.getInstance().create();

        Code code = new Code("colr");
        code.setPattern(new Pattern(Arrays.asList(
                new Constant("[color="),
                new Text("color", new Scope("escapeXml"), false),
                new Constant("]"),
                new Text("text", new Scope("escapeXml"), false),
                new Constant("[/color]")
        )));

        code.setTemplate(new Template(Arrays.asList(
                new Constant("<font color=\""),
                new NamedValue("color"),
                new Constant("\">"),
                new NamedValue("text"),
                new Constant("</font>")
        )));

        cfg.getRootScope().addCode(code);

// This is basically how you find out how to setPatterns and setTemplates from
// existing patterns and templates

//        Iterator<Code> it = cfg.getRootScope().getCodes().iterator();
//        while (it.hasNext()) {
//            Code testCode = it.next();
//            if (testCode.getName().equals("color")) {
//                List patts = testCode.getPattern().getElements();
//                Log.d("CFG", ((Constant) patts.get(0)).getValue());
//                Log.d("CFG", ((Text) patts.get(1)).getName());
//                Iterator<Code> it2 = ((Scope) ((Text) patts.get(1)).getScope()).getCodes().iterator();
//                while (it2.hasNext()) {
//                    Code code2 = it2.next();
//                    List patts2 = code2.getPattern().getElements();
//                    Log.d("CFG", ((Constant) patts2.get(0)).getValue());
//                }
//                Log.d("CFG", ((Constant) patts.get(2)).getValue());
//
//                // This text node had no scope elements
//                Log.d("CFG", ((Text) patts.get(3)).getName());
//
//                Log.d("CFG", ((Constant) patts.get(4)).getValue());
//
//                List patts3 = testCode.getTemplate().getElements();
//                Log.d("CFG", ((Constant) patts3.get(0)).getValue());
//                Log.d("CFG", ((NamedValue) patts3.get(1)).getName());
//                Log.d("CFG", ((Constant) patts3.get(2)).getValue());
//                Log.d("CFG", ((NamedValue) patts3.get(3)).getName());
//                Log.d("CFG", ((Constant) patts3.get(4)).getValue());
//
//            }
//        }

        return cfg;
    }

    private static TextProcessor getProcessorInstance() {
        if (mProcessor == null) {
            mProcessor = BBProcessorFactory.getInstance().create(getConfiguration());
        }
        return mProcessor;
    }

    private static String processActions(String userName, String message) {
        if (message.startsWith("/login")) {
            return message.replace("/login", "").concat(" logged in");
        }

        if (message.startsWith("/logout")) {
            if (message.contains("Timeout")) {
                return message.replace("/logout", "").replace("Timeout", "logged out (timeout)");
            } else {
                return message.replace("/logout", "").concat(" logged out");
            }
        }

        if (message.startsWith("/roll")) {
            return message.replace("/roll", "Roll:");
        }

        if (message.startsWith("/me")) {
            return message.replace("/me", userName);
        }

        if (message.startsWith("/action")) {
            return message.replace("/action", userName);
        }

        if (message.startsWith("/privmsgto")) {
            return message.replace("/privmsgto", "Whisper to");
        }

        if (message.startsWith("/privmsg")) {
            return message.replace("/privmsg", "Whisper from " + userName);
        }

        if (message.startsWith("/privactionto")) {
            return message.replace("/privactionto", "Action to");
        }

        if (message.startsWith("/privaction")) {
            return message.replace("/privaction", "Action from " + userName);
        }

        if (message.startsWith("/queryOpen")) {
            return message.replace("/queryOpen", "Private channel opened to");
        }

        if (message.startsWith("/queryClose")) {
            return message.replace("/queryClose", "Private channel to ").concat(" closed");
        }

        if (message.startsWith("/channelLeave")) {
            return message.replace("/channelLeave", "").concat(" has left the channel");
        }

        if (message.startsWith("/channelEnter")) {
            return message.replace("/channelEnter", "").concat(" has entered the channel");
        }

        return message;
    }

    public static String process(String userName, String message) {
        return getProcessorInstance().process(StringEscapeUtils.unescapeXml(processActions(userName, message)));
    }
}
