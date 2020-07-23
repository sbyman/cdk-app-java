package com.myorg.mapper;

import com.myorg.dto.*;
import org.jetbrains.annotations.Async;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TotemMapper {

    public TotemDto fromDynamoToTotemDto(Map<String, AttributeValue> items) {

        System.out.println("items = " + items);

        TotemDto totemDto = new TotemDto();

        totemDto.setId(items.get("id").s());
        if (items.get("firmware") != null && !items.get("firmware").s().isEmpty()) {
            totemDto.setFirmware(items.get("firmware").s());
        }
        if (items.get("imei") != null && !items.get("imei").s().isEmpty()) {
            totemDto.setImei(items.get("imei").s());
        }
        if (items.get("customerId") != null && !items.get("customerId").s().isEmpty()) {
            totemDto.setCustomerId(items.get("customerId").s());
        }
        if (items.get("model") != null && !items.get("model").s().isEmpty()) {
            totemDto.setModel(items.get("model").s());
        }
        if (items.get("location") != null && !items.get("location").m().isEmpty()) {
            totemDto.setLocation(fromDynamoToLocation(items.get("location").m()));
        }
        if (items.get("hardware") != null && !items.get("hardware").m().isEmpty()) {
            totemDto.setHardware(fromDynamoToHardware(items.get("hardware").m()));
        }
        if (items.get("adSchedule") != null && !items.get("adSchedule").l().isEmpty()) {
            totemDto.setAdSchedule(fromDynamoListToScheduleList(items.get("adSchedule").l()));
        }

        return totemDto;
    }

    private List<ScheduledAds> fromDynamoListToScheduleList(List<AttributeValue> adScheduleItems) {
        List<ScheduledAds> scheduledAds = new ArrayList<>();
        for(AttributeValue attributeValue : adScheduleItems){
            if(attributeValue != null && !attributeValue.m().isEmpty())
            scheduledAds.add(fromDynamoToSchedule(attributeValue.m()));
        }
        return scheduledAds;
    }

    private ScheduledAds fromDynamoToSchedule(Map<String, AttributeValue> adScheduleItem) {
        ScheduledAds scheduledAds = new ScheduledAds();
        if (adScheduleItem.get("adId") != null && !adScheduleItem.get("adId").s().isEmpty()) {
            scheduledAds.setAdId(adScheduleItem.get("adId").s());
        }
        if (adScheduleItem.get("streamStartTime") != null && !adScheduleItem.get("streamStartTime").n().isEmpty()) {
            scheduledAds.setStreamStartTime(Long.parseLong(adScheduleItem.get("streamStartTime").n()));
        }
        if (adScheduleItem.get("streamEndTime") != null && !adScheduleItem.get("streamEndTime").n().isEmpty()) {
            scheduledAds.setStreamEndTime(Long.parseLong(adScheduleItem.get("streamEndTime").n()));
        }
        return scheduledAds;
    }


    private Location fromDynamoToLocation(Map<String, AttributeValue> dynamoLocation) {
        Location location = new Location();

        if (dynamoLocation.get("address") != null && !dynamoLocation.get("address").s().isEmpty()) {
            location.setAddress(dynamoLocation.get("address").s());
        }
        if (dynamoLocation.get("floor") != null && !dynamoLocation.get("floor").s().isEmpty()) {
            location.setFloor(dynamoLocation.get("floor").s());
        }

        return location;
    }

    private Hardware fromDynamoToHardware(Map<String, AttributeValue> dynamoHardware) {
        Hardware hardware = new Hardware();
        if (dynamoHardware.get("name") != null && !dynamoHardware.get("name").s().isEmpty()) {
            hardware.setName(dynamoHardware.get("name").s());
        }
        if (dynamoHardware.get("ram") != null && !dynamoHardware.get("ram").s().isEmpty()) {
            hardware.setRam(dynamoHardware.get("ram").s());
        }
        if (dynamoHardware.get("rom") != null && !dynamoHardware.get("rom").s().isEmpty()) {
            hardware.setRom(dynamoHardware.get("rom").s());
        }
        if (dynamoHardware.get("display") != null && !dynamoHardware.get("display").m().isEmpty()) {
            hardware.setDisplay(fromDynamoToDisplay(dynamoHardware.get("display").m()));
        }

        return hardware;
    }

    private Display fromDynamoToDisplay(Map<String, AttributeValue> dynamoDisplay) {
        Display display = new Display();
        if (dynamoDisplay.get("height") != null && !dynamoDisplay.get("height").n().isEmpty()) {
            display.setHeight(Integer.parseInt(dynamoDisplay.get("height").n()));
        }
        if (dynamoDisplay.get("width") != null && !dynamoDisplay.get("width").n().isEmpty()) {
            display.setWidth(Integer.parseInt(dynamoDisplay.get("width").n()));
        }
        if (dynamoDisplay.get("resolution") != null && !dynamoDisplay.get("resolution").n().isEmpty()) {
            display.setResolution(Integer.parseInt(dynamoDisplay.get("resolution").n()));
        }

        return display;
    }

    public List<TotemDto> fromDynamoListToTotemDto(List<Map<String, AttributeValue>> items) {

        List<TotemDto> totemDtoList = new ArrayList<>();

        for (Map<String, AttributeValue> item : items) {
            totemDtoList.add(fromDynamoToTotemDto(item));
        }

        return totemDtoList;
    }

    public Map<String, AttributeValue> fromTotemDtoToDynamoEntry(TotemDto totemDto) {

        Map<String, AttributeValue> items = new HashMap<>();
        if (totemDto.getFirmware() != null) {

            AttributeValue firmwareValue = AttributeValue.builder().s(totemDto.getFirmware()).build();
            items.put("firmware", firmwareValue);
        }
        if (totemDto.getImei() != null) {

            AttributeValue imeiValue = AttributeValue.builder().s(totemDto.getImei()).build();
            items.put("imei", imeiValue);
        }
        if (totemDto.getCustomerId() != null) {

            AttributeValue customerValue = AttributeValue.builder().s(totemDto.getCustomerId()).build();
            items.put("customerId", customerValue);
        }
        if (totemDto.getModel() != null) {

            AttributeValue modelValue = AttributeValue.builder().s(totemDto.getModel()).build();
            items.put("model", modelValue);
        }
        if (totemDto.getLocation() != null) {
            AttributeValue locationValue = AttributeValue.builder().m(fromLocationToDynamoEntry(totemDto.getLocation())).build();
            items.put("location", locationValue);
        }

        if (totemDto.getHardware() != null) {
            AttributeValue hardwareValue = AttributeValue.builder().m(fromHardwareToDynamoEntry(totemDto.getHardware())).build();
            items.put("hardware", hardwareValue);
        }
        if(totemDto.getAdSchedule() != null && !totemDto.getAdSchedule().isEmpty()){
            AttributeValue scheduleAdListValue = AttributeValue.builder().l(fromScheduledAdListToDynamoEntry(totemDto.getAdSchedule())).build();
            items.put("adSchedule", scheduleAdListValue);
        }

        return items;
    }

    private Map<String, AttributeValue> fromHardwareToDynamoEntry(Hardware hardware) {
        Map<String, AttributeValue> items = new HashMap<>();
        if (hardware.getName() != null) {

            AttributeValue nameValue = AttributeValue.builder().s(hardware.getName()).build();
            items.put("name", nameValue);
        }
        if (hardware.getRam() != null) {

            AttributeValue ramValue = AttributeValue.builder().s(hardware.getRam()).build();
            items.put("ram", ramValue);
        }
        if (hardware.getRom() != null) {

            AttributeValue romValue = AttributeValue.builder().s(hardware.getRom()).build();
            items.put("rom", romValue);
        }
        if (hardware.getDisplay() != null) {
            AttributeValue displayValue = AttributeValue.builder().m(fromDisplayToDynamoEntry(hardware.getDisplay())).build();
            items.put("display", displayValue);
        }

        return items;
    }

    private Map<String, AttributeValue> fromDisplayToDynamoEntry(Display display) {
        Map<String, AttributeValue> items = new HashMap<>();

        AttributeValue heightValue = AttributeValue.builder().n(String.valueOf(display.getHeight())).build();
        items.put("height", heightValue);

        AttributeValue lengthValue = AttributeValue.builder().n(String.valueOf(display.getWidth())).build();
        items.put("width", lengthValue);

        AttributeValue resolutionValue = AttributeValue.builder().n(String.valueOf(display.getResolution())).build();
        items.put("resolution", resolutionValue);

        return items;
    }

    private List<AttributeValue> fromScheduledAdListToDynamoEntry(List<ScheduledAds> scheduledAdsList) {
        List<AttributeValue> items = new ArrayList<>();

        for(ScheduledAds scheduledAds : scheduledAdsList){
            AttributeValue scheduledAdsValue = AttributeValue.builder().m(fromScheduledAdToDynamoEntry(scheduledAds)).build();
            items.add(scheduledAdsValue);
        }

        return items;
    }

    private Map<String, AttributeValue> fromScheduledAdToDynamoEntry(ScheduledAds scheduledAds) {
        Map<String, AttributeValue> items = new HashMap<>();

        AttributeValue adIdValue = AttributeValue.builder().n(String.valueOf(scheduledAds.getAdId())).build();
        items.put("adId", adIdValue);

        AttributeValue streamStartValue = AttributeValue.builder().n(String.valueOf(scheduledAds.getStreamStartTime())).build();
        items.put("streamStartTime", streamStartValue);


        AttributeValue streamEndValue = AttributeValue.builder().n(String.valueOf(scheduledAds.getStreamEndTime())).build();
        items.put("streamEndTime", streamEndValue);

        return items;
    }

    private Map<String, AttributeValue> fromLocationToDynamoEntry(Location location) {
        Map<String, AttributeValue> items = new HashMap<>();
        if (location.getAddress() != null) {

            AttributeValue addressValue = AttributeValue.builder().s(location.getAddress()).build();
            items.put("address", addressValue);
        }
        if (location.getFloor() != null) {

            AttributeValue floorValue = AttributeValue.builder().s(location.getFloor()).build();
            items.put("floor", floorValue);
        }

        return items;
    }
}
