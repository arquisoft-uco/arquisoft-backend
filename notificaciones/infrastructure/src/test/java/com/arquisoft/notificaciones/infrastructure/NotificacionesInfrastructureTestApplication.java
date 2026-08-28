package com.arquisoft.notificaciones.infrastructure;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

/**
 * Punto de anclaje para los slices {@code @DataJpaTest} de este modulo.
 *
 * <p>Spring Test busca hacia arriba una clase {@code @SpringBootConfiguration}; sin esta el slice
 * no arranca. Vive solo en {@code src/test} — no forma parte del artefacto.
 */
@SpringBootApplication
@EntityScan("com.arquisoft.notificaciones.infrastructure")
public class NotificacionesInfrastructureTestApplication {
}
