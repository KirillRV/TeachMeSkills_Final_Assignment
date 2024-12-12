package main.com.teachmeskills.final_assignment.model.session;

import main.com.teachmeskills.final_assignment.util.PropertiesLoader;

import java.time.LocalDateTime;

public class Session {
    private String username;
    private LocalDateTime expiryTime;
    private String secretKey;

    public Session(String username, String secretKey, LocalDateTime expiryTime) {
        this.username = username;
        this.secretKey = secretKey;
        this.expiryTime = expiryTime;
    }

    public Session(String username, String secretKey) {
        this(username, secretKey, LocalDateTime.now().plusMinutes(
                Integer.parseInt(PropertiesLoader.loadProperties().getProperty("session.duration", "1"))
        ));
    }

    public Session(String username) {
        this(username, null, LocalDateTime.now().plusMinutes(
                Integer.parseInt(PropertiesLoader.loadProperties().getProperty("session.duration", "1"))
        ));
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
