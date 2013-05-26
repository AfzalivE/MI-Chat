package com.afzaln.mi_chat.processor;
import android.os.Bundle;

public interface ResourceProcessor {

        void getResource();
        void postResource(Bundle params);
        void putResource(ResourceProcessorCallback callback, Bundle params);
        void deleteResource();

}