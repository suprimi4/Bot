package com.surpimi4.crud.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class TelegramTimeRequest {
    private Long chatId;
    private LocalTime time;

}
