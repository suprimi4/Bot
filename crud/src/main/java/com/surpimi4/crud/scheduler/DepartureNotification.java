package com.surpimi4.crud.scheduler;


import com.surpimi4.crud.model.UserInfo;
import com.surpimi4.crud.repository.UserInfoRepository;
import com.surpimi4.crud.service.RouteService;
import com.surpimi4.crud.service.TelegramBotService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.time.*;
import java.time.format.DateTimeFormatter;


@Component
public class DepartureNotification {

    private final UserInfoRepository userInfoRepository;
    private final RouteService routeService;
    private final TelegramBotService telegramBotService;


    public DepartureNotification(UserInfoRepository userInfoRepository, RouteService routeService, TelegramBotService telegramBotService) {
        this.userInfoRepository = userInfoRepository;
        this.routeService = routeService;
        this.telegramBotService = telegramBotService;
    }

    @Scheduled(fixedRate = 60000)
    private void checkDepartureTime() {
        if (isWeekend()) {
            return;
        }
        userInfoRepository.findAll().forEach(this::notifyUsers);

    }

    private void notifyUsers(UserInfo userInfo) {
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
        if (isTimeToSendNotification(currentTime, notificationTime, departureTime)) {
            sendNotification(userInfo, userDay, arriveTime, departureTime);
        }
    }

    private boolean isTimeToSendNotification(LocalTime currentTime,
                                             LocalTime notificationTime,
                                             LocalTime departureTime) {
        return currentTime.isAfter(notificationTime) && currentTime.isBefore(departureTime);
    }

    private void sendNotification(UserInfo userInfo,
                                  LocalDate userDay,
                                  LocalTime arriveTime,
                                  LocalTime departureTime) {
        String message = String.format("""
                        Напоминание о выезде!
                        Чтобы приехать к %s,
                        вам нужно выехать в %s""",
                arriveTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                departureTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        );

        telegramBotService.sendMessage(userInfo.getId(), message);
        userInfo.setLastNotificationDate(userDay);
        userInfoRepository.save(userInfo);
    }

    private boolean isWeekend() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        return today == DayOfWeek.SATURDAY || today == DayOfWeek.SUNDAY;
    }

}
