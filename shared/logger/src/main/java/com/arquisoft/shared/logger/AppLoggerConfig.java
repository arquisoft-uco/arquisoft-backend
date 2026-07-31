package com.arquisoft.shared.logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Provee el puerto {@link AppLogger} como bean inyectable por constructor.
 *
 * <p>El scope prototype con {@link InjectionPoint} permite que cada clase reciba
 * un logger nombrado según su propia clase declarante — equivalente funcional a
 * {@code LoggerFactory.getLogger(MiClase.class)} pero a través del puerto,
 * sin acoplar al consumidor a SLF4J ni a Lombok.</p>
 */
@Configuration
public class AppLoggerConfig {

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    AppLogger appLogger(InjectionPoint injectionPoint) {
        Class<?> clase = injectionPoint.getMember().getDeclaringClass();
        return new Slf4jAppLogger(LoggerFactory.getLogger(clase));
    }
}
