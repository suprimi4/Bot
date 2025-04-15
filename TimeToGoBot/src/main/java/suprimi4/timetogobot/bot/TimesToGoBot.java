package suprimi4.timetogobot.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import suprimi4.timetogobot.api.GeocodeApiClient;
import suprimi4.timetogobot.dto.*;
import suprimi4.timetogobot.model.MessageState;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class TimesToGoBot extends TelegramLongPollingBot {
    private final String botUserName;

    private final Map<Long, MessageState> messageState = new HashMap<>();
    private final GeocodeApiClient geocodeApiClient;


    public TimesToGoBot(@Value("${bot.username}") String botUserName,
                        @Value("${bot.token}") String botToken,
                        GeocodeApiClient geocodeApiClient
    ) {
        super(botToken);
        this.botUserName = botUserName;
        this.geocodeApiClient = geocodeApiClient;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String message = update.getMessage().getText();

            if (message.equalsIgnoreCase("/start")) {
                sendMessage(chatId, "Добро пожаловать. Введите адрес проживания");
                geocodeApiClient.deleteUserInfo(new TelegramChatIdRequest(chatId));
                messageState.put(chatId, MessageState.WAITING_HOME_ADDRESS);

            } else if (message.equalsIgnoreCase("/info")) {
                showUserInfo(chatId);
            } else {
                handleMessage(chatId, message);
            }
        }
    }

    private void handleMessage(Long chatId, String message) {
        MessageState currentState = messageState.get(chatId);


        switch (currentState) {
            case WAITING_HOME_ADDRESS -> handleHomeAddress(chatId, message);
            case WAITING_WORK_ADDRESS -> handleWorkAddress(chatId, message);
            case WAITING_WORK_TIME -> handleWorkTime(chatId, message);
            default -> sendMessage(chatId, "Что-то пошло не так, напиши /start, чтобы начать заново");
        }
    }

    private void handleHomeAddress(Long chatId, String message) {
        try {
            geocodeApiClient.resolveHomeAddress(new TelegramAddressRequest(chatId, message));

            sendMessage(chatId, "Теперь введи адрес работы:");
            messageState.put(chatId, MessageState.WAITING_WORK_ADDRESS);
        } catch (Exception e) {
            sendMessage(chatId, "Не удалось распознать адрес. Пожалуйста, введите адрес проживания еще раз:");
        }
    }

    private void handleWorkAddress(Long chatId, String message) {
        try {
            geocodeApiClient.resolveWorkAddress(new TelegramAddressRequest(chatId, message));
            sendMessage(chatId, "Введи время, когда нужно быть на работе (например, 09:00):");
            messageState.put(chatId, MessageState.WAITING_WORK_TIME);
        } catch (Exception e) {
            sendMessage(chatId, "Не удалось распознать адрес. Пожалуйста, введите адрес проживания еще раз:");
        }

    }

    private void handleWorkTime(Long chatId, String message) {

        LocalTime workTime = LocalTime.parse(message, DateTimeFormatter.ofPattern("HH:mm"));
        geocodeApiClient.saveTime(new TelegramTimeRequest(chatId, workTime));
        UserInfoDTO userInfo = geocodeApiClient.getUserInfo(new TelegramChatIdRequest(chatId));
        if (userInfo != null && userInfo.getTimezone() != null) {
            String infoMessage = """
                    Данные сохранены!
                    Пришлю уведомление за пол часа до оптимального времени
                    """;
            sendMessage(chatId, infoMessage);
        }

        messageState.put(chatId, MessageState.DONE);

    }


    private void showUserInfo(Long chatId) {
        if (messageState.get(chatId) != MessageState.DONE) {
            sendMessage(chatId, "Сначала завершите настройку, используя команду /start");
            return;
        }

        UserInfoDTO userInfo = geocodeApiClient.getUserInfo(new TelegramChatIdRequest(chatId));
        if (userInfo != null) {

            String info = String.format("""
                            Ваши текущие настройки:
                            Домашний адрес: %s
                            Рабочий адрес: %s
                            Время прибытия: %s
                            Часовой пояс: %s
                            
                            Текущее время в вашем поясе: %s""",
                    userInfo.getHomeAddress(),
                    userInfo.getWorkAddress(),
                    userInfo.getTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                    userInfo.getTimezone(),
                    ZonedDateTime.now(ZoneId.of(userInfo.getTimezone())).format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")));

            sendMessage(chatId, info);
        } else {
            sendMessage(chatId, "Не удалось получить информацию. Попробуйте позже.");
        }
    }

    private void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(message).build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}