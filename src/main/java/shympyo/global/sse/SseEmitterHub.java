package shympyo.global.sse;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseEmitterHub {

    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> byPlace = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public SseEmitter add(Long placeId, SseEmitter emitter) {
        byPlace
                .computeIfAbsent(placeId, k -> new CopyOnWriteArrayList<>())
                .add(emitter);

        emitter.onCompletion(() -> removeEmitter(placeId, emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            removeEmitter(placeId, emitter);
        });
        emitter.onError((ex) -> {
            emitter.complete();
            removeEmitter(placeId, emitter);
        });

        return emitter;
    }

    private void removeEmitter(Long placeId, SseEmitter emitter) {
        var list = byPlace.get(placeId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) {
                byPlace.remove(placeId);
            }
        }
    }


    public void send(Long placeId, String event, Object data) {
        var list = byPlace.get(placeId);
        if (list == null || list.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : list) {
            executor.submit(() -> {
                try {
                    emitter.send(
                            SseEmitter.event()
                                    .name(event)
                                    .data(data)
                    );
                } catch (IOException | IllegalStateException ex) {
                    emitter.complete();
                    removeEmitter(placeId, emitter);
                }
            });
        }
    }

    public void sendAll(String event, Object data) {
        byPlace.values().forEach(list -> {
            for (SseEmitter e : list) {
                try {
                    e.send(SseEmitter.event().name(event).data(data));
                } catch (Exception ex) {
                    e.complete();
                    list.remove(e);
                }
            }
        });
    }

    private void remove(Long placeId, SseEmitter emitter) {
        var list = byPlace.get(placeId);
        if (list != null) {
            list.remove(emitter);
        }
    }
}
