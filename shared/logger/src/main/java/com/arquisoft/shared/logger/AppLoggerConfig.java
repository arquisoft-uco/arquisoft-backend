package com.arquisoft.shared.logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppLoggerConfig {

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    AppLogger appLogger(InjectionPoint injectionPoint) {
        Class<?> clase = injectionPoint.getMember().getDeclaringClass();
        return new Slf4jAppLogger(LoggerFactory.getLogger(clase));
    }
}
