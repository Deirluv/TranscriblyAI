package org.itstep.transcriblyai.modules.transcribe.services;

import lombok.RequiredArgsConstructor;
import org.itstep.transcriblyai.modules.auth.models.UserModel;
import org.itstep.transcriblyai.modules.transcribe.models.TranscriptionModel;
import org.itstep.transcriblyai.modules.transcribe.repositories.TranscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TranscriptionService {
    private final TranscriptionRepository transcriptionRepository;

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
}
