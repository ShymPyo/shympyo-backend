package shympyo.rental.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import shympyo.rental.domain.Place;
import shympyo.rental.domain.Rental;
import shympyo.rental.dto.*;
import shympyo.rental.repository.PlaceRepository;
import shympyo.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shympyo.user.domain.User;
import shympyo.user.domain.UserRole;
import shympyo.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final PlaceRepository placeRepository;


    @Transactional
    public RentalResponse startRental(Long userId, String placeCode) {

        Place place = placeRepository.findByCodeForUpdate(placeCode)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 장소가 없습니다."));

        User user = userRepository.getReferenceById(userId);

        long usingCount = rentalRepository.countByPlaceIdAndStatus(place.getId(), "using");
        if (usingCount >= place.getMaxCapacity()) {
            throw new IllegalStateException("이 장소는 이미 최대 수용 인원("
                    + place.getMaxCapacity() + "명)에 도달했습니다.");
        }

        Rental rental = Rental.start(place, user, LocalDateTime.now());
        rentalRepository.save(rental);

        return RentalResponse.from(rental);
    }


    @Transactional
    public ExitResponse endRental(Long userId) {

        var actives = rentalRepository.findByUserIdAndStatus(userId, "using");

        if (actives.isEmpty()) {
            throw new IllegalStateException("진행 중인 대여가 없습니다.");
        }
        if (actives.size() > 1) {
            throw new IllegalStateException("진행 중인 대여가 여러 건입니다. 어떤 대여를 종료할지 지정해 주세요.");
        }

        Long rentalId = actives.get(0).getId();
        Rental rental = rentalRepository.findByIdForUpdate(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("대여 정보를 찾을 수 없습니다."));

        if (!"using".equals(rental.getStatus())) return ExitResponse.from(rental);

        rental.end(LocalDateTime.now());

        return ExitResponse.from(rental);
    }



    @Transactional(readOnly = true)
    public List<CurrentRentalResponse> getRental(Long userId){

        User provider = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (provider.getRole() != UserRole.PROVIDER) {
            throw new IllegalArgumentException("제공자가 아닙니다.");
        }

        Place place = placeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("등록된 장소가 없습니다."));

        return rentalRepository.findCurrentRentalsWithUserByPlace(place.getId());

    }

    @Transactional(readOnly = true)
    public List<RentalHistoryResponse> getTotalRental(Long userId){

        User provider = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (provider.getRole() != UserRole.PROVIDER) {
            throw new IllegalArgumentException("제공자가 아닙니다.");
        }

        Place place = placeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("등록된 장소가 없습니다."));

        return rentalRepository.findAllHistoryByPlace(place.getId());

    }


    
    
}
