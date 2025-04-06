package org.itstep.transcriblyai.modules.transcribe.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SpeechToTextService {
    public SpeechClient createSpeechClient() throws IOException {

        String keyPath = "D:\\vscode_source\\Exams\\TranscriblyAI\\google-credentials\\speech-key.json";
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(keyPath));

        SpeechSettings settings = SpeechSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

        return SpeechClient.create(settings);
    }
}
