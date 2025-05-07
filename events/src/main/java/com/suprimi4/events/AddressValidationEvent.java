package com.suprimi4.events;


public class AddressValidationEvent {

    private Long chatId;
    private boolean valid;
    private EventType addressType;


    public AddressValidationEvent(Long chatId, boolean valid, EventType addressType) {
        this.chatId = chatId;
        this.valid = valid;
        this.addressType = addressType;
    }

    public AddressValidationEvent() {
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public EventType getAddressType() {
        return addressType;
    }

    public void setAddressType(EventType addressType) {
        this.addressType = addressType;
    }

}
