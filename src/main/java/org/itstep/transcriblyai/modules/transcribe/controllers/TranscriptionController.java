package org.itstep.transcriblyai.modules.transcribe.controllers;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.itstep.transcriblyai.modules.auth.models.UserModel;
import org.itstep.transcriblyai.modules.auth.services.UserService;
import org.itstep.transcriblyai.modules.transcribe.models.TranscriptionModel;
import org.itstep.transcriblyai.modules.transcribe.services.FileStorageService;
import org.itstep.transcriblyai.modules.transcribe.services.TranscriptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import org.springframework.mock.web.MockMultipartFile;

@Controller
@RequiredArgsConstructor
public class TranscriptionController {

    private final TranscriptionService transcriptionService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            UserModel user = (UserModel) authentication.getPrincipal();
            List<TranscriptionModel> transcriptions = transcriptionService.getUserTranscriptions(user.getId());
            model.addAttribute("transcriptions", transcriptions);
            model.addAttribute("user", user);
            return "auth/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/transcribe")
    public String transcribe() {
        return "transcribe/transcribe";
    }

    @PostMapping("/transcribe") // old root without chunks & redis
    public String createTranscription(@RequestParam("audioFile") MultipartFile file,
                                      @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            UserModel user = (UserModel) authentication.getPrincipal();

            TranscriptionModel transcription = new TranscriptionModel();
            transcription.setUser(user);
            transcription.setText(transcriptionService.generateTranscription(file).toString());
            transcription.setFilename(fileStorageService.storeFile(file));
            transcription.setPublic(false);

            transcriptionService.saveTranscription(transcription);

            return "redirect:/dashboard";
        }

        return "redirect:/login";
    }


    @PostMapping("/transcribe/chunk")
    public ResponseEntity<?> handleChunkUpload(HttpServletRequest request,
                                               @RequestHeader("X-Chunk-Index") int chunkIndex,
                                               @RequestHeader("X-Total-Chunks") int totalChunks,
                                               @RequestHeader("X-File-Name") String fileName,
                                               @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        UserModel user = (UserModel) authentication.getPrincipal();
        String tempDir = System.getProperty("java.io.tmpdir") + "/chunked_uploads";
        Files.createDirectories(Paths.get(tempDir));
        Path tempFile = Paths.get(tempDir, fileName + ".part" + chunkIndex);

        try (InputStream inputStream = request.getInputStream();
             OutputStream outputStream = Files.newOutputStream(tempFile, StandardOpenOption.CREATE)) {
            StreamUtils.copy(inputStream, outputStream);
        }

        if (chunkIndex == totalChunks - 1) {
            String combinedFileName = UUID.randomUUID() + "-" + fileName;
            Path combinedPath = Paths.get(tempDir, combinedFileName);

            try (OutputStream mergedStream = Files.newOutputStream(combinedPath, StandardOpenOption.CREATE)) {
                for (int i = 0; i < totalChunks; i++) {
                    Path partPath = Paths.get(tempDir, fileName + ".part" + i);
                    Files.copy(partPath, mergedStream);
                    Files.delete(partPath);
                }
            }

            MultipartFile multipartFile = new MockMultipartFile(
                    fileName,
                    fileName,
                    "audio/mpeg",
                    Files.readAllBytes(combinedPath)
            );

            TranscriptionModel transcription = new TranscriptionModel();
            transcription.setUser(user);
            transcription.setText(transcriptionService.generateTranscription(multipartFile).toString());
            transcription.setFilename(fileStorageService.storeFile(multipartFile));
            transcription.setPublic(false);
            transcriptionService.saveTranscription(transcription);

            Files.deleteIfExists(combinedPath);
        }

        return ResponseEntity.ok("Chunk " + chunkIndex + " uploaded successfully.");
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

    @PostMapping("/transcription/{id}/delete")
    public String deleteTranscription(@PathVariable Long id, Principal principal) {
        TranscriptionModel transcription = transcriptionService.findById(id);
        if(transcription.getUser().getUsername().equals(principal.getName())) {
            transcriptionService.deleteTranscription(id);
        }
        return "redirect:/dashboard";
    }
}
