package com.realestate.rental.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@ConditionalOnProperty(name = "app.firebase.enabled", havingValue = "true", matchIfMissing = false)
public class FirebaseConfig {

    @Value("${app.firebase.credentials-path}")
    private String credentialsPath;

    @PostConstruct
    public void initialize() {
        if (credentialsPath == null || credentialsPath.isBlank()) {
            System.err.println("Firebase credentials path is empty. Skipping Firebase initialization.");
            return;
        }
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                try (FileInputStream serviceAccount = new FileInputStream(credentialsPath)) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();

                    FirebaseApp.initializeApp(options);
                    System.out.println("Firebase initialized successfully");
                }
            }
        } catch (IOException e) {
            System.err.println("Firebase initialization failed: " + e.getMessage());
            System.err.println("Push notifications will not work. Get firebase-adminsdk.json from Firebase Console.");
        }
    }
}