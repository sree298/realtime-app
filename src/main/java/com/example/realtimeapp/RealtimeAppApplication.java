package com.example.realtimeapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class RealtimeAppApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(RealtimeAppApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RealtimeAppApplication.class);
    }
}