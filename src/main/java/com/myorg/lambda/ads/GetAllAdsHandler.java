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
import org.w3c.dom.Attr;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAllAdsHandler implements RequestHandler<Map<String, Object>, GatewayResponse>{

    @Override
    public GatewayResponse handleRequest(Map<String,Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Inside com.myorg.lambda.ads: GetAllAdsHandler");

        Map<String, Object> queryStringParameters = (Map<String, Object>) input.get("queryStringParameters");

        List<AdDto> response = getData(context, queryStringParameters);

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

    private List<AdDto> getData(Context context, Map<String, Object> queryParameters) {
        DynamoDbClient ddb = DynamoDbClient.create();
        String tableName= System.getenv("AD_TABLE_NAME");

        ScanRequest.Builder scanRequestBuilder= ScanRequest.builder()
                .tableName(tableName);

        ScanRequest scanRequest = scanRequestBuilder.build();
        ScanResponse response = ddb.scan(scanRequest);

        List<AdDto> adDtoList = null;

        if(response.hasItems()){
            AdMapper adMapper = new AdMapper();
            adDtoList = adMapper.fromDynamoListToAdDto(response.items());
        }

        return adDtoList;
    }
}
