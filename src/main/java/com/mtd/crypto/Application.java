package com.mtd.crypto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * These annotations are important. Make sure you add them to each Application class if you split this app into microservices
 */
@SpringBootApplication
@EnableMethodSecurity
@EnableJpaAuditing
@EnableCaching
@EnableScheduling


public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
