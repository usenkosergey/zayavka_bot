package ru.demo.zayavka;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ZayavkaApplication {
    public static void main(String[] args) {
        new ServletInitializer()
                .configure(new SpringApplicationBuilder(ServletInitializer.class))
                .run(args);
    }
}
