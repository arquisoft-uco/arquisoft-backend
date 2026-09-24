package com.arquisoft.solicitudes.domain.respuesta.rules.impl;

import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEncontradaException;
import com.arquisoft.solicitudes.domain.respuesta.model.ExistenciaRespuesta;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RespuestaExisteRuleImplTest {

    private final RespuestaExisteRuleImpl rule = new RespuestaExisteRuleImpl();

    @Test
    void debePasar_cuandoLaRespuestaExiste() {
        // Arrange
        var existencia = new ExistenciaRespuesta(UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaRespuestaNoExiste() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var existencia = new ExistenciaRespuesta(solicitud, false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(RespuestaNoEncontradaException.class)
                .hasMessageContaining(solicitud.toString());
    }
}
