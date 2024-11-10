package org.example.subwp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.example.subwp")
public class SubWpApplication {
    public static void main(String[] args) {
        SpringApplication.run(SubWpApplication.class, args);
    }

}
