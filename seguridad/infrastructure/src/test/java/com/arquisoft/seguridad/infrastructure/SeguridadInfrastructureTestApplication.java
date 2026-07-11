package com.arquisoft.seguridad.infrastructure;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase de configuración mínima para los tests de slice (@WebMvcTest) del módulo
 * seguridad:infrastructure.
 *
 * <p>Este módulo no tiene una clase @SpringBootApplication propia (es un submódulo
 * del monorepo). Spring Boot Test necesita encontrar una @SpringBootConfiguration
 * en el árbol de paquetes del test para inicializar el contexto de test.</p>
 */
@SpringBootApplication
public class SeguridadInfrastructureTestApplication {
}
