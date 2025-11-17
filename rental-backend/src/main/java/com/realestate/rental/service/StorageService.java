package com.realestate.rental.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    @Value("${app.aws.s3.access-key:dummy-key}")
    private String accessKey;

    @Value("${app.aws.s3.secret-key:dummy-secret}")
    private String secretKey;

    @Value("${app.aws.s3.region:us-east-1}")
    private String region;

    @Value("${app.aws.s3.bucket-name:rental-app-media}")
    private String bucketName;

    @Value("${app.aws.s3.endpoint:}")
    private String endpoint;

    public String uploadFile(MultipartFile file, String folder) {
        // For development without AWS, return a dummy URL
        if ("dummy-key".equals(accessKey)) {
            String fileName = folder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
            System.out.println("⚠️ AWS not configured. Would upload: " + fileName);
            return "https://placeholder.com/" + fileName;
        }

        try {
            String fileName = folder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

            S3Client s3Client = createS3Client();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Return public URL
            if (endpoint != null && !endpoint.isEmpty()) {
                return endpoint + "/" + bucketName + "/" + fileName;
            } else {
                return String.format("https://%s.s3.%s.amazonaws.com/%s",
                        bucketName, region, fileName);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    private S3Client createS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        var builder = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(region));

        // For MinIO or custom S3-compatible storage
        if (endpoint != null && !endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(endpoint));
        }

        return builder.build();
    }
}