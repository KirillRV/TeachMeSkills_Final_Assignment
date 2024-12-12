package main.com.teachmeskills.final_assignment.amazons3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class S3ConfigLoader {
    private final Properties properties = new Properties();


    public S3ConfigLoader(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Configuration file not found: " + filePath);
        }
        try (FileInputStream input = new FileInputStream(file)) {
            properties.load(input);
        }
    }

    public String getAccessKey() {
        return properties.getProperty("s3.accessKey", "default-access-key");
    }

    public String getSecretKey() {
        return properties.getProperty("s3.secretKey", "default-secret-key");
    }

    public String getBucketName() {
        return properties.getProperty("s3.bucketName", "default-bucket");
    }

    public String getRegion() {
        return properties.getProperty("s3.region", "us-east-1");
    }

    public static void main(String[] args) {
        try {
            // Укажите полный или относительный путь к файлу config.properties
            String configPath = "config.properties";
            S3ConfigLoader configLoader = new S3ConfigLoader(configPath);

            System.out.println("Access Key: " + configLoader.getAccessKey());
            System.out.println("Secret Key: " + configLoader.getSecretKey());
            System.out.println("Bucket Name: " + configLoader.getBucketName());
            System.out.println("Region: " + configLoader.getRegion());
        } catch (IOException e) {
            System.err.println("Error loading S3 configuration: " + e.getMessage());
        }
    }
}

