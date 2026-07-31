package com.arquisoft.shared.logger;

/**
 * Puerto de logging de la aplicación — patrón Strategy.
 *
 * <p>Desacopla las capas de aplicación e infraestructura de la tecnología de logging
 * concreta (hoy SLF4J vía {@link Slf4jAppLogger}): cambiar de proveedor solo requiere
 * una nueva implementación de esta interfaz, sin tocar a los consumidores.</p>
 *
 * <p><strong>Contrato de formato:</strong> los mensajes usan placeholders {@code {}}
 * posicionales (convención de las constantes {@code LOG_*} del catálogo de mensajes).
 * Toda implementación debe interpretarlos; la implementación SLF4J delega directamente.</p>
 *
 * <p>Se inyecta por constructor como cualquier otra dependencia
 * (ver {@link AppLoggerConfig}) y se sustituye por un mock en pruebas unitarias.</p>
 */
public interface AppLogger {

    void debug(String message, Object... args);

    void info(String message, Object... args);

    void warn(String message, Object... args);

    void error(String message, Object... args);

    void error(String message, Throwable cause, Object... args);
}
