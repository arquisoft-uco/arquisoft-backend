package com.arquisoft.solicitudes.domain.respuesta.rules.impl;

import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEnRevisionException;
import com.arquisoft.solicitudes.domain.respuesta.model.EstadoRespuestaActual;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RespuestaEnRevisionRuleImplTest {

    private final RespuestaEnRevisionRuleImpl rule = new RespuestaEnRevisionRuleImpl();

    @Test
    void debePasar_cuandoElEstadoEsEnRevision() {
        // Arrange
        var estado = new EstadoRespuestaActual(UUID.randomUUID(), EstadoRespuesta.EN_REVISION.getId());

        // Act & Assert
        assertThatCode(() -> rule.validar(estado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoElEstadoEsAprobada() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var estado = new EstadoRespuestaActual(solicitud, EstadoRespuesta.APROBADA.getId());

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(estado))
                .isInstanceOf(RespuestaNoEnRevisionException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @Test
    void debeLanzar_cuandoElEstadoEsNoAprobada() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var estado = new EstadoRespuestaActual(solicitud, EstadoRespuesta.NO_APROBADA.getId());

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(estado))
                .isInstanceOf(RespuestaNoEnRevisionException.class)
                .hasMessageContaining(solicitud.toString());
    }
}
