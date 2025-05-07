package suprimi4.timetogobot.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import suprimi4.timetogobot.bot.TimesToGoBot;
import suprimi4.timetogobot.model.MessageState;

@Service
public class TelegramService {

    private final TimesToGoBot timesToGoBot;

    public TelegramService(@Lazy TimesToGoBot timesToGoBot) {
        this.timesToGoBot = timesToGoBot;
    }

    public void sendMessage(Long chatId, String text) {
        timesToGoBot.sendMessage(chatId, text);
    }

    public void setMessageState(Long chatId, MessageState state) {
        timesToGoBot.setMessageState(chatId,state);
    }
}