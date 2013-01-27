package com.macinsiders.chat.processor;
import android.os.Bundle;

public interface ResourceProcessor {

        void getResource(ResourceProcessorCallback callback, Bundle params);
        void postResource(ResourceProcessorCallback callback, Bundle params);
        void putResource(ResourceProcessorCallback callback, Bundle params);
        void deleteResource(ResourceProcessorCallback callback, Bundle params);

}