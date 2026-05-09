package com.arquisoft.seguridad.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Habilita el soporte de tareas programadas ({@code @Scheduled}) de Spring.
 *
 * <p>Se define en una clase dedicada para que la activación del scheduler no sea
 * un efecto secundario inesperado de otra clase de configuración (SRP). Cualquier
 * componente del módulo puede declarar métodos {@code @Scheduled} con solo tener
 * esta clase en el contexto.</p>
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
