package ru.demo.zayavka;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * @author Usenko Sergey
 * @since 31.05.2025
 */
@Service
public class NotificationService {

    private final TelegramMailBot bot;
    private final UserRegistryService userRegistryService;

    public NotificationService(TelegramMailBot bot, UserRegistryService userRegistryService) {
        this.bot = bot;
        this.userRegistryService = userRegistryService;
    }

    public Boolean broadcast(String subject, String body) {
        int count = 0;
        for (Long chatId : userRegistryService.getAllUsers()) {
            try {
                SendMessage msg = SendMessage.builder()
                        .chatId(chatId.toString())
                        .text("📧 Письмо:\n\nТема:\n" + subject + "\n\nТекст письма:\n" + body)
                        .build();
                bot.execute(msg);
                count++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count > 0;
    }
}
