package main.com.teachmeskills.final_assignment.amazons3;

import main.com.teachmeskills.final_assignment.util.PropertiesLoader;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.nio.file.Path;

import static main.com.teachmeskills.final_assignment.constant.Constants.*;

public class AWSTesterS3 {

    public static void uploadFileToAWS() {
        try {
            String accessKey = PropertiesLoader.loadProperties().getProperty("s3.accessKey");
            String secretKey = PropertiesLoader.loadProperties().getProperty("s3.secretKey");
            String bucketName = PropertiesLoader.loadProperties().getProperty("s3.bucketName");
            String regionName = PropertiesLoader.loadProperties().getProperty("s3.region");

            String key = STATISTICS_FILE_NAME;
            File file = new File(PATH_TO_STATISTICS + key);

            AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

            S3Client s3Client = S3Client
                    .builder()
                    .region(Region.of(regionName))
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            PutObjectResponse response = s3Client.putObject(request, Path.of(file.toURI()));
            System.out.println(response.eTag());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
