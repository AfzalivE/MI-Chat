package com.afzal.mi_chat;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.afzal.mi_chat.Utils.NetUtils;
import com.loopj.android.http.XmlHttpResponseHandler;

public class MessagesActivity extends BaseActivity {

    private static final String TAG = MessagesActivity.class.getSimpleName();
    private XmlHttpResponseHandler myResponseHandler = new XmlHttpResponseHandler() {
        @Override
        public void onStart() {
            Log.d(TAG, "onStart");
        }

        @Override
        public void onSuccess(Document response) {
            Log.d(TAG, "onSuccess");
        }

        @Override
        public void onFailure(Throwable e, Document response) {
            Log.d(TAG, "onFailure");
            e.printStackTrace();
            // Response failed :(
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "onFinish");
            // Completed the request (either success or failure)
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);
        getWindow().setBackgroundDrawable(null);

        setSlidingActionBarEnabled(true);

        ListView list = (ListView) findViewById(R.id.messagelist);

        ArrayList<HashMap<String, String>> arrayList = populateArray();

        // To be switched to MessagesAdapter
        SimpleAdapter adapter = new SimpleAdapter(this, arrayList, R.layout.message_list_item, new String[] { "username", "timestamp", "message" }, new int[] {
                R.id.username,
                R.id.timestamp,
                R.id.message });

        list.setAdapter(adapter);
    }

    /*
     * To be removed later
     */
    private ArrayList<HashMap<String, String>> populateArray() {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
        String[][] array = {
                { "Defroster", "8:30:40 pm", "ponyo text me" },
                { "Jester", "8:30:42 pm", "plsbehonest" },
                { "Entropy", "8:30:44 pm", "Yeah" },
                { "Entropy", "8:30:48 pm", "I like you so much, you asshole" },
                { "Defroster", "8:30:49 pm", "why names not afzal bro" },
                { "Afzal", "8:30:51 pm", "ah, explains" },
                { "Jester", "8:30:53 pm", "lol" },
                { "Jimbojones", "8:31:02 pm", "lol" },
                { "Jimbojones", "8:31:15 pm", "her bad grammer is because she's from the middle east?" },
                { "Ponyo", "8:31:24 pm", "besides this is chat i really dont feel the need to write here like im writing an essay :/" },
                { "Afzal", "8:31:28 pm", "i didn't say what it explains" },
                { "Defroster", "8:31:32 pm", "i agree with ponyo" },
                { "Defroster", "8:31:35 pm", "talk as you please" },
                { "Afzal", "8:31:38 pm", "you might not even know what it explains" },
                { "Entropy", "8:31:39 pm", "You don't have to" },
                { "Entropy", "8:31:44 pm", "I don't type perfectly either" },
                { "Afzal", "8:31:53 pm", "jadoahfoajlkajsldhaofhaah" },
                { "Jimbojones", "8:31:53 pm", "ok" },
                { "Afzal", "8:31:54 pm", "see" },
                { "Jester", "8:32:02 pm", "her grammar isnt as bad as it was when she first got here" },
                { "romita_sur", "8:32:05 pm", "Defroster :D" },
                { "Afzal", "8:32:16 pm", "when did she get hurr?" }, };

        HashMap<String, String> item;
        for (int i = 0; i < array.length; i++) {
            item = new HashMap<String, String>();
            item.put("username", array[i][0]);
            item.put("timestamp", array[i][1]);
            item.put("message", array[i][2]);
            arrayList.add(item);
        }
        return arrayList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                NetUtils.client.get("http://www.macinsiders.com/chat/?ajax=true", myResponseHandler);
                return true;
            case R.id.action_clearprefs:
                NetUtils.getCookieStoreInstance(MessagesActivity.this).clear();
        }
        return super.onOptionsItemSelected(item);
    }
}
