package suprimi4.timetogobot.kafka;

import com.suprimi4.events.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import suprimi4.timetogobot.config.KafkaConfig;
import suprimi4.timetogobot.model.MessageState;
import suprimi4.timetogobot.service.TelegramService;


@Service
public class EventsHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TelegramService telegramService;


    public EventsHandler(KafkaTemplate<String, Object> kafkaTemplate, TelegramService telegramService) {
        this.kafkaTemplate = kafkaTemplate;
        this.telegramService = telegramService;
    }

    public void sendHomeAddressRequest(AddressEvent event) {
        kafkaTemplate.send(KafkaConfig.BOT_COMMAND_TOPIC, String.valueOf(event.getChatId()), new EventWrapper(EventType.HOME_ADDRESS, event));
    }

    public void sendWorkAddressRequest(AddressEvent event) {
        kafkaTemplate.send(KafkaConfig.BOT_COMMAND_TOPIC, String.valueOf(event.getChatId()), new EventWrapper(EventType.WORK_ADDRESS, event));
    }

    public void sendWorkTimeRequest(TimeEvent event) {
        kafkaTemplate.send(KafkaConfig.BOT_COMMAND_TOPIC, String.valueOf(event.getChatId()), new EventWrapper(EventType.TIME, event));
    }

    public void sendUserInfoRequest(UserEvent event) {
        kafkaTemplate.send(KafkaConfig.BOT_COMMAND_TOPIC, String.valueOf(event.getChatId()), new EventWrapper(EventType.USER_INFO, event));
    }

    public void deleteUserInfo(UserEvent event) {
        kafkaTemplate.send(KafkaConfig.BOT_COMMAND_TOPIC, String.valueOf(event.getChatId()), new EventWrapper(EventType.DELETE_USER, event));
    }

    @KafkaListener(topics = KafkaConfig.ALERT_TOPIC)
    public void handleArriveAlert(AlertEvent alertEvent) {
        Long chatId = alertEvent.getChatId();
        String message = alertEvent.getMessage();
        telegramService.sendMessage(chatId, message);
    }


    @KafkaListener(topics = KafkaConfig.BOT_COMMAND_CONFIRM_TOPIC)
    public void handleAddressConfirmation(AddressValidationEvent event) {
        Long chatId = event.getChatId();
        if (event.isValid()) {
            handleValidAddress(event, chatId);
        } else {
            telegramService.sendMessage(chatId, "Адрес не найден, введите заново");
        }
    }

    private void handleValidAddress(AddressValidationEvent event, Long chatId) {
        switch (event.getAddressType()) {
            case HOME_ADDRESS -> {
                telegramService.sendMessage(chatId, "Теперь введи адрес работы:");
                telegramService.setMessageState(chatId, MessageState.WAITING_WORK_ADDRESS);
            }
            case WORK_ADDRESS -> {
                telegramService.sendMessage(chatId, "Теперь введи рабочее время:");
                telegramService.setMessageState(chatId, MessageState.WAITING_WORK_TIME);
            }
        }
    }

}
