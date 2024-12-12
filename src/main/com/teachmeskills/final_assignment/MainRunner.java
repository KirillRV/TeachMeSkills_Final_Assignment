package main.com.teachmeskills.final_assignment;

import main.com.teachmeskills.final_assignment.logging.Logger;
import main.com.teachmeskills.final_assignment.service.AuthenticationService;

public class MainRunner {

    public static void main(String[] args) {
        Logger.logFileInfo(1, "Program started.");

        AuthenticationService.processAuthentication();
    }
}