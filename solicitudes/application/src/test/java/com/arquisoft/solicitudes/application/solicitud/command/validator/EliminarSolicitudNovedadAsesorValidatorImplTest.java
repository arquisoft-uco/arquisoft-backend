package com.arquisoft.solicitudes.application.solicitud.command.validator;

import com.arquisoft.solicitudes.application.solicitud.command.validator.impl.EliminarSolicitudNovedadAsesorValidatorImpl;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudConRespuestasException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoPropiaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideAsesorException;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminarSolicitudNovedadAsesorValidatorImplTest {

    private final EliminarSolicitudNovedadAsesorValidatorImpl validator =
            new EliminarSolicitudNovedadAsesorValidatorImpl();

    private static final String TIPO_OK = TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId();

    @Test
    void debePasar_cuandoLasCuatroReglasSeCumplen() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var solicitante = UUID.randomUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(
                solicitud, true, solicitante, TIPO_OK, solicitante, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudNoEncontrada_cuandoLaSolicitudNoExiste() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var solicitante = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                solicitud, false, solicitante, TIPO_OK, solicitante, false))
                .isInstanceOf(SolicitudNoEncontradaException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @Test
    void debeLanzarSolicitudNoPropia_cuandoElRemitenteNoEsElSolicitante() {
        // Arrange
        var solicitud = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                solicitud, true, UUID.randomUUID(), TIPO_OK, UUID.randomUUID(), false))
                .isInstanceOf(SolicitudNoPropiaException.class);
    }

    @Test
    void debeLanzarSolicitudTipoNoCoincideAsesor_cuandoElTipoNoEsNovedadAsesor() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var solicitante = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                solicitud, true, solicitante, TipoSolicitud.CAMBIO_DE_ASESOR.getId(), solicitante, false))
                .isInstanceOf(SolicitudTipoNoCoincideAsesorException.class);
    }

    @Test
    void debeLanzarSolicitudConRespuestas_cuandoLaSolicitudTieneRespuestas() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var solicitante = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                solicitud, true, solicitante, TIPO_OK, solicitante, true))
                .isInstanceOf(SolicitudConRespuestasException.class);
    }
}
