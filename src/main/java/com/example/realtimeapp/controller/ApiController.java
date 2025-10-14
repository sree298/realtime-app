package com.example.realtimeapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class ApiController {

    @GetMapping("/api")
    public Map<String, String> getApiStatus() {
        return Map.of(
            "status", "OK",
            "message", "Welcome to Realtime App API 🚀",
            "timestamp", java.time.LocalDateTime.now().toString()
        );
    }
}
