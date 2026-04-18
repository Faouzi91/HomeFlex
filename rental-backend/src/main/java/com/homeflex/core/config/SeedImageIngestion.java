package com.homeflex.core.config;

import com.homeflex.core.service.StorageService;
import com.homeflex.features.property.domain.entity.PropertyImage;
import com.homeflex.features.property.domain.repository.PropertyImageRepository;
import com.homeflex.features.vehicle.domain.entity.VehicleImage;
import com.homeflex.features.vehicle.domain.repository.VehicleImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Runs after SampleDataInitializer. Downloads any externally-hosted seed image
 * (Unsplash, Picsum, etc.), re-uploads the bytes to MinIO/S3, and replaces the
 * DB URL so that all images served by the API come from our own object storage.
 * No-op when S3 is disabled or images have already been rehosted.
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
@Order(3)
public class SeedImageIngestion implements CommandLineRunner {

    private final PropertyImageRepository propertyImageRepository;
    private final VehicleImageRepository vehicleImageRepository;
    private final StorageService storageService;

    @Value("${app.aws.enabled:false}")
    private boolean awsEnabled;

    @Value("${app.aws.s3.endpoint:}")
    private String endpoint;

    @Value("${app.aws.s3.bucket-name:rental-app-media}")
    private String bucketName;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @Override
    @Transactional
    public void run(String... args) {
        if (!awsEnabled) {
            log.info("Seed image ingestion skipped (app.aws.enabled=false)");
            return;
        }
        log.info("Seed image ingestion starting…");
        int props = rehostPropertyImages();
        int vehs = rehostVehicleImages();
        log.info("Seed image ingestion complete: {} property + {} vehicle images rehosted", props, vehs);
    }

    private int rehostPropertyImages() {
        int count = 0;
        for (PropertyImage img : propertyImageRepository.findAll()) {
            String newUrl = rehost(img.getImageUrl(), "properties");
            if (newUrl != null) {
                img.setImageUrl(newUrl);
                propertyImageRepository.save(img);
                count++;
            }
        }
        return count;
    }

    private int rehostVehicleImages() {
        int count = 0;
        for (VehicleImage img : vehicleImageRepository.findAll()) {
            String newUrl = rehost(img.getImageUrl(), "vehicles");
            if (newUrl != null) {
                img.setImageUrl(newUrl);
                vehicleImageRepository.save(img);
                count++;
            }
        }
        return count;
    }

    private String rehost(String url, String folder) {
        if (url == null || url.isBlank()) return null;
        if (isAlreadyHosted(url)) return null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();
            HttpResponse<byte[]> response = http.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                log.warn("Skip rehost (HTTP {}): {}", response.statusCode(), url);
                return null;
            }
            byte[] data = response.body();
            if (data.length == 0) {
                log.warn("Skip rehost (empty body): {}", url);
                return null;
            }
            String contentType = response.headers().firstValue("Content-Type").orElse("image/jpeg");
            String fileName = deriveFileName(url, contentType);
            String newUrl = storageService.uploadFile(data, fileName, contentType, folder);
            log.debug("Rehosted {} -> {}", url, newUrl);
            return newUrl;
        } catch (Exception e) {
            log.warn("Failed to rehost {}: {}", url, e.getMessage());
            return null;
        }
    }

    private boolean isAlreadyHosted(String url) {
        if (endpoint != null && !endpoint.isBlank() && url.startsWith(endpoint)) return true;
        return url.contains("/" + bucketName + "/");
    }

    private String deriveFileName(String url, String contentType) {
        String ext = contentType.contains("png") ? ".png"
                : contentType.contains("webp") ? ".webp"
                : ".jpg";
        int slash = url.lastIndexOf('/');
        int q = url.indexOf('?', slash);
        String base = slash >= 0
                ? url.substring(slash + 1, q > 0 ? q : url.length())
                : "image";
        if (base.isBlank()) base = "image";
        return base + ext;
    }
}
