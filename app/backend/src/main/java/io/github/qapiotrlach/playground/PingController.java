package io.github.qapiotrlach.playground;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api")
public class PingController {

    public record PingResponse(String status, String application, Instant timestamp) {}

    @GetMapping("/ping")
    public PingResponse ping() {
        return new PingResponse("ok", "playground-backend", Instant.now());
    }
}
