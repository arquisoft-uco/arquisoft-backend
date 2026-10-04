package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudConRespuestasException;
import com.arquisoft.solicitudes.domain.solicitud.model.RespuestasSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudSinRespuestasRuleImplTest {

    private final SolicitudSinRespuestasRuleImpl regla = new SolicitudSinRespuestasRuleImpl();

    @Test
    void debeNoLanzar_cuandoLaSolicitudNoTieneRespuestas() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new RespuestasSolicitud(UUID.randomUUID(), false)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudConRespuestas_cuandoLaSolicitudTieneRespuestas() {
        // Arrange
        UUID solicitud = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new RespuestasSolicitud(solicitud, true)))
                .isInstanceOf(SolicitudConRespuestasException.class)
                .hasMessageContaining(solicitud.toString());
    }
}
