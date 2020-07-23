package com.myorg.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class ErrorMessageGenerator {

    public static String generateErrorMessage(String errorType, int httpStatusCode, String requestId, String message){
        Map<String, Object> errorPayload = new HashMap<>();
        errorPayload.put("errorType", errorType);
        errorPayload.put("httpStatus", httpStatusCode);
        errorPayload.put("requestId", requestId);
        errorPayload.put("message", message);

        try {
            return new ObjectMapper().writeValueAsString(errorPayload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(generateErrorMessage("InternalServerError", 500, requestId, "Error in parsing error message"));
        }
    }
}
