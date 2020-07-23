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
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.util.HashMap;
import java.util.Map;

public class GetAdByIdHandler implements RequestHandler<Map<String,Object>, GatewayResponse>{


    @Override
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Inside com.myorg.lambda.ads: GetAdByIdHandler " + input.getClass() + " data:" + input);

        Map<String, Object> pathParameters = (Map<String, Object>)input.get("pathParameters");
        String id = (String)pathParameters.get("id");
        logger.log("Getting data for input parameter:"+id);

        AdDto response = getData(id);

        Map<String, String> headers = new HashMap<>();
        //headers = CORSHeaderSetter.setCORSHeader(headers);
        headers.put("Content-Type", "application/json");

        GatewayResponse gatewayResponse;

        if(response == null){
            gatewayResponse = new GatewayResponse(null, headers, 404);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();

            String output = null;
            try {
                output = objectMapper.writeValueAsString(response);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new RuntimeException(ErrorMessageGenerator.generateErrorMessage("InternalServerError", 500, context.getAwsRequestId(), "Error in parsing output message"));
            }
            gatewayResponse = new GatewayResponse(output, headers, 200);
        }

        return gatewayResponse;
    }

    private AdDto getData(String id) {
        DynamoDbClient ddb = DynamoDbClient.create();
        String tableName= System.getenv("AD_TABLE_NAME");
        String primaryKey = System.getenv("AD_PRIMARY_KEY");
        Map<String, AttributeValue> tableKey = new HashMap<>();
        tableKey.put(primaryKey, AttributeValue.builder().s(id).build());
        GetItemRequest getItemRequest= GetItemRequest.builder()
                .key(tableKey)
                .tableName(tableName)
                .build();
        GetItemResponse response = ddb.getItem(getItemRequest);

        AdDto adDto = null;

        if(response.hasItem()){
            AdMapper totemMapper = new AdMapper();
            adDto = totemMapper.fromDynamoToAdDto(response.item());
        }
        return adDto;
    }
}