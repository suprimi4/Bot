package com.suprimi4.events;

public class AlertEvent implements GeneralEvent {
    private Long chatId;
    private String message;

    public AlertEvent() {
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AlertEvent(Long chatId, String message) {
        this.chatId = chatId;
        this.message = message;
    }
}
