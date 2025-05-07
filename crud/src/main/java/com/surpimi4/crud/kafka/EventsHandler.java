package com.surpimi4.crud.kafka;


import com.suprimi4.events.*;
import com.surpimi4.crud.service.GeocodeService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class EventsHandler {
    private final GeocodeService geocodeService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventsHandler(GeocodeService geocodeService,
                         KafkaTemplate<String, Object> kafkaTemplate) {
        this.geocodeService = geocodeService;
        this.kafkaTemplate = kafkaTemplate;
    }


    public void handleHomeAddress(AddressEvent event) {
        handleAddress(event, EventType.HOME_ADDRESS);
    }

    public void handleWorkAddress(AddressEvent event) {
        handleAddress(event, EventType.WORK_ADDRESS);
    }

    private void handleAddress(AddressEvent event, EventType eventType) {
        Long chatId = event.getChatId();
        String address = event.getAddress();
        boolean isValid = false;
        switch (eventType) {
            case HOME_ADDRESS -> {
                geocodeService.saveHomeAddressInfoAndTimezone(chatId, address);
                isValid = geocodeService.userHomeAddressIsExist(chatId);
            }
            case WORK_ADDRESS -> {
                geocodeService.saveWorkAddressInfo(chatId, address);
                isValid = geocodeService.userWorkAddressIsExist(chatId);
            }
        }


        AddressValidationEvent validationEvent = new AddressValidationEvent(
                chatId,
                isValid,
                eventType
        );

        kafkaTemplate.send("bot-command-confirm-topic", String.valueOf(chatId), validationEvent);
    }


    public void deleteUserInfo(UserEvent event) {
        Long chatId = event.getChatId();
        geocodeService.deleteUserInfo(chatId);

    }


    public void handleTime(TimeEvent event) {
        Long chatId = event.getChatId();
        LocalTime time = event.getTime();
        geocodeService.saveUserTime(chatId, time);
    }

    @KafkaListener(topics = "${spring.kafka.topic}")
    public void handleBotRequest(EventWrapper wrapper) {
        switch (wrapper.getEventType()) {
            case HOME_ADDRESS -> handleHomeAddress((AddressEvent) wrapper.getEvent());
            case WORK_ADDRESS -> handleWorkAddress((AddressEvent) wrapper.getEvent());
            case TIME -> handleTime((TimeEvent) wrapper.getEvent());
            case DELETE_USER -> deleteUserInfo((UserEvent) wrapper.getEvent());
        }
    }

    public void sendAlertInfo(Long chatId, AlertEvent alertEvent) {
        kafkaTemplate.send("alert-topic", String.valueOf(chatId), alertEvent);
    }
}
