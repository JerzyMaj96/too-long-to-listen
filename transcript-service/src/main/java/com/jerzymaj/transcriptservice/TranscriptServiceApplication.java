package com.jerzymaj.transcriptservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TranscriptServiceApplication {

    static void main(String[] args) {
        SpringApplication.run(TranscriptServiceApplication.class, args);
    }

}
