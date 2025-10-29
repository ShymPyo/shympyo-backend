package shympyo.storage.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shympyo.rental.repository.PlaceRepository;
import shympyo.storage.dto.PresignResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class ImagePresignService {

    @Value("${ncp.object-storage.bucket}")
    private String bucket;

    @Value("${ncp.object-storage.public-base-url}")
    private String publicBaseUrl;

    private final S3Presigner presigner;
    private final PlaceRepository placeRepository;


    public PresignResponse createUserProfilePresign(Long userId, String fileExtension, String contentType) {


        String objectKey = "users/%d/profile_%d.%s".formatted(
                userId,
                System.currentTimeMillis(),
                fileExtension
        );
        return createPresignForKey(objectKey, contentType);
    }

    public PresignResponse createPlaceImagePresign(Long placeId, Long userId, String fileExtension, String contentType) {

        if (!placeRepository.existsByIdAndOwnerId(placeId, userId)) {
            throw new IllegalArgumentException("해당 장소는 이 제공자의 소유가 아닙니다.");
        }

        String objectKey = "places/%d/main_%d.%s".formatted(
                placeId,
                System.currentTimeMillis(),
                fileExtension
        );
        return createPresignForKey(objectKey, contentType);
    }

    private PresignResponse createPresignForKey(String objectKey, String contentType) {

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
        String publicUrl = "%s/%s/%s".formatted(
                publicBaseUrl,
                bucket,
                objectKey
        );

        return new PresignResponse(uploadUrl, objectKey, publicUrl);
    }

    private String buildObjectKey(String userId, String fileExtension) {
        long now = System.currentTimeMillis();
        return "users/%s/profile_%d.%s".formatted(userId, now, fileExtension);
    }
}