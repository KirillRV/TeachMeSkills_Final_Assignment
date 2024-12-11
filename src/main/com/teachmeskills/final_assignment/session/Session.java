package main.com.teachmeskills.final_assignment.session;

import java.time.LocalDateTime;

public class Session {
    private String username;
    private LocalDateTime expiryTime;

    public Session(String username, LocalDateTime expiryTime) {
        this.username = username;
        this.expiryTime = expiryTime;
    }

    public Session(String username, int sessionDurationMinutes) {
        this.username = username;
        this.expiryTime = LocalDateTime.now().plusMinutes(sessionDurationMinutes);
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
}