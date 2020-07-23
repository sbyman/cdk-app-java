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
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAllTotemsHandler implements RequestHandler<Object, GatewayResponse>{

    @Override
    public GatewayResponse handleRequest(Object input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Inside com.myorg.lambda.totem: GetAllAdsHandler");

        List<TotemDto> response = getData(context);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        GatewayResponse gatewayResponse;

        if(response == null || response.isEmpty()){
            gatewayResponse = new GatewayResponse("[]", headers, 200);
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
        //headers = CORSHeaderSetter.setCORSHeader(headers);
        return gatewayResponse;
    }

    private List<TotemDto> getData(Context context) {
        DynamoDbClient ddb = DynamoDbClient.create();
        String tableName= System.getenv("TOTEM_TABLE_NAME");
        ScanRequest scanRequest= ScanRequest.builder()
                .tableName(tableName)
                .build();
        ScanResponse response = ddb.scan(scanRequest);

        List<TotemDto> totemDtoList = null;

        if(response.hasItems()){
            TotemMapper totemMapper = new TotemMapper();
            totemDtoList = totemMapper.fromDynamoListToTotemDto(response.items());
        }

        return totemDtoList;
    }
}
