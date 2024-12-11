package main.com.teachmeskills.final_assignment.session;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private Map<String, Session> activeSessions = new HashMap<>();
    private int sessionDurationMinutes;

    public SessionManager(int sessionDurationMinutes) {
        this.sessionDurationMinutes = sessionDurationMinutes;
    }

    public void createSession(String username) {
        activeSessions.put(username, new Session(username, sessionDurationMinutes));
    }

    public void createSession(String username, LocalDateTime expiryTime) {
        activeSessions.put(username, new Session(username, expiryTime));
    }

    public boolean isSessionActive(String username) {
        Session session = activeSessions.get(username);
        return session != null && !session.isExpired();
    }

    public void endSession(String username) {
        activeSessions.remove(username);
    }

    public LocalDateTime getSessionExpiry(String username) {
        Session session = activeSessions.get(username);
        return session != null ? session.getExpiryTime() : null;
    }
}