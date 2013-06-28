package com.loopj.android.http;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Message;

public class XmlHttpResponseHandler extends AsyncHttpResponseHandler {

    public void onSuccess(Document response) {}

    public void onSuccess(int statusCode, Header[] headers, Document response) {
        onSuccess(statusCode, response);
    }

    public void onSuccess(int statusCode, Document response) {
        onSuccess(response);
    }

    public void onFailure(Throwable e, Document errorResponse) {}

    //
    // Pre-processing on messages (executes in background threadpool thread)
    //

    @Override
    protected void sendSuccessMessage(int statusCode, Header[] headers, String responseBody) {
        Object xmlResponse;
        if (statusCode != HttpStatus.SC_NO_CONTENT) {
            try {
                xmlResponse = parseResponse(responseBody);
                sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[] {statusCode, headers, xmlResponse}));
            } catch (Exception e) {
                sendFailureMessage(e, responseBody);
            }
        } else {
            try {
                xmlResponse = parseResponse(null);
                sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[] {statusCode, xmlResponse}));
            } catch (Exception e) {
                sendFailureMessage(e, responseBody);
            }
        }
    }

    //
    // Pre-processing of messages (in original calling thread, typically the UI thread)
    //

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case SUCCESS_MESSAGE:
                Object[] response = (Object[]) msg.obj;
                handleSuccessXmlMessage(((Integer) response[0]).intValue(), (Header[]) response[1], response[2]);
                break;
            default:
                super.handleMessage(msg);
        }
    }

    private void handleSuccessXmlMessage(int statusCode, Header[] headers, Object xmlResponse) {
        if (xmlResponse instanceof Document) {
            onSuccess(statusCode, headers, (Document) xmlResponse);
        } else {
            onFailure(new XmlParserException("Unexpected type " + xmlResponse.getClass().getName()), (Document) null);
        }
    }

    protected Object parseResponse(String xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc;
        if (xml != null) {
            InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            doc = db.parse(is);
        } else {
            doc = db.newDocument();
        }
        return doc;
    }

    @Override
    protected void handleFailureMessage(Throwable e, String responseBody) {
        try {
            if (responseBody != null) {
                Object xmlResponse = parseResponse(responseBody);
                if (xmlResponse instanceof Document) {
                    onFailure(e, (Document) xmlResponse);
                } else {
                    onFailure(e, responseBody);
                }
            } else {
                onFailure(e, "");
            }
        } catch (Exception ex) {
            onFailure(ex, responseBody);
        }
    }
}
