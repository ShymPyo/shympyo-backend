package account_service.rental.service;

import account_service.rental.domain.Place;
import account_service.rental.domain.Rental;
import account_service.rental.dto.ExitResponse;
import account_service.rental.dto.RentalResponse;
import account_service.rental.repository.PlaceRepository;
import account_service.rental.repository.RentalRepository;
import account_service.user.domain.User;
import account_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final PlaceRepository placeRepository;


    @Transactional
    public RentalResponse startRental(Long userId, String placeCode) {

        // 1) 장소 행을 잠그며 조회 → 같은 placeId에 대한 동시 진입 직렬화
        Place place = placeRepository.findByCodeForUpdate(placeCode)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 장소가 없습니다."));

        // 2) 유저는 프록시로 참조(불필요한 select 회피)
        User user = userRepository.getReferenceById(userId);

        // 3) 현재 사용 인원 카운트
        long usingCount = rentalRepository.countByPlaceIdAndStatus(place.getId(), "using");
        if (usingCount >= place.getMaxCapacity()) {
            throw new IllegalStateException("이 장소는 이미 최대 수용 인원("
                    + place.getMaxCapacity() + "명)에 도달했습니다.");
        }

        // 4) 대여 시작
        Rental rental = Rental.start(place, user, LocalDateTime.now());
        rentalRepository.save(rental);

        RentalResponse response = toRentalResponse(rental);

        return response;
    }


    @Transactional
    public ExitResponse endRental(Long userId) {
        // 진행 중 대여들 조회
        var actives = rentalRepository.findByUserIdAndStatus(userId, "using");

        if (actives.isEmpty()) {
            throw new IllegalStateException("진행 중인 대여가 없습니다.");
        }
        if (actives.size() > 1) {
            // 정책상 모호 → 에러 (또는 '가장 최근 1건'으로 자동 선택하도록 바꿀 수 있음)
            throw new IllegalStateException("진행 중인 대여가 여러 건입니다. 어떤 대여를 종료할지 지정해 주세요.");
        }

        // 동시 종료 대비: 해당 행 재조회 + 락
        Long rentalId = actives.get(0).getId();
        Rental rental = rentalRepository.findByIdForUpdate(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("대여 정보를 찾을 수 없습니다."));

        // 멱등
        if (!"using".equals(rental.getStatus())) return toExitResponse(rental);

        rental.end(LocalDateTime.now());
        return toExitResponse(rental);
    }

    private ExitResponse toExitResponse(Rental r) {
        // 여기서 LAZY 필드 접근해도 트랜잭션 안이라 안전
        return ExitResponse.builder()
                .rentalId(r.getId())
                .placeName(r.getPlace().getName())
                .startTime(r.getStartTime())
                .endTime(r.getStartTime())
                .build();
    }

    private RentalResponse toRentalResponse(Rental rental) {
        RentalResponse response = RentalResponse.builder()

                .rentalId(rental.getId())
                .placeName(rental.getPlace().getName())
                .startTime(rental.getStartTime())
                .build();
        return response;
    }
    
    
}
