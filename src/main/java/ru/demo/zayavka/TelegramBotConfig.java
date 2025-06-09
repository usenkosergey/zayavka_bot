package ru.demo.zayavka;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * @author Usenko Sergey
 * @since 31.05.2025
 */
@Configuration
public class TelegramBotConfig {
    private final TelegramMailBot telegramMailBot;
    private BotSession session;

    public TelegramBotConfig(TelegramMailBot telegramMailBot) {
        this.telegramMailBot = telegramMailBot;
    }

    @PostConstruct
    public void start() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        session = botsApi.registerBot(telegramMailBot);
    }

    @PreDestroy
    public void shutdown() {
        if (session != null) {
            session.stop();
        }
    }
}
