package com.team_7.moment_film;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


//(exclude = SecurityAutoConfiguration.class)
@EnableJpaAuditing
@SpringBootApplication
public class MomentFilmApplication {

    public static void main(String[] args) {
        SpringApplication.run(MomentFilmApplication.class, args);
    }

}
