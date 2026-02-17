package com.example.afternote.domain.image.service;

import com.example.afternote.domain.image.dto.PresignedUrlResponse;
import com.example.afternote.global.exception.CustomException;
import com.example.afternote.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.region}")
    private String region;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final Set<String> ALLOWED_DIRECTORIES = Set.of("profiles", "timeletters", "afternotes", "mindrecords");
    private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofMinutes(10);
    private static final Duration GET_PRESIGNED_URL_EXPIRATION = Duration.ofHours(1);

    public PresignedUrlResponse generatePresignedUrl(String directory, String extension) {
        String normalizedDir = directory.toLowerCase();
        String normalizedExt = extension.toLowerCase().replaceFirst("^\\.", "");

        validateDirectory(normalizedDir);
        validateExtension(normalizedExt);

        String fileName = UUID.randomUUID() + "." + normalizedExt;
        String key = normalizedDir + "/" + fileName;

        String contentType = getContentType(normalizedExt);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(PRESIGNED_URL_EXPIRATION)
                .putObjectRequest(putObjectRequest)
                .build();

        try {
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            String presignedUrl = presignedRequest.url().toString();
            String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);

            return PresignedUrlResponse.builder()
                    .presignedUrl(presignedUrl)
                    .imageUrl(imageUrl)
                    .contentType(contentType)
                    .build();
        } catch (Exception e) {
            log.error("Presigned URL generation failed for key: {}", key, e);
            throw new CustomException(ErrorCode.PRESIGNED_URL_GENERATION_FAILED);
        }
    }

    public String generateGetPresignedUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return null;
        }

        String key = extractKeyFromUrl(rawUrl);
        if (key == null) {
            return rawUrl;
        }

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(GET_PRESIGNED_URL_EXPIRATION)
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (Exception e) {
            log.error("GET Presigned URL generation failed for rawUrl: {}", rawUrl, e);
            throw new CustomException(ErrorCode.PRESIGNED_URL_GENERATION_FAILED);
        }
    }

    private String extractKeyFromUrl(String rawUrl) {
        String prefix = String.format("https://%s.s3.%s.amazonaws.com/", bucket, region);
        if (rawUrl.startsWith(prefix)) {
            return rawUrl.substring(prefix.length());
        }
        return null;
    }

    private void validateExtension(String extension) {
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new CustomException(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    private void validateDirectory(String directory) {
        if (!ALLOWED_DIRECTORIES.contains(directory)) {
            throw new CustomException(ErrorCode.INVALID_DIRECTORY);
        }
    }

    private String getContentType(String extension) {
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> throw new CustomException(ErrorCode.INVALID_FILE_EXTENSION);
        };
    }
}
