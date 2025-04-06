package org.itstep.transcriblyai.modules.transcribe.services;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.itstep.transcriblyai.modules.transcribe.models.TranscriptionModel;
import org.itstep.transcriblyai.modules.transcribe.repositories.TranscriptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TranscriptionService {
    private final TranscriptionRepository transcriptionRepository;
    private final SpeechToTextService speechToTextService;


    public StringBuilder generateTranscription(MultipartFile file) throws IOException {
        ByteString audioBytes = ByteString.readFrom(file.getInputStream());
        try (SpeechClient speechClient = speechToTextService.createSpeechClient()) {
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.MP3)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("en-US")
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

            RecognizeResponse response = speechClient.recognize(config, audio);
            StringBuilder transcript = new StringBuilder();

            for (SpeechRecognitionResult result : response.getResultsList()) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                transcript.append(alternative.getTranscript());
            }

            return transcript;
        }
    }

    public void saveTranscription(TranscriptionModel transcription) {
        transcriptionRepository.save(transcription);
    }

    public List<TranscriptionModel> getUserTranscriptions(Long userId) {
        return transcriptionRepository.findByUserId(userId);
    }

    public List<TranscriptionModel> getPublicTranscriptions() {
        return transcriptionRepository.findByIsPublicTrue();
    }

    public void toggleVisibility(Long id) {
        TranscriptionModel transcription = transcriptionRepository.findById(id).orElseThrow();
        transcription.setPublic(!transcription.isPublic());
        transcriptionRepository.save(transcription);
    }

    public Page<TranscriptionModel> getPublicTranscriptions(Pageable pageable) {
        return transcriptionRepository.findByIsPublicTrue(pageable);
    }

    public void deleteTranscription(Long transcriptionId) {
        TranscriptionModel transcription = transcriptionRepository.findById(transcriptionId)
                .orElseThrow(() -> new RuntimeException("Transcription not found"));
        transcriptionRepository.delete(transcription);
    }
}
