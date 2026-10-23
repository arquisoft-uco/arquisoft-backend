package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep.mapper;

import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep.PepProperties;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class PepSolicitudMapperTest {

    private final PepProperties propiedades = new PepProperties("https://pep", "app", "lab", "cred", false,
            Duration.ofSeconds(1), Duration.ofSeconds(3), 0, Duration.ofMillis(1));

    @Test
    void debeArmarCuerpoYCabeceras_cuandoLaCorrelacionEsValida() {
        // Arrange
        var solicitud = new SolicitudAcceso("POST", "/api/x", "tok", "principal", "corr_1.a:b-2");

        // Act
        var cuerpo = PepSolicitudMapper.toCuerpo(propiedades, solicitud);
        var cabeceras = PepSolicitudMapper.toCabeceras(propiedades, solicitud);

        // Assert
        assertThat(cuerpo).containsEntry("applicationName", "app").containsEntry("environment", "lab")
                .containsEntry("path", "/api/x").containsEntry("method", "POST");
        assertThat(cabeceras.getFirst("Authorization")).isEqualTo("Bearer tok");
        assertThat(cabeceras.getFirst("X-Application-Credential")).isEqualTo("cred");
        assertThat(cabeceras.getFirst("X-Correlation-Id")).isEqualTo("corr_1.a:b-2");
    }

    @Test
    void debeUsarElRequestIdComoCorrelacion_cuandoNoCumpleElPatronOVieneVacia() {
        // Arrange
        var invalida = new SolicitudAcceso("GET", "/api/x", "tok", "principal", "corr con espacios\n");
        var vacia = new SolicitudAcceso("GET", "/api/x", "tok", "principal", null);

        // Act
        var cabecerasInvalida = PepSolicitudMapper.toCabeceras(propiedades, invalida);
        var cabecerasVacia = PepSolicitudMapper.toCabeceras(propiedades, vacia);

        // Assert
        assertThat(cabecerasInvalida.getFirst("X-Correlation-Id")).isEqualTo(cabecerasInvalida.getFirst("X-Request-Id"));
        assertThat(cabecerasVacia.getFirst("X-Correlation-Id")).isEqualTo(cabecerasVacia.getFirst("X-Request-Id"));
    }
}
