package org.itstep.transcriblyai.modules.transcribe.controllers;

import lombok.RequiredArgsConstructor;
import org.itstep.transcriblyai.modules.auth.models.UserModel;
import org.itstep.transcriblyai.modules.auth.services.UserService;
import org.itstep.transcriblyai.modules.transcribe.models.TranscriptionModel;
import org.itstep.transcriblyai.modules.transcribe.services.TranscriptionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TranscriptionController {

    private final TranscriptionService transcriptionService;
    private final UserService userService;

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
    public String createTranscription(@AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {
        UserModel user = userService.findByUsername(currentUser.getUsername());

        TranscriptionModel transcription = new TranscriptionModel();
        transcription.setUser(user);
        transcription.setText("Тестовая транскрипция");

        transcriptionService.saveTranscription(transcription);
        return "redirect:/dashboard";
    }

    @PostMapping("/transcription/{id}/toggle")
    public String toggleVisibility(@PathVariable Long id) {
        transcriptionService.toggleVisibility(id);
        return "redirect:/dashboard";
    }
}
