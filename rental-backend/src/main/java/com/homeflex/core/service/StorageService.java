package com.homeflex.core.service;

import com.homeflex.core.exception.DomainException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

@Slf4j
@Service
public class StorageService {

    @Value("${app.aws.s3.access-key:}")
    private String accessKey;

    @Value("${app.aws.s3.secret-key:}")
    private String secretKey;

    @Value("${app.aws.s3.region:us-east-1}")
    private String region;

    @Value("${app.aws.s3.bucket-name:rental-app-media}")
    private String bucketName;

    @Value("${app.aws.s3.endpoint:}")
    private String endpoint;

    @Value("${app.aws.enabled:false}")
    private boolean awsEnabled;

    private S3Client s3Client;

    @PostConstruct
    public void init() {
        if (awsEnabled && accessKey != null && !accessKey.isBlank()) {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

            var builder = S3Client.builder()
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .region(Region.of(region));

            if (endpoint != null && !endpoint.isEmpty()) {
                builder.endpointOverride(URI.create(endpoint));
            }

            s3Client = builder.build();
            log.info("S3 storage initialized (bucket={}, region={})", bucketName, region);
        } else {
            log.warn("AWS S3 not configured — file uploads will use local placeholder URLs");
        }
    }

    public String uploadFile(MultipartFile file, String folder) {
        String fileName = folder + "/" + UUID.randomUUID() + "-" + sanitizeFilename(file.getOriginalFilename());
        
        byte[] finalData;
        String contentType = file.getContentType();

        try {
            if (contentType != null && contentType.startsWith("image/")) {
                finalData = resizeImage(file.getInputStream(), contentType);
            } else {
                finalData = file.getBytes();
            }
        } catch (IOException e) {
            log.error("Failed to read file input stream", e);
            throw new DomainException("Failed to process file upload");
        }

        if (s3Client == null) {
            log.debug("S3 not configured. Would upload: {}", fileName);
            return "https://placeholder.local/" + fileName;
        }

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(finalData));

            String url;
            if (endpoint != null && !endpoint.isEmpty()) {
                url = endpoint + "/" + bucketName + "/" + fileName;
            } else {
                url = String.format("https://%s.s3.%s.amazonaws.com/%s",
                        bucketName, region, fileName);
            }

            log.debug("File uploaded to S3: {}", url);
            return url;

        } catch (Exception e) {
            log.error("Failed to upload file to S3: {}", fileName, e);
            throw new DomainException("File upload failed. Please try again.");
        }
    }

    public String uploadFile(byte[] data, String fileName, String contentType, String folder) {
        String fullPath = folder + "/" + UUID.randomUUID() + "-" + sanitizeFilename(fileName);

        if (s3Client == null) {
            log.debug("S3 not configured. Would upload: {}", fullPath);
            return "https://placeholder.local/" + fullPath;
        }

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fullPath)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));

            String url;
            if (endpoint != null && !endpoint.isEmpty()) {
                url = endpoint + "/" + bucketName + "/" + fullPath;
            } else {
                url = String.format("https://%s.s3.%s.amazonaws.com/%s",
                        bucketName, region, fullPath);
            }

            log.debug("File uploaded to S3: {}", url);
            return url;

        } catch (Exception e) {
            log.error("Failed to upload byte array to S3: {}", fullPath, e);
            throw new DomainException("File upload failed. Please try again.");
        }
    }

    private byte[] resizeImage(InputStream inputStream, String contentType) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputStream);
        if (originalImage == null) return new byte[0];

        // Max width 1200px, maintain aspect ratio
        BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, 1200);
        
        String formatName = contentType.substring(contentType.indexOf("/") + 1);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(resizedImage, formatName, baos);
            return baos.toByteArray();
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null) return "file";
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
