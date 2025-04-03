package org.itstep.transcriblyai.modules.transcribe.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    @Value("${storage.upload_dir}")
    private String uploadDir;

    @Value("${storage.private}")
    private String privateStorage;

    @Value("${storage.public}")
    private String publicStorage;

    public String storeFile(MultipartFile file) throws IOException {

        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        Path targetLocation = Paths.get(publicStorage, filename);

        Files.createDirectories(targetLocation.getParent());

        Files.copy(file.getInputStream(), targetLocation);

        return filename;
    }
}
