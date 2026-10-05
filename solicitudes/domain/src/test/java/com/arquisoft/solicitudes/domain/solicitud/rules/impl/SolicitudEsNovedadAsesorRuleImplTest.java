package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideAsesorException;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudEsNovedadAsesorRuleImplTest {

    private final SolicitudEsNovedadAsesorRuleImpl regla = new SolicitudEsNovedadAsesorRuleImpl();

    private static final String ESPERADO = TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId();

    @Test
    void debeNoLanzar_cuandoElTipoActualCoincideConElEsperado() {
        // Act & Assert
        assertThatCode(() -> regla.validar(
                new TipoSolicitudConcordante(UUID.randomUUID(), ESPERADO, ESPERADO)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudTipoNoCoincideAsesor_cuandoElTipoActualDifiere() {
        // Arrange
        var solicitud = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new TipoSolicitudConcordante(
                solicitud, TipoSolicitud.CAMBIO_DE_ASESOR.getId(), ESPERADO)))
                .isInstanceOf(SolicitudTipoNoCoincideAsesorException.class)
                .hasMessageContaining(solicitud.toString());
    }
}
