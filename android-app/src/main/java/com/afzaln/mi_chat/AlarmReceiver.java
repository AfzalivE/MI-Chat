package com.afzaln.mi_chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.afzaln.mi_chat.processor.ProcessorFactory;
import com.afzaln.mi_chat.processor.ResourceProcessor;
import com.afzaln.mi_chat.utils.ServiceContract;
import com.afzaln.mi_chat.utils.NetUtils;

/**
 * Created by afzal on 2013-06-27.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetUtils.isConnected(context)) {
            ResourceProcessor processor = ProcessorFactory.getInstance(context).getProcessor(ServiceContract.RESOURCE_TYPE_PAGE);
            processor.getResource();
        }
    }
}
