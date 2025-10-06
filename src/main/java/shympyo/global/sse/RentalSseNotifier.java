package shympyo.global.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import shympyo.rental.domain.Rental;
import shympyo.rental.dto.sse.RentalEndedEvent;
import shympyo.rental.dto.sse.RentalEndedPayload;
import shympyo.rental.dto.sse.RentalStartedEvent;
import shympyo.rental.dto.sse.RentalStartedPayload;
import shympyo.rental.repository.RentalRepository;

@Component
@RequiredArgsConstructor
public class RentalSseNotifier {

    private final SseEmitterHub hub;
    private final RentalRepository rentalRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRentalStarted(RentalStartedEvent event) {
        Rental r = rentalRepository.findById(event.rentalId())
                .orElseThrow();

        var payload = new RentalStartedPayload(
                r.getId(), r.getPlace().getId(), r.getUser().getId(), r.getUser().getName(), r.getStartTime()
        );
        hub.send(r.getPlace().getId(), "rental-started", payload);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRentalEnded(RentalEndedEvent event) {
        Rental r = rentalRepository.findById(event.rentalId())
                .orElseThrow();

        var payload = new RentalEndedPayload(
                r.getId(), r.getPlace().getId(), r.getEndTime()
        );
        hub.send(r.getPlace().getId(), "rental-ended", payload);
    }
}
