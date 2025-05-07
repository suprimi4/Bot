package com.suprimi4.events;

public class AddressEvent implements GeneralEvent {
    private Long chatId;
    private String address;

    public AddressEvent(Long chatId, String address) {
        this.chatId = chatId;
        this.address = address;
    }

    public AddressEvent() {

    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
