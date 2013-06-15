package com.afzaln.mi_chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class ServiceHelper {

    private static final String TAG = ServiceHelper.class.getSimpleName();

    public static String ACTION_REQUEST_RESULT = "REQUEST_RESULT";
    public static String EXTRA_REQUEST_ID = "EXTRA_REQUEST_ID";
    public static String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";

    private List<Long> pendingRequests = new ArrayList<Long>();
    private Object pendingRequestsLock = new Object();
    private Context mContext;
    private ServiceResultReceiver serviceCallback;

    private Class<? extends ServiceContract> mServiceClass;

    public ServiceHelper(Context context) {
        this.mContext = context.getApplicationContext();
        this.serviceCallback = new ServiceResultReceiver();
        this.mServiceClass = Service.class;
    }

    class ServiceResultReceiver extends ResultReceiver {
        public ServiceResultReceiver() {
            super(null);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Intent origIntent = resultData.getParcelable(ServiceContract.ORIGINAL_INTENT_EXTRA);

            if (origIntent != null) {
                long requestId = origIntent.getLongExtra(EXTRA_REQUEST_ID, 0);
                synchronized (pendingRequestsLock) {
                    pendingRequests.remove(requestId);
                }

                Intent resultBroadcast = new Intent(ACTION_REQUEST_RESULT);
                resultBroadcast.putExtra(EXTRA_REQUEST_ID, requestId);
                resultBroadcast.putExtra(EXTRA_RESULT_CODE, resultCode);

                mContext.sendBroadcast(resultBroadcast);
            }
        }
    }

    public long getPage() {
        long requestId = generateRequestId();
        synchronized (pendingRequestsLock) {
            pendingRequests.add(requestId);
        }

        Intent intent = new Intent(this.mContext, mServiceClass);
        intent.putExtra(ServiceContract.METHOD_EXTRA, ServiceContract.METHOD_GET);
        intent.putExtra(ServiceContract.RESOURCE_TYPE_EXTRA, ServiceContract.RESOURCE_TYPE_PAGE);
        intent.putExtra(ServiceContract.SERVICE_CALLBACK_EXTRA, serviceCallback);
        intent.putExtra(EXTRA_REQUEST_ID, requestId);

        Log.d(TAG, "Starting Service");
        this.mContext.startService(intent);

        return requestId;
    }

    public void setServiceClass(Class<? extends ServiceContract> service) {
        mServiceClass = service;
    }

    private long generateRequestId() {
        long requestId = UUID.randomUUID().getLeastSignificantBits();
        return requestId;
    }

    public boolean isRequestPending(long requestId) {
        synchronized (pendingRequestsLock) {
            return this.pendingRequests.contains(requestId);
        }
    }

}
