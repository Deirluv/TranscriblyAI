package org.itstep.transcriblyai.modules.transcribe.controllers;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.itstep.transcriblyai.modules.auth.models.UserModel;
import org.itstep.transcriblyai.modules.auth.services.UserService;
import org.itstep.transcriblyai.modules.transcribe.models.TranscriptionModel;
import org.itstep.transcriblyai.modules.transcribe.services.FileStorageService;
import org.itstep.transcriblyai.modules.transcribe.services.TranscriptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TranscriptionController {

    private final TranscriptionService transcriptionService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser, Model model) {
        UserModel user = userService.findByUsername(currentUser.getUsername());
        List<TranscriptionModel> transcriptions = transcriptionService.getUserTranscriptions(user.getId());
        model.addAttribute("transcriptions", transcriptions);
        return "dashboard";
    }

    @GetMapping("/transcribe")
    public String transcribe() {
        return "transcribe/transcribe";
    }

    @PostMapping("/transcribe")
    public String createTranscription(@RequestParam("audioFile") MultipartFile file,
                                      @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) throws Exception {

        UserModel user = userService.findByUsername(currentUser.getUsername());

        TranscriptionModel transcription = new TranscriptionModel();
        transcription.setUser(user);
        transcription.setText(transcriptionService.generateTranscription(file).toString());
        transcription.setFilename(fileStorageService.storeFile(file));
        transcription.setPublic(false);

        transcriptionService.saveTranscription(transcription);

        return "redirect:/dashboard";
    }

    @PostMapping("/transcription/{id}/toggle")
    public String toggleVisibility(@PathVariable Long id) {
        transcriptionService.toggleVisibility(id);
        return "redirect:/dashboard";
    }

    @GetMapping("/transcriptions")
    public String publicTranscriptions(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10);  // По 10 элементов на странице
        Page<TranscriptionModel> transcriptionsPage = transcriptionService.getPublicTranscriptions(pageable);

        model.addAttribute("transcriptions", transcriptionsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transcriptionsPage.getTotalPages());

        return "transcribe/transcriptions";
    }
}
