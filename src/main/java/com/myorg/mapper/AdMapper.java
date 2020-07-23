package com.myorg.mapper;

import com.myorg.dto.*;
import org.w3c.dom.Attr;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;
import java.util.function.Consumer;

public class AdMapper {

    public AdDto fromDynamoToAdDto(Map<String, AttributeValue> items) {

        System.out.println("items = " + items);

        AdDto adDto = new AdDto();

        adDto.setId(items.get("id").s());
        if (items.get("payload") != null) {
            adDto.setPayload(items.get("payload").s());
        }
        if (items.get("encoding") != null && !items.get("encoding").s().isEmpty()) {
            adDto.setEncoding(items.get("encoding").s());
        }
        if (items.get("compression") != null && !items.get("compression").s().isEmpty()) {
            adDto.setCompression(items.get("compression").s());
        }
        if (items.get("duration") != null && !items.get("duration").n().isEmpty()) {
            adDto.setDuration(Long.parseLong(items.get("duration").n()));
        }


        return adDto;
    }

    public List<AdDto> fromDynamoListToAdDto(List<Map<String, AttributeValue>> items) {

        List<AdDto> adDtoList = new ArrayList<>();

        for (Map<String, AttributeValue> item : items) {
            adDtoList.add(fromDynamoToAdDto(item));
        }

        return adDtoList;
    }

    public Map<String, AttributeValue> fromAdDtoToDynamoEntry(AdDto adDto) {

        Map<String, AttributeValue> items = new HashMap<>();

        if (adDto.getPayload() != null) {
            AttributeValue payloadValue = AttributeValue.builder().s(adDto.getPayload()).build();
            items.put("payload", payloadValue);
        }
        if (adDto.getEncoding() != null) {
            AttributeValue encodingValue = AttributeValue.builder().s(adDto.getEncoding()).build();
            items.put("encoding", encodingValue);
        }
        if (adDto.getCompression() != null) {
            AttributeValue compressionValue = AttributeValue.builder().s(adDto.getCompression()).build();
            items.put("compression", compressionValue);
        }
        if (adDto.getDuration() != null) {
            AttributeValue durationValue = AttributeValue.builder().n(String.valueOf(adDto.getDuration())).build();
            items.put("duration", durationValue);
        }

        return items;
    }
}
