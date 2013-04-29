package com.afzal.mi_chat.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.afzal.mi_chat.processor.ProcessorFactory;
import com.afzal.mi_chat.processor.ResourceProcessor;
import com.afzal.mi_chat.processor.ResourceProcessorCallback;

public class Service extends IntentService implements ServiceContract {

    private static final String TAG = Service.class.getSimpleName();

    private static final int REQUEST_INVALID = -1;

    public Service() {
        super("DefaultService");
    }

    @Override
    protected void onHandleIntent(Intent requestIntent) {
        Log.d(TAG, "Starting to handle intent");
        int resourceType = requestIntent.getIntExtra(ServiceContract.RESOURCE_TYPE_EXTRA, -1);
        String method = requestIntent.getStringExtra(ServiceContract.METHOD_EXTRA);
        Bundle params = requestIntent.getBundleExtra(ServiceContract.EXTRA_REQUEST_PARAMS);
        ResultReceiver serviceHelperCallback = requestIntent.getParcelableExtra(ServiceContract.SERVICE_CALLBACK_EXTRA);
        ResourceProcessor processor = ProcessorFactory.getInstance(this).getProcessor(resourceType);

        if (processor == null) {
            if (serviceHelperCallback != null) {
                serviceHelperCallback.send(REQUEST_INVALID, bundleOriginalIntent(requestIntent));
            }
        }
        Log.d(TAG, "After serviceHelperCallback.send()");

        ResourceProcessorCallback processorCallback = makeProcessorCallback(requestIntent, serviceHelperCallback);

        if (method.equalsIgnoreCase(METHOD_GET)) {
            try {
                Log.d(TAG, "Handling intent for GET method");
//                processor.getResource(processorCallback, params);
            } catch (NullPointerException e) {
                Log.d(TAG, "Couldn't get resource from processor: ");
                e.printStackTrace();
            }
        } else if (method.equalsIgnoreCase(METHOD_POST)) {
            processor.postResource(processorCallback, params);
        } else if (method.equals(METHOD_DELETE)) {
            processor.deleteResource();
        } else if (serviceHelperCallback != null) {
            serviceHelperCallback.send(REQUEST_INVALID, bundleOriginalIntent(requestIntent));
        }
    }

    private ResourceProcessorCallback makeProcessorCallback(final Intent originalIntent, final ResultReceiver serviceReceiver) {

        ResourceProcessorCallback callback = new ResourceProcessorCallback() {

            @Override
            public void send(int resultCode, String resourceId) {
                if (serviceReceiver != null) {
                    serviceReceiver.send(resultCode, bundleOriginalIntent(originalIntent));
                }
            }
        };
        return callback;
    }

    private Bundle bundleOriginalIntent(Intent originalIntent) {

        Bundle originalRequest = new Bundle();
        originalRequest.putParcelable(ORIGINAL_INTENT_EXTRA, originalIntent);
        return originalRequest;
    }
}
