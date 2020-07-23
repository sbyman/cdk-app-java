package com.myorg;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.apigateway.IResource;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableProps;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CdkAppJavaStack extends Stack {

    public CdkAppJavaStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public CdkAppJavaStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        TableProps totemTableProps;
        Attribute totemPartitionKey = Attribute.builder()
                .name("id")
                .type(AttributeType.STRING)
                .build();
        totemTableProps = TableProps.builder()
                .tableName("totems")
                .partitionKey(totemPartitionKey)
                // The default removal policy is RETAIN, which means that cdk destroy will not attempt to delete
                // the new table, and it will remain in your account until manually deleted. By setting the policy to
                // DESTROY, cdk destroy will delete the table (even if it has data in it)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
        Table totemDynamodbTable = new Table(this, "totems", totemTableProps);

        TableProps adTableProps;
        Attribute adPartitionKey = Attribute.builder()
                .name("id")
                .type(AttributeType.STRING)
                .build();
        adTableProps = TableProps.builder()
                .tableName("ads")
                .partitionKey(adPartitionKey)
                // The default removal policy is RETAIN, which means that cdk destroy will not attempt to delete
                // the new table, and it will remain in your account until manually deleted. By setting the policy to
                // DESTROY, cdk destroy will delete the table (even if it has data in it)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
        Table adDynamodbTable = new Table(this, "ads", adTableProps);

        Map<String, String> lambdaEnvMap = new HashMap<>();
        lambdaEnvMap.put("TOTEM_TABLE_NAME", totemDynamodbTable.getTableName());
        lambdaEnvMap.put("TOTEM_PRIMARY_KEY","id");
        lambdaEnvMap.put("AD_TABLE_NAME", adDynamodbTable.getTableName());
        lambdaEnvMap.put("AD_PRIMARY_KEY","id");

        //Functions for Totem Lambdas
        final Function getAllTotemsFunction = new Function(this, "getAllTotemsFunction", getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.totem.GetAllTotemsHandler"));
        final Function getTotemByIdFunction = new Function(this, "getTotemByIdFunction", getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.totem.GetTotemByIdHandler"));
        final Function createTotemFunction = new Function(this, "createTotemFunction", getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.totem.CreateTotemHandler"));
        final Function updateTotemFunction = new Function(this, "updateTotemFunction", getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.totem.UpdateTotemHandler"));
        final Function deleteTotemFunction = new Function(this, "deleteTotemFunction", getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.totem.DeleteTotemHandler"));

        //Functions for AD Lambdas
        final Function getAllAdsFunction = new Function(this, "getAllAdsFunction", getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.ads.GetAllAdsHandler"));
        final Function getAdByIdFunction = new Function(this, "getAdByIdFunction", getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.ads.GetAdByIdHandler"));
        final Function createAdFunction = new Function(this, "createAdFunction", getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.ads.CreateAdHandler"));
        final Function updateAdFunction = new Function(this, "updateAdFunction", getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.ads.UpdateAdHandler"));
        final Function deleteAdFunction = new Function(this, "deleteAdFunction", getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.ads.DeleteAdHandler"));

        //DynamoDB permission for totem Lambdas
        totemDynamodbTable.grantReadData(getTotemByIdFunction);
        totemDynamodbTable.grantReadData(getAllTotemsFunction);
        totemDynamodbTable.grantReadWriteData(createTotemFunction);
        totemDynamodbTable.grantReadWriteData(updateTotemFunction);
        totemDynamodbTable.grantReadWriteData(deleteTotemFunction);

        //DynamoDB permission for ad Lambdas
        adDynamodbTable.grantReadData(getAllAdsFunction);
        adDynamodbTable.grantReadData(getAdByIdFunction);
        adDynamodbTable.grantReadWriteData(createAdFunction);
        adDynamodbTable.grantReadWriteData(updateAdFunction);
        adDynamodbTable.grantReadWriteData(deleteAdFunction);

        Map<String, String> integrationResponseParameters = new HashMap<>();
        integrationResponseParameters.put("method.response.header.Access-Control-Allow-Headers","'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token,X-Amz-User-Agent'");
        integrationResponseParameters.put("method.response.header.Access-Control-Allow-Origin","'*'");
        integrationResponseParameters.put("method.response.header.Access-Control-Allow-Methods","'OPTIONS,GET,PUT,POST,DELETE'");

        //Totems API Gateway resources configuration
        RestApi totemApi = new RestApi(this, "totemApi",
                RestApiProps.builder().restApiName("Totems Service").build());

        IResource totems = totemApi.getRoot().addResource("totems");

        Integration getAllTotemsIntegration = new LambdaIntegration(getAllTotemsFunction);
        totems.addMethod("GET", getAllTotemsIntegration);
        Integration createTotemIntegration = new LambdaIntegration(createTotemFunction);
        totems.addMethod("POST", createTotemIntegration);
        //addCorsOptions(totems);

        IResource singleTotem = totems.addResource("{id}");
        Integration getOneTotemIntegration = new LambdaIntegration(getTotemByIdFunction);
        singleTotem.addMethod("GET", getOneTotemIntegration);

        Integration updateTotemIntegration = new LambdaIntegration(updateTotemFunction);
        singleTotem.addMethod("PUT", updateTotemIntegration);

        Integration deleteOneTotemIntegration = new LambdaIntegration(deleteTotemFunction);
        singleTotem.addMethod("DELETE",deleteOneTotemIntegration);
        //addCorsOptions(singleTotem);

        //Ads API Gateway resources configuration
        RestApi adApi = new RestApi(this, "adApi",
                RestApiProps.builder()
                        .restApiName("Ad Service")
                        .defaultCorsPreflightOptions(CorsOptions.builder()
                                .allowMethods(Cors.ALL_METHODS)
                                .allowOrigins(Cors.ALL_ORIGINS)
                                .allowHeaders(Cors.DEFAULT_HEADERS)
                                .build()).build());

        IResource ads = adApi.getRoot().addResource("ads");

        Integration getAllAdsIntegration = new LambdaIntegration(getAllAdsFunction);
        ads.addMethod("GET", getAllAdsIntegration);
        Integration createAdIntegration = new LambdaIntegration(createAdFunction);
        ads.addMethod("POST", createAdIntegration);
        //addCorsOptions(ads);

        IResource singleAd = ads.addResource("{id}");
        Integration getOneAdIntegration = new LambdaIntegration(getAdByIdFunction);
        singleAd.addMethod("GET", getOneAdIntegration);

        Integration updateAdIntegration = new LambdaIntegration(updateAdFunction);
        singleAd.addMethod("PUT", updateAdIntegration);

        Integration deleteOneAdIntegration = new LambdaIntegration(deleteAdFunction);
        singleAd.addMethod("DELETE", deleteOneAdIntegration);
        //addCorsOptions(singleAd);

    }

    private void addCorsOptions(IResource item) {
        List<MethodResponse> methoedResponses = new ArrayList<>();

        Map<String, Boolean> responseParameters = new HashMap<>();
        responseParameters.put("method.response.header.Access-Control-Allow-Headers", Boolean.TRUE);
        responseParameters.put("method.response.header.Access-Control-Allow-Methods", Boolean.TRUE);
        responseParameters.put("method.response.header.Access-Control-Allow-Origin", Boolean.TRUE);
        methoedResponses.add(MethodResponse.builder()
                .responseParameters(responseParameters)
                .statusCode("200")
                .build());
        MethodOptions methodOptions = MethodOptions.builder()
                .methodResponses(methoedResponses)
                .build()
                ;

        Map<String, String> requestTemplate = new HashMap<>();
        requestTemplate.put("application/json","{\"statusCode\": 200}");
        List<IntegrationResponse> integrationResponses = new ArrayList<>();

        Map<String, String> integrationResponseParameters = new HashMap<>();
        integrationResponseParameters.put("method.response.header.Access-Control-Allow-Headers","'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token,X-Amz-User-Agent'");
        integrationResponseParameters.put("method.response.header.Access-Control-Allow-Origin","'*'");
        integrationResponseParameters.put("method.response.header.Access-Control-Allow-Methods","'OPTIONS,GET,PUT,POST,DELETE'");
        integrationResponses.add(IntegrationResponse.builder()
                .responseParameters(integrationResponseParameters)
                .statusCode("200")
                .build());
        Integration methodIntegration = MockIntegration.Builder.create()
                .integrationResponses(integrationResponses)
                .passthroughBehavior(PassthroughBehavior.NEVER)
                .requestTemplates(requestTemplate)
                .build();

        item.addMethod("OPTIONS", methodIntegration, methodOptions);
    }

    private FunctionProps getLambdaFunctionProps(Map<String, String> lambdaEnvMap, String reference){
        return FunctionProps.builder()
                .code(Code.fromAsset("./target/cdk-app-java-0.1-jar-with-dependencies.jar"))
                .handler(reference)
                .runtime(Runtime.JAVA_8)
                .environment(lambdaEnvMap)
                .timeout(Duration.seconds(30))
                .memorySize(512)
                .build();
    }
}
