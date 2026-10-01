package com.arquisoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class ArquisoftApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArquisoftApplication.class, args);
    }
}
