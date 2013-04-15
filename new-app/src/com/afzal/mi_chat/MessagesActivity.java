package com.afzal.mi_chat;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MessagesActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);

        setSlidingActionBarEnabled(true);

        ListView list = (ListView) findViewById(R.id.list);

        String[] sports_array = getResources().getStringArray(R.array.sports_array);
//        list.setAdapter(new ArrayAdapter<String>(this, R.layout.message_list_item, sports_array));
                list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sports_array));
    }
}
