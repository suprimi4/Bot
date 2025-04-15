package suprimi4.timetogobot.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import suprimi4.timetogobot.api.GeocodeApiClient;
import suprimi4.timetogobot.api.RouteApiClient;
import suprimi4.timetogobot.dto.*;
import suprimi4.timetogobot.model.MessageState;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class TimesToGoBot extends TelegramLongPollingBot {
    private final String botUserName;

    private final Map<Long, UserData> userData = new HashMap<>();
    private final Map<Long, MessageState> messageState = new HashMap<>();
    private final GeocodeApiClient geocodeApiClient;
    private final RouteApiClient routeApiClient;

    public TimesToGoBot(@Value("${bot.username}") String botUserName,
                        @Value("${bot.token}") String botToken,
                        GeocodeApiClient geocodeApiClient,
                        RouteApiClient routeApiClient
    ) {
        super(botToken);
        this.botUserName = botUserName;
        this.geocodeApiClient = geocodeApiClient;
        this.routeApiClient = routeApiClient;
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
        UserData currentUser = userData.getOrDefault(chatId, new UserData());

        switch (currentState) {
            case WAITING_HOME_ADDRESS -> handleHomeAddress(chatId, message, currentUser);
            case WAITING_WORK_ADDRESS -> handleWorkAddress(chatId, message, currentUser);
            case WAITING_WORK_TIME -> handleWorkTime(chatId, message, currentUser);
            default -> sendMessage(chatId, "Что-то пошло не так, напиши /start, чтобы начать заново");
        }
    }

    private void handleHomeAddress(Long chatId, String message, UserData currentUser) {
        try {
            geocodeApiClient.resolveHomeAddress(new TelegramAddressRequest(chatId, message));

            currentUser.setHomeAddress(message);
            userData.put(chatId, currentUser);
            sendMessage(chatId, "Теперь введи адрес работы:");
            messageState.put(chatId, MessageState.WAITING_WORK_ADDRESS);
        } catch (Exception e) {
            sendMessage(chatId, "Не удалось распознать адрес. Пожалуйста, введите адрес проживания еще раз:");
        }
    }

    private void handleWorkAddress(Long chatId, String message, UserData currentUser) {
        try {
            geocodeApiClient.resolveWorkAddress(new TelegramAddressRequest(chatId, message));
            currentUser.setWorkAddress(message);
            userData.put(chatId, currentUser);
            sendMessage(chatId, "Введи время, когда нужно быть на работе (например, 09:00):");
            messageState.put(chatId, MessageState.WAITING_WORK_TIME);
        } catch (Exception e) {
            sendMessage(chatId, "Не удалось распознать адрес. Пожалуйста, введите адрес проживания еще раз:");
        }

    }

    private void handleWorkTime(Long chatId, String message, UserData currentUser) {

            LocalTime workTime = LocalTime.parse(message, DateTimeFormatter.ofPattern("HH:mm"));
            geocodeApiClient.saveTime(new TelegramTimeRequest(chatId, workTime));
            double travelDuration = routeApiClient.getRouteDuration(new TelegramChatIdRequest(chatId));
            currentUser.setArriveTime(workTime);
            currentUser.setDurationTime(travelDuration);
            currentUser.setLastNotificationDate(null);
            userData.put(chatId, currentUser);


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
            UserData localData = userData.get(chatId);

            String info = String.format("""
                            Ваши текущие настройки:
                            Домашний адрес: %s
                            Рабочий адрес: %s
                            Время прибытия: %s
                            Часовой пояс: %s
                            
                            Текущее время в вашем поясе: %s""",
                    userInfo.getHomeAddress(),
                    userInfo.getWorkAddress(),
                    localData.getArriveTime().format(DateTimeFormatter.ofPattern("HH:mm")),
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