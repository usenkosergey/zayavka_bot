package ru.demo.zayavka;

import jakarta.mail.*;
import jakarta.mail.search.FlagTerm;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * @author Usenko Sergey
 * @since 31.05.2025
 */
@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.to}")
    private String to;
    @Value("${mail.imap.host}")
    private String host;
    @Value("${mail.imap.port}")
    private String port;
    @Value("${mail.imap.username}")
    private String username;
    @Value("${mail.imap.password}")
    private String password;

    private final NotificationService notificationService;

    public MailService(NotificationService notificationService, JavaMailSender mailSender) {
        this.notificationService = notificationService;
        this.mailSender = mailSender;
    }

//    @Scheduled(fixedDelay = 30000)
    @Scheduled(fixedDelay = 300000)
    public void checkMail() {
        try {
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.host", host);
            props.put("mail.imaps.port", port);
            props.put("mail.imaps.ssl.enable", "true");

            Session session = Session.getInstance(props);
            Store store = session.getStore("imaps");
            store.connect(host, username, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            for (Message message : messages) {
                String subject = message.getSubject();
                String body = "";
                Object content = message.getContent();
                if (content instanceof Multipart) {
                    body = extractFromMultipart((Multipart) content);
                } else if (content instanceof String) {
                    body = (String) content;
                }
                assert body != null;
                Boolean result = notificationService.broadcast(subject, Jsoup.parse(body).text());
                message.setFlag(Flags.Flag.SEEN, result);
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private String extractFromMultipart(Multipart multipart) throws Exception {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);
            // Если встретился вложенный multipart (например, multipart/related)
            if (part.getContent() instanceof Multipart) {
                String result = extractFromMultipart((Multipart) part.getContent());
                if (result != null) {
                    return result;
                }
            }
            // Выбираем текстовую часть или HTML
            if (part.isMimeType("text/plain")) {
                return (String) part.getContent();
            }
            if (part.isMimeType("text/html")) {
                // Можно вернуть HTML или конвертировать в текст
                return (String) part.getContent();
            }
        }
        return null;
    }

    public void sendMail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(to);
        message.setSubject("Упал бот ZayavkaWithTrafikPlusBot");
        message.setText("Упал бот ZayavkaWithTrafikPlusBot. Нужно чинить");
        mailSender.send(message);
    }
}
