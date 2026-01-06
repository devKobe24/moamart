package com.kobe.moamart;

import com.kobe.moamart.global.config.SecretsManagerConfigLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MoamartApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MoamartApplication.class);
        // Secrets Manager에서 값을 가져와서 Spring Environment에 주입
        app.addListeners(new SecretsManagerConfigLoader());
        app.run(args);
    }

}
