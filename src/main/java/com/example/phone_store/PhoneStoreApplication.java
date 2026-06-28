package com.example.phone_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync          // ← cho phép @Async hoạt động (gửi email không block request)
public class PhoneStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhoneStoreApplication.class, args);
    }
}