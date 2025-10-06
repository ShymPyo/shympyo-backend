package shympyo.global.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SseHeartbeatScheduler {

    private final SseEmitterHub hub;

    @Scheduled(fixedRate = 30000)
    public void sendPing() {
        hub.sendAll("ping", System.currentTimeMillis());
    }
}
