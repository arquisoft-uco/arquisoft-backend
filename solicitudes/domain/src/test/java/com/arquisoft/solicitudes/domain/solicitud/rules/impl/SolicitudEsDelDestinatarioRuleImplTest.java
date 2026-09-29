package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEsDestinatarioException;
import com.arquisoft.solicitudes.domain.solicitud.model.PropiedadDestinatarioSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudEsDelDestinatarioRuleImplTest {

    private final SolicitudEsDelDestinatarioRuleImpl rule = new SolicitudEsDelDestinatarioRuleImpl();

    @Test
    void debePasar_cuandoElDestinatarioEsElSolicitante() {
        UUID coordinador = UUID.randomUUID();
        assertThatCode(() -> rule.validar(new PropiedadDestinatarioSolicitud(
                UUID.randomUUID(), coordinador, coordinador)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoElSolicitanteNoEsElDestinatario() {
        assertThatThrownBy(() -> rule.validar(new PropiedadDestinatarioSolicitud(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }
}
