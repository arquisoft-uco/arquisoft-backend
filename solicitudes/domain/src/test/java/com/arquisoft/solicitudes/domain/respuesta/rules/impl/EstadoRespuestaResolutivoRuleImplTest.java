package com.arquisoft.solicitudes.domain.respuesta.rules.impl;

import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.exception.EstadoRespuestaNoResolutivoException;
import com.arquisoft.solicitudes.domain.respuesta.model.NuevoEstadoRespuesta;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoRespuestaResolutivoRuleImplTest {

    private final EstadoRespuestaResolutivoRuleImpl rule = new EstadoRespuestaResolutivoRuleImpl();

    @Test
    void debePasar_cuandoElNuevoEstadoEsAprobada() {
        assertThatCode(() -> rule.validar(
                new NuevoEstadoRespuesta(UUID.randomUUID(), EstadoRespuesta.APROBADA)))
                .doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoElNuevoEstadoEsNoAprobada() {
        assertThatCode(() -> rule.validar(
                new NuevoEstadoRespuesta(UUID.randomUUID(), EstadoRespuesta.NO_APROBADA)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoElNuevoEstadoEsEnRevision() {
        var solicitud = UUID.randomUUID();
        assertThatThrownBy(() -> rule.validar(
                new NuevoEstadoRespuesta(solicitud, EstadoRespuesta.EN_REVISION)))
                .isInstanceOf(EstadoRespuestaNoResolutivoException.class)
                .hasMessageContaining(solicitud.toString());
    }
}
