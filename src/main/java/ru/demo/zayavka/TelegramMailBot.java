package ru.demo.zayavka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * TODO Class Description
 *
 * @author Usenko Sergey
 * @since 31.05.2025
 */
@Component
public class TelegramMailBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    private final UserRegistryService userRegistryService;

    public TelegramMailBot(UserRegistryService userRegistryService) {
        this.userRegistryService = userRegistryService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();
            userRegistryService.registerUser(chatId);

            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText("Вы подписались на уведомления.");
            try {
                execute(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

}
