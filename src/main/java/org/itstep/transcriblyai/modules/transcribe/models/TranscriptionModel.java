package org.itstep.transcriblyai.modules.transcribe.models;

import jakarta.persistence.*;
import lombok.Data;
import org.itstep.transcriblyai.modules.auth.models.UserModel;

@Entity
@Table(name = "transcriptions")
@Data
public class TranscriptionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    private String filename;
    private String text;
    private boolean isPublic;
}
