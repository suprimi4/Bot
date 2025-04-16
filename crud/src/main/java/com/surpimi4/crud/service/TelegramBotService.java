package com.surpimi4.crud.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramBotService {

    @Value("${telegram.bot.token}")
    private String botToken;
    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";
    private final RestTemplate restTemplate;

    public TelegramBotService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendMessage(Long chatId, String text) {
        String url = String.format("%s%s/sendMessage?chat_id=%d&text=%s",
                TELEGRAM_API_URL,
                botToken,
                chatId,
                text
        );
        restTemplate.getForObject(url, String.class);
    }
}
