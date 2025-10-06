package shympyo.global.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseEmitterHub {

    private final ConcurrentHashMap<Long, CopyOnWriteArrayList<SseEmitter>> byPlace = new ConcurrentHashMap<>();

    public SseEmitter add(Long placeId, SseEmitter emitter) {
        byPlace.computeIfAbsent(placeId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(placeId, emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            remove(placeId, emitter);
        });
        emitter.onError(ex -> remove(placeId, emitter));

        try { emitter.send(SseEmitter.event().name("hello").data("connected")); } catch (IOException ignored) {}

        return emitter;
    }

    public void send(Long placeId, String event, Object data) {
        var list = byPlace.getOrDefault(placeId, new CopyOnWriteArrayList<>());
        for (SseEmitter e : list) {
            try {
                e.send(SseEmitter.event().name(event).data(data));
            } catch (Exception ex) {
                e.complete();
                list.remove(e);
            }
        }
    }

    private void remove(Long placeId, SseEmitter emitter) {
        var list = byPlace.get(placeId);
        if (list != null) list.remove(emitter);
    }
}
