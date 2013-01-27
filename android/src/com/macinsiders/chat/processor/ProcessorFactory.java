package com.macinsiders.chat.processor;

import android.content.Context;

import com.macinsiders.chat.service.ServiceContract;

public class ProcessorFactory {
    private static ProcessorFactory mSingleton;
    private Context mContext;
    private ResourceProcessor mDefaultProcessor;
    
    public void setDefaultProcessor (ResourceProcessor processor) {
        mDefaultProcessor = processor;
    }
    
    public ResourceProcessor getProcessor(int resourceType) {
        switch (resourceType) {
            case ServiceContract.RESOURCE_TYPE_MESSAGES:
                return new MessagesProcessor(mContext);
             // case ServiceContract.RESOURCE_TYPE_USERS:
            default:
                return null;
        }
    }
    
    public static ProcessorFactory getInstance(Context context) {
        if (mSingleton == null) {
            mSingleton = new ProcessorFactory(context.getApplicationContext());
        }
        
        return mSingleton;
    }
    
    private ProcessorFactory(Context context) {
        mContext = context;
        mDefaultProcessor = new MessagesProcessor(mContext);
    }
}
