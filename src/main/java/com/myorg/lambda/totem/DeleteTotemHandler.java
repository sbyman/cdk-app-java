package com.myorg.lambda.totem;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.myorg.dto.GatewayResponse;
import com.myorg.utils.CORSHeaderSetter;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

public class DeleteTotemHandler implements RequestHandler<Map<String,Object>, GatewayResponse>{

    @Override
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Inside com.myorg.lambda.totem: DeleteAdHandler "+input.getClass()+ " data: "+input);

        Map<String, Object> pathParameters = (Map<String, Object>)input.get("pathParameters");

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String id = (String) pathParameters.get("id");
        logger.log("updating data for input parameter:"+id);
        String output = deleteItem(id);
        //headers = CORSHeaderSetter.setCORSHeader(headers);
        return new GatewayResponse(output, headers, 200);
    }

    private String deleteItem(String id) {
        DynamoDbClient ddb = DynamoDbClient.create();
        String tableName= System.getenv("TOTEM_TABLE_NAME");
        String primaryKey = System.getenv("TOTEM_PRIMARY_KEY");
        Map<String, AttributeValue> tableKey = new HashMap<>();
        tableKey.put(primaryKey, AttributeValue.builder().s(id).build());
        DeleteItemRequest getItemRequest= DeleteItemRequest.builder()
                .key(tableKey)
                .tableName(tableName)
                .build();
        DeleteItemResponse response = ddb.deleteItem(getItemRequest);
        return response.toString();

    }
}
