package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideException;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudEsNovedadCoordinadorRuleImplTest {

    private final SolicitudEsNovedadCoordinadorRuleImpl regla = new SolicitudEsNovedadCoordinadorRuleImpl();

    private static final String ESPERADO = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId();

    @Test
    void debeNoLanzar_cuandoElTipoActualCoincideConElEsperado() {
        // Act & Assert
        assertThatCode(() -> regla.validar(
                new TipoSolicitudConcordante(UUID.randomUUID(), ESPERADO, ESPERADO)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudTipoNoCoincide_cuandoElTipoActualDifiere() {
        // Arrange
        UUID solicitud = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new TipoSolicitudConcordante(
                solicitud, TipoSolicitud.CAMBIO_DE_ASESOR.getId(), ESPERADO)))
                .isInstanceOf(SolicitudTipoNoCoincideException.class)
                .hasMessageContaining(solicitud.toString());
    }
}
