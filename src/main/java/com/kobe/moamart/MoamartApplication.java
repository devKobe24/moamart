package com.kobe.moamart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MoamartApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoamartApplication.class, args);
    }

}
