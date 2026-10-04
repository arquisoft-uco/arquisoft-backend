package com.arquisoft.solicitudes.domain.respuesta.rules.impl;

import com.arquisoft.solicitudes.domain.respuesta.exception.SolicitudYaRespondidaException;
import com.arquisoft.solicitudes.domain.respuesta.model.RespuestaSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudRespondidaRuleImplTest {

    private final SolicitudRespondidaRuleImpl rule = new SolicitudRespondidaRuleImpl();

    @Test
    void debePasar_cuandoLaSolicitudNoTieneRespuesta() {
        assertThatCode(() -> rule.validar(new RespuestaSolicitud(UUID.randomUUID(), false)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaSolicitudYaFueRespondida() {
        UUID solicitud = UUID.randomUUID();
        assertThatThrownBy(() -> rule.validar(new RespuestaSolicitud(solicitud, true)))
                .isInstanceOf(SolicitudYaRespondidaException.class)
                .hasMessageContaining(solicitud.toString());
    }
}
