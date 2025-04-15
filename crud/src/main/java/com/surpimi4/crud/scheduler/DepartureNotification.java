package com.surpimi4.crud.scheduler;


import com.surpimi4.crud.repository.UserInfoRepository;

import com.surpimi4.crud.service.RouteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.time.format.DateTimeFormatter;


@Component
public class DepartureNotification {
    @Value("${telegram.bot.token}")
    private String botToken;
    private final UserInfoRepository userInfoRepository;
    private final RouteService routeService;
    private final RestTemplate restTemplate;
    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";

    public DepartureNotification(UserInfoRepository userInfoRepository, RouteService routeService, RestTemplate restTemplate) {
        this.userInfoRepository = userInfoRepository;
        this.routeService = routeService;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 60000)
    private void checkDepartureTime() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        if (today == DayOfWeek.SATURDAY || today == DayOfWeek.SUNDAY) {
            return;
        }

        userInfoRepository.findAll().forEach(userInfo -> {
            if (userInfo == null || userInfo.getTimezone() == null)
                return;

            ZoneId userZone = ZoneId.of(userInfo.getTimezone());
            ZonedDateTime nowInUserZone = ZonedDateTime.now(userZone);

            LocalDate userDay = nowInUserZone.toLocalDate();
            if (userInfo.getLastNotificationDate() != null
                    && userInfo.getLastNotificationDate().equals(userDay)) {
                return;
            }

            double durationSeconds = routeService.getRouteByChatId(userInfo.getId());
            LocalTime arriveTime = userInfo.getTime();
            LocalTime departureTime = arriveTime.minusSeconds((long) durationSeconds);
            LocalTime notificationTime = departureTime.minusMinutes(30);
            LocalTime currentTime = nowInUserZone.toLocalTime();
            if (currentTime.isAfter(notificationTime) && currentTime.isBefore(departureTime)) {
                String message = String.format("""
                                Напоминание о выезде!
                                Чтобы приехать к %s,
                                вам нужно выехать в %s""",
                        arriveTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        departureTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                );
                sendMessage(userInfo.getId(), message);
                userInfo.setLastNotificationDate(userDay);
                userInfoRepository.save(userInfo);
            }
        });

    }

    private void sendMessage(Long chatId, String text) {
        String url = String.format("%s%s/sendMessage?chat_id=%d&text=%s",
                TELEGRAM_API_URL,
                botToken,
                chatId,
                text
        );
        restTemplate.getForObject(url, String.class);
    }
}
