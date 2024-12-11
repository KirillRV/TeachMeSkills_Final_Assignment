package main.com.teachmeskills.final_assignment.amazons3;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.nio.file.Path;

import static main.com.teachmeskills.final_assignment.constant.Constants.PATH_TO_STATISTICS;

public class AWSTesterS3 {
 // TODO Vlad - сделать метод не main и вызвать его в processFolder методе после getFiles, также в проперти файл перенести все ключи и тд
    public static void main(String[] args) {
        String accessKey = "AKIAWQUOZ3GRVBUIJUIZ";
        String secretKey = "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";
        String bucketName = "reports-tms";
        String regionName = "eu-north-1";

        // TODO в кавычки вставить название файла с расширением
        String key = "total_statistics.txt";
        // TODO полный путь к файлу
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

        PutObjectResponse response = s3Client.putObject(request, Path.of(file.toURI()) );
        System.out.println(response.eTag());
    }

}
