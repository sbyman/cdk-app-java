package com.myorg.lambda.ads;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myorg.dto.AdDto;
import com.myorg.dto.GatewayResponse;
import com.myorg.dto.TotemDto;
import com.myorg.mapper.AdMapper;
import com.myorg.mapper.TotemMapper;
import com.myorg.utils.CORSHeaderSetter;
import com.myorg.utils.ErrorMessageGenerator;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

public class UpdateAdHandler implements RequestHandler<Map<String,Object>, GatewayResponse>{

    @Override
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Inside com.myorg.lambda.ad: UpdateAdHandler " + input.getClass() + " data:" + input);
        Map<String, Object> pathParameters = (Map<String, Object>)input.get("pathParameters");
        String id = (String)pathParameters.get("id");

        String body = null;
        if(input.get("body") != null){
            body = (String) input.get("body");
            logger.log("Body is:" + body);
        } else {
            throw new RuntimeException(ErrorMessageGenerator.generateErrorMessage("BadRequest", 400, context.getAwsRequestId(), "Input body is empty"));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        AdDto adDto = null;
        try {
            adDto = objectMapper.readValue((String) input.get("body"), AdDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(ErrorMessageGenerator.generateErrorMessage("BadRequest", 400, context.getAwsRequestId(), "Input body is not as expected"));
        }

        logger.log("updating data for input parameter:"+id);
        AdDto output = updateData(id, adDto);

        String response = null;
        try {
            response = objectMapper.writeValueAsString(output);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(ErrorMessageGenerator.generateErrorMessage("InternalServerError", 500, context.getAwsRequestId(), "Insert executed successfully but something in the response parsing went wrong"));
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        //headers = CORSHeaderSetter.setCORSHeader(headers);
        return new GatewayResponse(response, headers, 200);
    }

    private AdDto updateData(String id, AdDto adDto) {
        DynamoDbClient ddb = DynamoDbClient.create();
        String tableName= System.getenv("AD_TABLE_NAME");
        String primaryKey = System.getenv("AD_PRIMARY_KEY");
        Map<String, AttributeValue> tableKey = new HashMap<>();

        tableKey.put(primaryKey, AttributeValue.builder().s(id).build());
        adDto.setId(id);

        AdMapper adMapper = new AdMapper();

        Map<String, AttributeValue> item = adMapper.fromAdDtoToDynamoEntry(adDto);

        Map<String, AttributeValueUpdate> itemUpdate = new HashMap<>();

        for (String key : item.keySet()) {
            itemUpdate.put(key, AttributeValueUpdate.builder()
                    .value(item.get(key))
                    .action(AttributeAction.PUT)
                    .build()
            );
        }

        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .key(tableKey)
                .tableName(tableName)
                .attributeUpdates(itemUpdate)
                .returnValues(ReturnValue.ALL_NEW)
                .build();
        UpdateItemResponse response = ddb.updateItem(updateItemRequest);
        return adDto;

    }
}
