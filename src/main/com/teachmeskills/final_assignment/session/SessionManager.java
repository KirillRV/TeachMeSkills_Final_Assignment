package main.com.teachmeskills.final_assignment.session;

import main.com.teachmeskills.final_assignment.model.session.Session;
import main.com.teachmeskills.final_assignment.util.PropertiesLoader;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final Map<String, Session> activeSessions = new HashMap<>();
    private static final int DEFAULT_SESSION_DURATION = Integer.parseInt(PropertiesLoader.loadProperties().getProperty("session.duration", "1"));

    public static void createSession(String username, String secretKey) {
        createSession(username, secretKey, DEFAULT_SESSION_DURATION);
    }

    public static void createSession(String username, String secretKey, int sessionDurationMinutes) {
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(sessionDurationMinutes);
        activeSessions.put(username, new Session(username, secretKey, expiryTime));
    }

    public static void createSession(String username, String secretKey, LocalDateTime expiryTime) {
        activeSessions.put(username, new Session(username, secretKey, expiryTime));
    }

    public static boolean isSessionActive(String username) {
        Session session = activeSessions.get(username);
        return session != null && !session.isExpired();
    }

    public static void endSession(String username) {
        activeSessions.remove(username);
    }

    public static Session getSession(String username) {
        return activeSessions.get(username);
    }

    public static void removeSession(String username) {
        activeSessions.remove(username);
    }
}

