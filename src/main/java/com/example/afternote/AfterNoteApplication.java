package com.example.afternote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class AfterNoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(AfterNoteApplication.class, args);
    }

}
