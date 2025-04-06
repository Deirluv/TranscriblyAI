package org.itstep.transcriblyai.modules.transcribe.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${storage.upload_dir}")
    private String uploadDir;

    @Value("${storage.private}")
    private String privateStorage;

    @Value("${storage.public}")
    private String publicStorage;

    public String storeFile(MultipartFile file) throws IOException {

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";

        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }

        String generatedFilename = UUID.randomUUID().toString() + extension;

        Path targetLocation = Paths.get(publicStorage).resolve(generatedFilename);

        Files.createDirectories(targetLocation.getParent());

        Files.copy(file.getInputStream(), targetLocation);

        return generatedFilename;
    }
}
