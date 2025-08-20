package com.example.Khebra.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ModelRetrainScheduler {

    //@Scheduled(fixedRate = 3 * 60 * 1000)
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void retrainModel() {
        try {
            System.out.println("Réentraînement du modèle...");
            ProcessBuilder pb = new ProcessBuilder("python", "src/main/java/com/example/Khebra/ML/train_model.py");
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
            System.out.println("Réentraînement terminé !");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
