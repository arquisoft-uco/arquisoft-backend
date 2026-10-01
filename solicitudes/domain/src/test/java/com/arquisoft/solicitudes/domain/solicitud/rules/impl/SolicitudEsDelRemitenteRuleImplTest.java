package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoPropiaException;
import com.arquisoft.solicitudes.domain.solicitud.model.PropiedadSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudEsDelRemitenteRuleImplTest {

    private final SolicitudEsDelRemitenteRuleImpl regla = new SolicitudEsDelRemitenteRuleImpl();

    @Test
    void debeNoLanzar_cuandoElRemitenteEsElSolicitante() {
        // Arrange
        UUID solicitante = UUID.randomUUID();

        // Act & Assert
        assertThatCode(() -> regla.validar(
                new PropiedadSolicitud(UUID.randomUUID(), solicitante, solicitante)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudNoPropia_cuandoElRemitenteDifiereDelSolicitante() {
        // Arrange
        UUID solicitud = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(
                new PropiedadSolicitud(solicitud, UUID.randomUUID(), UUID.randomUUID())))
                .isInstanceOf(SolicitudNoPropiaException.class)
                .hasMessageContaining(solicitud.toString());
    }
}
