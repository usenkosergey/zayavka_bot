package ru.demo.zayavka;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.File;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // Получаем реальный путь к корню приложения (…/tomcat/webapps/myapp/)
        String appRoot = servletContext.getRealPath("/");
        // Переходим к родительской папке webapps
        File webappsDir = new File(appRoot).getParentFile();
        // Ищем файл mailbot.properties в webapps
        File external = new File(webappsDir, "mailbot.properties");

        if (external.exists() && external.isFile()) {
            // На сервере: используем внешний файл
            System.setProperty(
                    "spring.config.location",
                    "optional:file:" + external.getAbsolutePath()
            );
        }
        // иначе: при локальной разработке берётся application.properties из classpath

        // Вызов базовой логики развёртывания
        super.onStartup(servletContext);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ZayavkaApplication.class);
    }

}
