package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideAsesorException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideException;
import com.arquisoft.solicitudes.domain.solicitud.model.TipoSolicitudConcordante;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.exception.TipoSolicitudNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudEsDelTipoRuleImplTest {

    private final SolicitudEsDelTipoRuleImpl regla = new SolicitudEsDelTipoRuleImpl();

    private static TipoSolicitudConcordante concordancia(TipoSolicitud actual, TipoSolicitud esperado) {
        return new TipoSolicitudConcordante(UUID.randomUUID(), actual.getId(), esperado.getId());
    }

    @Test
    void noDebeLanzar_cuandoElTipoCoincideConNovedadParaElCoordinador() {
        // Act & Assert
        assertThatCode(() -> regla.validar(concordancia(
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR)))
                .doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoElTipoCoincideConNovedadParaElAsesor() {
        // Act & Assert
        assertThatCode(() -> regla.validar(concordancia(
                TipoSolicitud.NOVEDAD_PARA_EL_ASESOR, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarElErrorDelCoordinador_cuandoSeEsperabaNovedadParaElCoordinador() {
        // Act & Assert
        assertThatThrownBy(() -> regla.validar(concordancia(
                TipoSolicitud.CAMBIO_DE_ASESOR, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR)))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeLanzarElErrorDelAsesor_cuandoSeEsperabaNovedadParaElAsesor() {
        // Act & Assert
        assertThatThrownBy(() -> regla.validar(concordancia(
                TipoSolicitud.CAMBIO_DE_ASESOR, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR)))
                .isInstanceOf(SolicitudTipoNoCoincideAsesorException.class);
    }

    @Test
    void debeLanzarTipoNoEncontrado_cuandoElTipoEsperadoNoTieneRegla() {
        // Act & Assert
        assertThatThrownBy(() -> regla.validar(concordancia(
                TipoSolicitud.CAMBIO_DE_ASESOR, TipoSolicitud.AMPLIACION_DE_PLAZO)))
                .isInstanceOf(TipoSolicitudNoEncontradoException.class);
    }
}
