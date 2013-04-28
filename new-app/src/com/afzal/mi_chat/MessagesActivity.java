package com.afzal.mi_chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ServiceConfigurationError;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.afzal.mi_chat.Utils.NetUtils;
import com.afzal.mi_chat.processor.ProcessorFactory;
import com.afzal.mi_chat.processor.ResourceProcessor;
import com.afzal.mi_chat.service.ServiceContract;
import com.afzal.mi_chat.service.ServiceHelper;

public class MessagesActivity extends BaseActivity {

    private static final String TAG = MessagesActivity.class.getSimpleName();

    private Long requestId;
    private ServiceHelper mServiceHelper;

    private BroadcastReceiver requestReceiver;
    private IntentFilter filter = new IntentFilter(ServiceHelper.ACTION_REQUEST_RESULT);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);
        getWindow().setBackgroundDrawable(null);

        //        this.requestReceiver = new Receiver();

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

    @Override
    public void onResume() {
        super.onResume();

        this.registerReceiver(this.requestReceiver, this.filter);

        ResourceProcessor processor = ProcessorFactory.getInstance(this).getProcessor(ServiceContract.RESOURCE_TYPE_PAGE);

        processor.getResource(null);

        //        mServiceHelper = new ServiceHelper(this);
        //
        //        if (requestId == null) {
        //            requestId = mServiceHelper.getPage();
        //            setProgressBarIndeterminateVisibility(true);
        //        } else if (mServiceHelper.isRequestPending(requestId)) {
        //            Log.d(TAG, "request is pending");
        //            setProgressBarIndeterminateVisibility(true);
        //        } else {
        //            Log.d(TAG, "requestId is not null and not in progress");
        //            setProgressBarIndeterminateVisibility(false);
        //        }
    }

    //    @Override
    //    public void onPause() {
    //        super.onPause();
    //
    //        try {
    //            this.unregisterReceiver(requestReceiver);
    //        } catch (IllegalArgumentException e) {
    //            Log.w(TAG, "Likely the receiver wasn't registered, ok to ignore");
    //        }
    //    }

    //    class Receiver extends BroadcastReceiver {
    //
    //        @Override
    //        public void onReceive(Context context, Intent intent) {
    //            long resultRequestId = intent.getLongExtra(ServiceHelper.EXTRA_REQUEST_ID, 0);
    //
    //            Log.d(TAG, "Received intent " + intent.getAction() + ", request ID: " + resultRequestId);
    //
    //            if (resultRequestId == requestId) {
    //                MessagesActivity.this.setProgressBarIndeterminateVisibility(false);
    //                Log.d(TAG, "Result is for our request ID");
    //
    //                int resultCode = intent.getIntExtra(ServiceHelper.EXTRA_RESULT_CODE, 0);
    //
    //                Log.d(TAG, "Result code = " + resultCode);
    //
    //                if (resultCode == 200) {
    //                    Log.d(TAG, "Request successful");
    //                } else {
    //                    Log.w(TAG, "Request failed" + resultCode);
    //                }
    //            } else {
    //                // not the request we need, but the request we deserved. :(
    //                Log.d(TAG, "Result is NOT for our request ID");
    //
    //            }
    //
    //        }
    //    }

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
                ResourceProcessor processor = ProcessorFactory.getInstance(this).getProcessor(ServiceContract.RESOURCE_TYPE_PAGE);

                processor.getResource(null);
                //                requestId = mServiceHelper.getPage();
                setProgressBarIndeterminateVisibility(true);
                return true;
            case R.id.action_clearprefs:
                NetUtils.getCookieStoreInstance(MessagesActivity.this).clear();
        }
        return super.onOptionsItemSelected(item);
    }
}
