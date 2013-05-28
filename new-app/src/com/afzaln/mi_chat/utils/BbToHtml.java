package com.afzaln.mi_chat.utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.ConfigurationFactory;
import org.kefirsf.bb.TextProcessor;
import org.kefirsf.bb.conf.*;

import java.util.Arrays;

/**
 * Created by afzaln on 2013-05-26.
 */
public class BbToHtml {

    private static TextProcessor mProcessor;

    private static Configuration getConfiguration() {
        Configuration cfg = ConfigurationFactory.getInstance().create();

// TODO fix link parsing
        Code code = new Code("colr");
        code.setPattern(new Pattern(Arrays.asList(
                new Constant("[color="),
                // not sure if "escapeXml" really works
                new Text("color", new Scope("escapeXml"), false),
                new Constant("]"),
                new Text("text", new Scope(), false),
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

    public static String process(String text) {
// TODO do something about ChatBot messages, make them nicer
//        if (messageText.contains("/login")) {
//            return messageText.substring(7) + " logs into the Chat.";
//        } else if (messageText.contains("/logout")) {
//            return messageText.substring(8) + " has been logged out.";
//        }
        return getProcessorInstance().process(StringEscapeUtils.unescapeXml(text));
    }
}
