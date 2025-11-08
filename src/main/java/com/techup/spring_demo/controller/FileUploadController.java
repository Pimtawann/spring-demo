package com.techup.spring_demo.controller;

import com.techup.spring_demo.service.SupabaseStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

  private final SupabaseStorageService supabaseStorageService;

  @PostMapping(value = "/upload", consumes = "multipart/form-data")
  public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
    log.info("Received file upload request - Filename: {}, Size: {} bytes, ContentType: {}",
        file.getOriginalFilename(), file.getSize(), file.getContentType());

    if (file.isEmpty()) {
      log.warn("Uploaded file is empty");
      return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
    }

    String url = supabaseStorageService.uploadFile(file);
    log.info("File uploaded successfully - URL: {}", url);
    return ResponseEntity.ok(Map.of("url", url));
  }
}
