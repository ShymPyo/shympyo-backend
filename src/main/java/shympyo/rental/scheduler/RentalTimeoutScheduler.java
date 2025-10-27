package shympyo.rental.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shympyo.rental.repository.RentalRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentalTimeoutScheduler {

    private final RentalRepository rentalRepository;

    @Transactional
    //@Scheduled(cron = "0 * * * * *")
    public void markOverdueRentals() {
        LocalDateTime now = LocalDateTime.now();
        int updated = rentalRepository.markTimeExceeded(now);
        log.info("[TIME_EXCEEDED] updated={}", updated);
    }

    @Transactional
    //@Scheduled(fixedDelay = 5000)
    public void markOverdueRentalsInBatches() {
        LocalDateTime now = LocalDateTime.now();
        int pageSize = 500;
        while (true) {
            var ids = rentalRepository.findOverdueIds(now, PageRequest.of(0, pageSize));
            if (ids.isEmpty()) break;
            rentalRepository.markTimeExceededByIds(ids, now);
        }
    }
}
