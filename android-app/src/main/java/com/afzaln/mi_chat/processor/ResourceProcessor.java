package com.afzaln.mi_chat.processor;

import android.os.Bundle;

import org.w3c.dom.Document;

public interface ResourceProcessor {

    void getResource();

    void postResource(Bundle params);

    void updateContentProvider(Document result);

    void putResource(ResourceProcessorCallback callback, Bundle params);

    void deleteResource();

}