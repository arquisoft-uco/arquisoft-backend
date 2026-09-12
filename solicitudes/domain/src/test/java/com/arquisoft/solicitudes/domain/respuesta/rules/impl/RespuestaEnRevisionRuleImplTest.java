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
        assertThatCode(() -> rule.validar(new EstadoRespuestaActual(
                UUID.randomUUID(), EstadoRespuesta.EN_REVISION.getId())))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoElEstadoEsAprobada() {
        UUID solicitud = UUID.randomUUID();
        assertThatThrownBy(() -> rule.validar(new EstadoRespuestaActual(
                solicitud, EstadoRespuesta.APROBADA.getId())))
                .isInstanceOf(RespuestaNoEnRevisionException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @Test
    void debeLanzar_cuandoElEstadoEsNoAprobada() {
        UUID solicitud = UUID.randomUUID();
        assertThatThrownBy(() -> rule.validar(new EstadoRespuestaActual(
                solicitud, EstadoRespuesta.NO_APROBADA.getId())))
                .isInstanceOf(RespuestaNoEnRevisionException.class)
                .hasMessageContaining(solicitud.toString());
    }
}
