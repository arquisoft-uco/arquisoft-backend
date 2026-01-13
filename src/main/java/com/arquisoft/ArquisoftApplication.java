package com.arquisoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación principal de Arquisoft.
 * Arquitectura Hexagonal Modular con 11 contextos independientes
 * y comunicación asincrónica mediante RabbitMQ.
 */
@SpringBootApplication
public class ArquisoftApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArquisoftApplication.class, args);
    }
}
