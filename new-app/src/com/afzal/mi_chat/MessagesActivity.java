package com.afzal.mi_chat;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.afzal.mi_chat.LoginActivity.LoginTask;
import com.afzal.mi_chat.Utils.NetUtils;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.common.io.CharStreams;

public class MessagesActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);
        getWindow().setBackgroundDrawable(null);

        setSlidingActionBarEnabled(true);

        ListView list = (ListView) findViewById(R.id.list);

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
                // Doesn't maintain session
                new GetMessagesTask().execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class GetMessagesTask extends AsyncTask<Void, Void, String> {

        private final String TAG = LoginTask.class.getSimpleName();

        @Override
        protected String doInBackground(Void... params) {

            HttpTransport transport = NetUtils.getTransport();
            HttpRequest request;
            HttpResponse response;
            String result = new String();

            try {

                request = transport.createRequestFactory().buildGetRequest(new GenericUrl("http://www.macinsiders.com/chat/?ajax=true"));
                response = request.execute();

                result = CharStreams.toString(new InputStreamReader(response.getContent()));

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, result);
        }
    }

}
