package com.afzal.mi_chat.processor;

import com.afzal.mi_chat.service.ServiceContract;

import android.content.Context;

public class ProcessorFactory {
    private static ProcessorFactory mSingleton;
    private Context mContext;
    private ResourceProcessor mDefaultProcessor;

    public void setDefaultProcessor (ResourceProcessor processor) {
        mDefaultProcessor = processor;
    }

    public ResourceProcessor getProcessor(int resourceType) {
        switch (resourceType) {
            case ServiceContract.RESOURCE_TYPE_PAGE:
                return new PageProcessor(mContext);
            case ServiceContract.RESOURCE_TYPE_MESSAGE:
                return new MessageProcessor(mContext);
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
        mDefaultProcessor = new PageProcessor(mContext);
    }
}
