package shympyo.user.service;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shympyo.user.dto.PresignRequest;
import shympyo.user.dto.PresignResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class ProfileImageService {

    @Value("${ncp.object-storage.bucket}")
    private String bucket;

    @Value("${ncp.object-storage.public-base-url}")
    private String publicBaseUrl;

    private final S3Presigner presigner;

    public ProfileImageService(S3Presigner presigner) {
        this.presigner = presigner;
    }

    public PresignResponse createPresignedUpload(Long userId, String fileExtension, String contentType) {

        String objectKey = "users/%d/profile_%d.%s".formatted(
                userId,
                System.currentTimeMillis(),
                fileExtension
        );

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .acl("public-read")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);

        String uploadUrl = presigned.url().toString();
        String publicUrl = "%s/%s/%s".formatted(publicBaseUrl, bucket, objectKey);

        return new PresignResponse(uploadUrl, objectKey, publicUrl);
    }


    private String buildObjectKey(String userId, String fileExtension) {
        long now = System.currentTimeMillis();
        return "users/%s/profile_%d.%s".formatted(userId, now, fileExtension);
    }
}