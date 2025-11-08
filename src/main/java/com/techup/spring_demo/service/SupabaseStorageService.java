package com.techup.spring_demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@Service
public class SupabaseStorageService {

  @Value("${supabase.url}")
  private String supabaseUrl;

  @Value("${supabase.bucket}")
  private String bucket;

  @Value("${supabase.apiKey}")
  private String apiKey;

  private final WebClient webClient = WebClient.builder().build();

  /** อัปโหลดไฟล์ขึ้น Supabase แล้วคืน public URL */
  public String uploadFile(MultipartFile file) {
    String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file.bin";
    String fileName = System.currentTimeMillis() + "_" + original;
    String uploadUrl = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucket, fileName);

    log.info("Uploading file to Supabase - URL: {}, Bucket: {}, Filename: {}", supabaseUrl, bucket, fileName);

    byte[] bytes;
    try {
      bytes = file.getBytes();
      log.debug("File bytes read successfully - Size: {} bytes", bytes.length);
    } catch (IOException e) {
      log.error("Failed to read file bytes", e);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot read file bytes", e);
    }

    try {
      log.info("Sending PUT request to Supabase: {}", uploadUrl);
      webClient.put()
          .uri(uploadUrl)
          .header("Authorization", "Bearer " + apiKey)
          .header("Content-Type", file.getContentType() != null ? file.getContentType() : "application/octet-stream")
          .bodyValue(bytes)
          .retrieve()
          .onStatus(HttpStatusCode::isError, res ->
              res.bodyToMono(String.class).defaultIfEmpty("Upload failed").flatMap(msg -> {
                log.error("Supabase upload failed with error: {}", msg);
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Supabase upload failed: " + msg));
              })
          )
          .toBodilessEntity()
          .block();

      String publicUrl = String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, bucket, fileName);
      log.info("File uploaded successfully to Supabase - Public URL: {}", publicUrl);
      return publicUrl;

    } catch (ResponseStatusException ex) {
      log.error("ResponseStatusException during upload", ex);
      throw ex;
    } catch (Exception ex) {
      log.error("Unexpected error while uploading to Supabase", ex);
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unexpected error while uploading to Supabase: " + ex.getMessage(), ex);
    }
  }
}