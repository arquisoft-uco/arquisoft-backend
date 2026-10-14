package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideAmpliacionPlazoException;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import com.arquisoft.shared.util.UtilUUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudEsAmpliacionPlazoRuleImplTest {

    private final SolicitudEsAmpliacionPlazoRuleImpl regla = new SolicitudEsAmpliacionPlazoRuleImpl();

    private static final String ESPERADO = TipoSolicitud.AMPLIACION_DE_PLAZO.getId();

    @Test
    void noDebeLanzar_cuandoElTipoCoincide() {
        // Act & Assert
        assertThatCode(() -> regla.validar(
                new TipoSolicitudConcordante(UtilUUID.generarNuevoUUID(), ESPERADO, ESPERADO)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudTipoNoCoincideAmpliacionPlazo_cuandoElTipoEsOtro() {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new TipoSolicitudConcordante(
                solicitud, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId(), ESPERADO)))
                .isInstanceOf(SolicitudTipoNoCoincideAmpliacionPlazoException.class);
    }
}
