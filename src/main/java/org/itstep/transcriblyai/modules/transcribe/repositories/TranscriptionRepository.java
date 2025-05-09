package org.itstep.transcriblyai.modules.transcribe.repositories;

import org.itstep.transcriblyai.modules.transcribe.models.TranscriptionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranscriptionRepository extends JpaRepository<TranscriptionModel, Long> {
    List<TranscriptionModel> findByUserId(Long userId);
    List<TranscriptionModel> findByIsPublicTrue();
    Page<TranscriptionModel> findByIsPublicTrue(Pageable pageable);
}
