package shympyo.rental.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shympyo.rental.domain.Place;
import shympyo.rental.domain.PlaceBusinessHour;
import shympyo.rental.dto.*;
import shympyo.rental.repository.PlaceBusinessHourRepository;
import shympyo.rental.repository.PlaceRepository;
import shympyo.user.domain.User;
import shympyo.user.domain.UserRole;
import shympyo.user.repository.UserRepository;

import java.time.DayOfWeek;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceBusinessHourService {

    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final PlaceBusinessHourRepository placeBusinessHourRepository;

    @Transactional(readOnly = true)
    public BusinessHoursResponse getBusinessHours(Long placeId) {

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소를 찾을 수 없습니다."));

        List<PlaceBusinessHour> list = placeBusinessHourRepository.findByPlaceId(place.getId());

        list.sort(Comparator.comparingInt(b -> b.getDayOfWeek().getValue()));

        List<BusinessHourItemResponse> items = list.stream()
                .map(this::toItemResponse)
                .toList();

        return BusinessHoursResponse.builder()
                .placeId(place.getId())
                .items(items)
                .build();
    }

    @Transactional
    public void upsertBusinessHours(Long placeId, Long ownerId, BusinessHoursRequest req) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소를 찾을 수 없습니다."));
        if (!place.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        List<PlaceBusinessHour> existing = placeBusinessHourRepository.findByPlaceId(placeId);
        Map<DayOfWeek, PlaceBusinessHour> byDay = existing.stream()
                .collect(Collectors.toMap(PlaceBusinessHour::getDayOfWeek, Function.identity()));

        ensureNoDuplicateDays(req);

        for (BusinessHourUpsertRequest i : req.getItems()) {
            PlaceBusinessHour bh = byDay.get(i.getDayOfWeek());
            if (bh == null) {
                bh = PlaceBusinessHour.builder()
                        .place(place)
                        .dayOfWeek(i.getDayOfWeek())
                        .build();
            }
            bh.update(i.getOpenTime(), i.getCloseTime(), i.getBreakStart(), i.getBreakEnd(), i.isClosed());
            placeBusinessHourRepository.save(bh); // insert or update
            byDay.put(i.getDayOfWeek(), bh);      // 맵 갱신(선택)
        }
    }

    private void ensureNoDuplicateDays(BusinessHoursRequest req) {
        Set<DayOfWeek> seen = new HashSet<>();
        for (BusinessHourUpsertRequest i : req.getItems()) {
            if (!seen.add(i.getDayOfWeek())) {
                throw new IllegalArgumentException("요일이 중복되었습니다: " + i.getDayOfWeek());
            }
        }
    }


    private BusinessHourItemResponse toItemResponse(PlaceBusinessHour bh) {
        return BusinessHourItemResponse.builder()
                .dayOfWeek(bh.getDayOfWeek())
                .openTime(bh.getOpenTime())
                .closeTime(bh.getCloseTime())
                .breakStart(bh.getBreakStart())
                .breakEnd(bh.getBreakEnd())
                .closed(bh.isClosed())
                .build();
    }

    private Place getManagedPlaceOfProvider(Long userId) {

        User provider = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (provider.getRole() != UserRole.PROVIDER) {
            throw new IllegalArgumentException("제공자가 아닙니다.");
        }

        return placeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalArgumentException("등록된 장소가 없습니다."));
    }

}
