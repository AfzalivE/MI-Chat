package com.macinsiders.chat.rest.method;

import java.net.UnknownHostException;

import com.macinsiders.chat.resource.Resource;

public interface RestMethod<T extends Resource>{

    public RestMethodResult<T> execute() throws UnknownHostException;
}
