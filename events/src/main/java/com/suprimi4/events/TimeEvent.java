package com.suprimi4.events;

import java.time.LocalTime;

public class TimeEvent implements GeneralEvent  {
    private Long chatId;
    private LocalTime time;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public TimeEvent() {
    }

    public TimeEvent(Long chatId, LocalTime time) {
        this.chatId = chatId;
        this.time = time;
    }
}
