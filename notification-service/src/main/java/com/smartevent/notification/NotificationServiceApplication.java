package com.smartevent.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages = "com.smartevent.notification.client")
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                NotificationServiceApplication.class, args);
    }
}