package ru.demo.zayavka;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class ShutdownNotifier {

	private final MailService mailService;

	public ShutdownNotifier(MailService mailService) {
		this.mailService = mailService;
	}

	@PreDestroy
	public void notifyShutdown() {
		try {
			mailService.sendMail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


