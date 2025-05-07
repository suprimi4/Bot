package com.suprimi4.events;

public class UserEvent implements GeneralEvent  {
    private Long chatId;

    public UserEvent() {
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public UserEvent(Long chatId) {
        this.chatId = chatId;
    }
}
