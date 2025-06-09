package ru.demo.zayavka;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Usenko Sergey
 * @since 31.05.2025
 */
@Service
public class UserRegistryService implements ServletContextAware {

    private ServletContext servletContext;
    private final Set<Long> users = new HashSet<>();
    private static Path USER_FILE_PATH;

    @PostConstruct
    public void init() {
        getPathUserFiles();
        loadUsersFromFile();
    }

    private void getPathUserFiles() {
        String appRoot = servletContext.getRealPath("/");
        Path appPath = Paths.get(appRoot);
        Path webappsPath = appPath.getParent();
        USER_FILE_PATH = webappsPath.resolve("mailbot_users.txt");
    }

    public synchronized void registerUser(Long chatId) {
        if (users.add(chatId)) {
            saveUserToFile(chatId);
        }
    }

    public synchronized Set<Long> getAllUsers() {
        return new HashSet<>(users);
    }

    private void saveUserToFile(Long chatId) {
        try {
            Files.createDirectories(USER_FILE_PATH.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(USER_FILE_PATH, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(chatId.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUsersFromFile() {
        if (Files.exists(USER_FILE_PATH)) {
            try (BufferedReader reader = Files.newBufferedReader(USER_FILE_PATH)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        users.add(Long.parseLong(line.trim()));
                    } catch (NumberFormatException ignore) {
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
