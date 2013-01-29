package com.macinsiders.chat.rest.method;

import com.macinsiders.chat.resource.Resource;

public class RestMethodResult<T extends Resource> {

    private int statusCode = 0;
    private String statusMsg;
    private T cookies;
    private T resource;

    public RestMethodResult(int statusCode, String statusMsg, T cookies, T resource) {
        super();
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
        this.cookies = cookies;
        this.resource = resource;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public T getCookies() {
        return cookies;
    }

    public T getResource() {
        return resource;
    }

}
