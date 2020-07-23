package com.myorg;

import com.myorg.dto.AdDto;
import com.myorg.dto.TotemDto;
import com.myorg.mapper.AdMapper;
import com.myorg.mapper.TotemMapper;
import org.junit.Before;
import software.amazon.awscdk.core.App;
import org.junit.Test;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThat;

public class CdkAppJavaStackTest {

    private Map<String, AttributeValue> totemValueMap;
    private Map<String, AttributeValue> adValueMap;

    @Test
    public void testStack() throws IOException {
        App app = new App();
        CdkAppJavaStack stack = new CdkAppJavaStack(app, "test");
    }

    @Before
    public void init() {
        Map<String, AttributeValue> totemValueMap = new HashMap<>();
        AttributeValue totemIdValue = AttributeValue.builder().s("Test_01").build();
        totemValueMap.put("id", totemIdValue);

        AttributeValue imeiValue = AttributeValue.builder().s("Test_imei_001").build();
        totemValueMap.put("imei", imeiValue);

        AttributeValue firmwareValue = AttributeValue.builder().s("Test_firmware_001").build();
        totemValueMap.put("firmware", firmwareValue);

        AttributeValue customerValue = AttributeValue.builder().s("CustomerId_Test_001").build();
        totemValueMap.put("customerId", customerValue);

        AttributeValue modelValue = AttributeValue.builder().s("Model_Test_001").build();
        totemValueMap.put("model", modelValue);

        Map<String, AttributeValue> locationValueMap = new HashMap<>();

        AttributeValue addressValue = AttributeValue.builder().s("Test address, 12345").build();
        locationValueMap.put("address", addressValue);

        AttributeValue floorValue = AttributeValue.builder().s("Test Floor 01").build();
        locationValueMap.put("floor", floorValue);

        AttributeValue locationValue = AttributeValue.builder().m(locationValueMap).build();
        totemValueMap.put("location", locationValue);

        Map<String, AttributeValue> displayValueMap = new HashMap<>();

        AttributeValue heightValue = AttributeValue.builder().n(String.valueOf(768)).build();
        displayValueMap.put("height", heightValue);

        AttributeValue widthValue = AttributeValue.builder().n(String.valueOf(1024)).build();
        displayValueMap.put("width", widthValue);

        AttributeValue resolutionValue = AttributeValue.builder().n(String.valueOf(1080)).build();
        displayValueMap.put("resolution", resolutionValue);

        Map<String, AttributeValue> hardwareValueMap = new HashMap<>();

        AttributeValue nameValue = AttributeValue.builder().s("Test_hardware_001").build();
        hardwareValueMap.put("name", nameValue);

        AttributeValue ramValue = AttributeValue.builder().s("512MB").build();
        hardwareValueMap.put("ram", ramValue);

        AttributeValue romValue = AttributeValue.builder().s("1GB").build();
        hardwareValueMap.put("rom", romValue);

        AttributeValue displayValue = AttributeValue.builder().m(displayValueMap).build();
        hardwareValueMap.put("display", displayValue);

        AttributeValue hardwareValue = AttributeValue.builder().m(hardwareValueMap).build();
        totemValueMap.put("hardware", hardwareValue);

        List<AttributeValue> scheduledValueList = new ArrayList<>();

        Map<String, AttributeValue> scheduledValueMap1 = new HashMap<>();
        Map<String, AttributeValue> scheduledValueMap2 = new HashMap<>();


        AttributeValue adId1 = AttributeValue.builder().s("AD_ID_001").build();
        AttributeValue startTime1 = AttributeValue.builder().n("134567890").build();
        AttributeValue endTime1 = AttributeValue.builder().n("4678945456").build();
        scheduledValueMap1.put("adId", adId1);
        scheduledValueMap1.put("streamStartTime", startTime1);
        scheduledValueMap1.put("streamEndTime", endTime1);

        AttributeValue adId2 = AttributeValue.builder().s("AD_ID_002").build();
        AttributeValue startTime2 = AttributeValue.builder().n("134567890").build();
        AttributeValue endTime2 = AttributeValue.builder().n("4678945456").build();
        scheduledValueMap2.put("adId", adId2);
        scheduledValueMap2.put("streamStartTime", startTime2);
        scheduledValueMap2.put("streamEndTime", endTime2);

        scheduledValueList.add(AttributeValue.builder().m(scheduledValueMap1).build());
        scheduledValueList.add(AttributeValue.builder().m(scheduledValueMap2).build());

        AttributeValue scheduledListValue = AttributeValue.builder().l(scheduledValueList).build();
        totemValueMap.put("adSchedule", scheduledListValue);

        this.totemValueMap = totemValueMap;

        Map<String, AttributeValue> adValueMap = new HashMap<>();
        AttributeValue idValue = AttributeValue.builder().s("Test_01").build();
        adValueMap.put("id", idValue);

        byte[] payload = "TEST_PAYLOAD_0001".getBytes();

        AttributeValue payloadValue = AttributeValue.builder().b(SdkBytes.fromByteArray(payload)).build();
        adValueMap.put("payload", payloadValue);

        AttributeValue encodingValue = AttributeValue.builder().s("MP4_Test_Encoding").build();
        adValueMap.put("encoding", encodingValue);

        adValueMap.put("customerId", customerValue);

        AttributeValue compressionValue = AttributeValue.builder().s("GZIP_Test_Compression").build();
        adValueMap.put("compression", compressionValue);

        AttributeValue durationValue = AttributeValue.builder().n(String.valueOf(1234565334L)).build();
        adValueMap.put("duration", durationValue);

        List<String> totemIdList = new ArrayList<>();
        totemIdList.add("TOTEM_ID_001");
        totemIdList.add("TOTEM_ID_002");
        totemIdList.add("TOTEM_ID_003");

        List<AttributeValue> totems = new ArrayList<>();

        for (String totemId : totemIdList) {
            AttributeValue totem = AttributeValue.builder().s(totemId).build();
            totems.add(totem);
        }

        AttributeValue totemsValue = AttributeValue.builder().l(totems).build();
        adValueMap.put("totems", totemsValue);

        this.adValueMap = adValueMap;
    }

