package main.com.teachmeskills.final_assignment.amazons3;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class S3ConfigLoader {
    private final Properties properties = new Properties();

    public S3ConfigLoader(String filePath) throws IOException {
        try (FileInputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        }
    }

    public String getAccessKey() {
        return properties.getProperty("s3.accessKey", "AKIAWQUOZ3GRVBUIJUIZ");
    }

    public String getSecretKey() {
        return properties.getProperty("s3.secretKey", "YMa0j9+282BqJFmTXlAZx7e3FXzb0cGJ/FAiRJuj");
    }

    public String getBucketName() {
        return properties.getProperty("s3.bucketName", "default-bucket");
    }

    public String getRegion() {
        return properties.getProperty("s3.region", "us-east-1");
    }
}

