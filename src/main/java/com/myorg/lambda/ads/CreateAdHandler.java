package com.myorg.lambda.ads;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myorg.dto.AdDto;
import com.myorg.dto.GatewayResponse;
import com.myorg.mapper.AdMapper;
import com.myorg.utils.CORSHeaderSetter;
import com.myorg.utils.ErrorMessageGenerator;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.util.*;

public class CreateAdHandler implements RequestHandler<Map<String,Object>, GatewayResponse>{

    @Override
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Inside com.myorg.lambda.ads: CreateAdHandler " + input.getClass() + " data:" + input);

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

        AdDto output = createItem(adDto);

        String response = null;
        try {
            response = objectMapper.writeValueAsString(output);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(ErrorMessageGenerator.generateErrorMessage("InternalServerError", 500, context.getAwsRequestId(), "Insert executed successfully but something in the response parsing went wrong"));
        }

        Map<String, String> headers = new HashMap<>();
        //headers = CORSHeaderSetter.setCORSHeader(headers);
        headers.put("Content-Type", "application/json");

        return new GatewayResponse(response, headers, 200);
    }

    private AdDto createItem(AdDto adDto) {
        DynamoDbClient ddb = DynamoDbClient.create();
        String tableName= System.getenv("AD_TABLE_NAME");
        String primaryKey = System.getenv("AD_PRIMARY_KEY");

        AdMapper adMapper = new AdMapper();

        Map<String, AttributeValue> item = adMapper.fromAdDtoToDynamoEntry(adDto);

        String id = UUID.randomUUID().toString();
        item.put(primaryKey, AttributeValue.builder().s(id).build());
        adDto.setId(id);

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();
        PutItemResponse response = ddb.putItem(putItemRequest);

        return adDto;

    }
}
