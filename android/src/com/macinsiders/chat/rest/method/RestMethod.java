package com.macinsiders.chat.rest.method;

import com.macinsiders.chat.resource.Resource;

public interface RestMethod<T extends Resource>{

    public RestMethodResult<T> execute(); // might throw UnknownHostException, something for later
}
