package com.suprimi4.events;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class EventWrapper {
    EventType eventType;
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "eventType"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = AddressEvent.class, name = "HOME_ADDRESS"),
            @JsonSubTypes.Type(value = AddressEvent.class, name = "WORK_ADDRESS"),
            @JsonSubTypes.Type(value = TimeEvent.class, name = "TIME"),
            @JsonSubTypes.Type(value = UserEvent.class, name = "DELETE_USER"),
            @JsonSubTypes.Type(value = AlertEvent.class, name = "ALERT")
    })
    GeneralEvent event;


    public EventWrapper(EventType eventType, GeneralEvent event) {
        this.eventType = eventType;
        this.event = event;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventWrapper() {
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Object getEvent() {
        return event;
    }

    public void setEvent(GeneralEvent event) {
        this.event = event;
    }

}
