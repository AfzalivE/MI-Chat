package com.afzal.mi_chat.processor;
import android.os.Bundle;

public interface ResourceProcessor {

        void getResource();
        void postResource(ResourceProcessorCallback callback, Bundle params);
        void putResource(ResourceProcessorCallback callback, Bundle params);
        void deleteResource(ResourceProcessorCallback callback, Bundle params);

}