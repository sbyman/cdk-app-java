package com.myorg.utils;

import java.util.Map;

public class CORSHeaderSetter {
    public static Map<String, String> setCORSHeader(Map<String, String> headers){
        headers.put("Access-Control-Allow-Origin","*");
        headers.put("Access-Control-Allow-Headers","Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token,X-Amz-User-Agent");
        headers.put("Access-Control-Allow-Methods","OPTIONS,GET,PUT,POST,DELETE");
        return headers;
    }
}
