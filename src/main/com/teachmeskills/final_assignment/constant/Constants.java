package main.com.teachmeskills.final_assignment.constant;

import java.util.List;

public interface Constants {

    String REGEX_EXP_SUM = "\\d+(?:\\.\\d+)?";
    String PATH_TO_STATISTICS = "src/main/resources/statistics/";
    String ERROR_LOGGER_FILE_PATH = "src/main/resources/logs/";
    String INFO_LOGGER_FILE_PATH = "src/main/resources/logs/";
    String INVALID_FILES_FOLDER = "invalid_files";
    List<String> ALLOWED_DIRECTORIES = List.of("checks", "orders", "invoices");
    String FILE_YEAR_TO_PARSE = "2024";
    String FILE_FORMAT = "txt";
    String SESSION_FILE = "src/main/resources/session_data.txt";
    String PROPERTIES_FILE_PATH = "src/main/resources/config.properties";
    String STATISTICS_FILE_NAME = "total_statistics.txt";
    String QR_CODE_FILE_PATH = "src/main/resources/qr_code.png";
}