    @Test
    public void testAdMapper() {
        GetItemResponse getItemResponse = GetItemResponse.builder().item(adValueMap).build();

        AdMapper adMapper = new AdMapper();

        System.out.println(adMapper.fromDynamoToAdDto(getItemResponse.item()));
    }


    @Test
    public void testDynamoAdMapper() {

        GetItemResponse getItemResponse = GetItemResponse.builder().item(adValueMap).build();

        AdMapper adMapper = new AdMapper();

        AdDto adDto = adMapper.fromDynamoToAdDto(getItemResponse.item());

        Map<String, AttributeValue> item = adMapper.fromAdDtoToDynamoEntry(adDto);
        System.out.println(item);
    }

    @Test
    public void testTotemMapper() {
        GetItemResponse getItemResponse = GetItemResponse.builder().item(totemValueMap).build();

        TotemMapper totemMapper = new TotemMapper();

        System.out.println(totemMapper.fromDynamoToTotemDto(getItemResponse.item()));
    }

    @Test
    public void testListMapper() {

        List<Map<String, AttributeValue>> mapList = new ArrayList<>();

        Map<String, AttributeValue> valueMap1 = new HashMap<>();
        AttributeValue totemIdValue = AttributeValue.builder().s("01").build();
        valueMap1.put("id", totemIdValue);

        AttributeValue imeiValue = AttributeValue.builder().s("12345678").build();
        valueMap1.put("imei", imeiValue);

        AttributeValue firmwareValue = AttributeValue.builder().s("1.231.33").build();
        valueMap1.put("firmware", firmwareValue);

        Map<String, AttributeValue> valueMap2 = new HashMap<>();
        totemIdValue = AttributeValue.builder().s("02").build();
        valueMap2.put("id", totemIdValue);

        imeiValue = AttributeValue.builder().s("9765432").build();
        valueMap2.put("imei", imeiValue);

        firmwareValue = AttributeValue.builder().s("1.231.37").build();
        valueMap2.put("firmware", firmwareValue);

        mapList.add(valueMap1);
        mapList.add(valueMap2);

        ScanResponse scanResponse = ScanResponse.builder().items(mapList).build();
        TotemMapper totemMapper = new TotemMapper();
        System.out.println(totemMapper.fromDynamoListToTotemDto(scanResponse.items()));
    }

    @Test
    public void testDynamoTotemMapper() {

        GetItemResponse getItemResponse = GetItemResponse.builder().item(totemValueMap).build();

        TotemMapper totemMapper = new TotemMapper();

        TotemDto totemDto = totemMapper.fromDynamoToTotemDto(getItemResponse.item());

        Map<String, AttributeValue> item = totemMapper.fromTotemDtoToDynamoEntry(totemDto);
        System.out.println(item);
    }
}
