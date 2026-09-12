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
        assertThatCode(() -> rule.validar(new ExistenciaRespuesta(UUID.randomUUID(), true)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaRespuestaNoExiste() {
        UUID solicitud = UUID.randomUUID();
        assertThatThrownBy(() -> rule.validar(new ExistenciaRespuesta(solicitud, false)))
                .isInstanceOf(RespuestaNoEncontradaException.class)
                .hasMessageContaining(solicitud.toString());
    }
}
