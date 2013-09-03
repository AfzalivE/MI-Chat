package com.afzaln.mi_chat.utils;


public interface ServiceContract {

    public static final String METHOD_EXTRA = "service.METHOD_EXTRA";
    public static final String SERVICE_CALLBACK_EXTRA = "service.SERVICE_CALLBACK";
    public static final String ORIGINAL_INTENT_EXTRA = "service.ORIGINAL_INTENT_EXTRA";
    public static final String RESOURCE_TYPE_EXTRA = "service.RESOURCE_TYPE_EXTRA";
    public static final String EXTRA_REQUEST_PARAMS = "service.EXTRA_REQUEST_PARAMS";

    public static final int RESOURCE_TYPE_PAGE = 1;
    public static final int RESOURCE_TYPE_MESSAGE = 2;

    public static final String METHOD_GET = Method.GET.toString();
    public static final String METHOD_POST = Method.POST.toString();
    public static final String METHOD_DELETE = Method.DELETE.toString();

    public static enum Method {
        GET, POST, PUT, DELETE
    }
}