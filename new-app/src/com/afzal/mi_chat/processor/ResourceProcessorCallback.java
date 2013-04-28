package com.afzal.mi_chat.processor;

public interface ResourceProcessorCallback {
    
    /**
     * Returns the result of the resource request and the resource id if applicable, 
     * eg the id created after a successful post.
     * @param resultCode
     * @param resourceId
     */
    void send(int resultCode, String resourceId);

}
