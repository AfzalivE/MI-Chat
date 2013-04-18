package com.afzal.mi_chat;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MessagesActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);

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
                { "jon.john23", "10:16:01 pm", "damn" },
                { "jon.john23", "10:16:01 pm", "better than exams" },
                { "Entropy", "10:16:01 pm", "It's because I have an exam" },
                { "jon.john23", "10:16:01 pm", "oh :p" },
                { "jon.john23", "10:16:01 pm", "wht the hell" },
                { "jon.john23", "10:16:01 pm", "wht class are u taking?" },
                { "Entropy", "10:16:01 pm", "Else I'd be able to split the work up over tomorrow/Friday, but I gotta spend those days studying for a Saturday exam" } };

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
}
