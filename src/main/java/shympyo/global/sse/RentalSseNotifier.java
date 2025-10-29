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

        var payload = new RentalStartedPayload(
                event.rentalId(),
                event.placeId(),
                event.userId(),
                event.userName(),
                event.startTime()
        );
        hub.send(event.placeId(), "rental-started", payload);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRentalEnded(RentalEndedEvent event) {

        var payload = new RentalEndedPayload(
                event.rentalId(),
                event.placeId(),
                event.endTime()
        );

        hub.send(event.placeId(), "rental-ended", payload);
    }
}
