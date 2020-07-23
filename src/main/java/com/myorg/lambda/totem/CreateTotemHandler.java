package com.myorg.lambda.totem;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myorg.dto.GatewayResponse;
import com.myorg.dto.TotemDto;
import com.myorg.mapper.TotemMapper;
import com.myorg.utils.CORSHeaderSetter;
import com.myorg.utils.ErrorMessageGenerator;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateTotemHandler implements RequestHandler<Map<String,Object>, GatewayResponse>{

    @Override
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Inside com.myorg.lambda.totem: CreateTotemHandler " + input.getClass() + " data:" + input);

        String body = null;
        if(input.get("body") != null){
            body = (String) input.get("body");
            logger.log("Body is:" + body);
        } else {
            throw new RuntimeException(ErrorMessageGenerator.generateErrorMessage("BadRequest", 400, context.getAwsRequestId(), "Input body is empty"));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        TotemDto totemDto = null;
        try {
            totemDto = objectMapper.readValue((String) input.get("body"), TotemDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(ErrorMessageGenerator.generateErrorMessage("BadRequest", 400, context.getAwsRequestId(), "Input body is not as expected"));
        }

        TotemDto output = createItem(totemDto);

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

    private TotemDto createItem(TotemDto totemDto) {
        DynamoDbClient ddb = DynamoDbClient.create();
        String tableName= System.getenv("TOTEM_TABLE_NAME");
        String primaryKey = System.getenv("TOTEM_PRIMARY_KEY");

        TotemMapper totemMapper = new TotemMapper();

        Map<String, AttributeValue> item = totemMapper.fromTotemDtoToDynamoEntry(totemDto);

        String id = UUID.randomUUID().toString();
        item.put(primaryKey, AttributeValue.builder().s(id).build());

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();
        PutItemResponse response = ddb.putItem(putItemRequest);

        return totemMapper.fromDynamoToTotemDto(putItemRequest.item());

    }
}
