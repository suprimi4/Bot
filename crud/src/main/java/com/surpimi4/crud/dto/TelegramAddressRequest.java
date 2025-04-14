package com.surpimi4.crud.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelegramAddressRequest {
    private Long chatId;
    private String address;

}
