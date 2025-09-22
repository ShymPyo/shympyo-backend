package shympyo.rental.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import shympyo.rental.domain.Place;
import shympyo.rental.dto.PlaceCreateRequest;
import shympyo.rental.dto.PlaceResponse;
import shympyo.rental.dto.PlaceUpdateRequest;
import shympyo.rental.repository.PlaceRepository;
import shympyo.user.domain.User;
import shympyo.user.domain.UserRole;
import shympyo.user.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private static final int CODE_MAX_RETRY = 5;

    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public PlaceResponse createPlace(Long userId, PlaceCreateRequest request) {

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (owner.getRole() != UserRole.PROVIDER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "제공자가 아닙니다.");
        }

        if (!request.getOpenTime().isBefore(request.getCloseTime())) {
            throw new IllegalArgumentException("오픈 시간은 마감 시간보다 전이어야 합니다.");
        }

        String name = safeTrim(request.getName());
        String address = safeTrim(request.getAddress());

        for (int attempt = 1; attempt <= CODE_MAX_RETRY; attempt++) {
            String code = generateUniqueCode(); // 난수 기반

            try {
                Place place = Place.builder()
                        .name(name)
                        .content(request.getContent())
                        .imageUrl(request.getImageUrl())
                        .maxCapacity(request.getMaxCapacity())
                        .latitude(request.getLatitude())
                        .longitude(request.getLongitude())
                        .address(address)
                        .openTime(request.getOpenTime())
                        .closeTime(request.getCloseTime())
                        .weeklyHoliday(request.getWeeklyHoliday())
                        .code(code)
                        .owner(owner)
                        .build();

                Place saved = placeRepository.save(place);
                return PlaceResponse.from(saved);

            } catch (DataIntegrityViolationException e) {
                // 유니크 제약 위반(코드 충돌) 가능성 → 재시도
                if (attempt == CODE_MAX_RETRY) {
                    throw new IllegalStateException("장소 코드 생성에 실패했다. 잠시 후 다시 시도하라.", e);
                }
            }
        }

        throw new IllegalStateException("장소 코드 생성 재시도 초과");
    }

    @Transactional(readOnly = true)
    public PlaceResponse getPlace(Long userId) {
        User provider = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (provider.getRole() != UserRole.PROVIDER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "제공자가 아닙니다.");
        }

        Place place = placeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("등록된 장소가 없습니다."));

        return PlaceResponse.from(place);
    }

    @Transactional
    public void updatePlace(Long userId, PlaceUpdateRequest request){
        User provider = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (provider.getRole() != UserRole.PROVIDER) {
            throw new IllegalArgumentException("제공자가 아닙니다.");
        }

        Place place = placeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("등록된 장소가 없습니다."));

        place.updatePatch(request);

        placeRepository.save(place);
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = "PL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            // 예: PL-A12F9C3D
        } while (placeRepository.existsByCode(code));
        return code;
    }

    private String safeTrim(String s) {
        return (s == null) ? null : s.trim();
    }


}
