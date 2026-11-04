package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideCambioAsesorException;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import com.arquisoft.shared.util.UtilUUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudEsCambioAsesorRuleImplTest {

    private final SolicitudEsCambioAsesorRuleImpl regla = new SolicitudEsCambioAsesorRuleImpl();

    private static final String ESPERADO = TipoSolicitud.CAMBIO_DE_ASESOR.getId();

    @Test
    void noDebeLanzar_cuandoElTipoCoincide() {
        // Act & Assert
        assertThatCode(() -> regla.validar(
                new TipoSolicitudConcordante(UtilUUID.generarNuevoUUID(), ESPERADO, ESPERADO)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudTipoNoCoincideCambioAsesor_cuandoElTipoEsOtro() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new TipoSolicitudConcordante(
                solicitud, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId(), ESPERADO)))
                .isInstanceOf(SolicitudTipoNoCoincideCambioAsesorException.class);
    }
}
