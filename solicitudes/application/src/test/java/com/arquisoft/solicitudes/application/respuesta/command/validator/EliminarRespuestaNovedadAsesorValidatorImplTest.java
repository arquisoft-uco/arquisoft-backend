package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.application.respuesta.command.validator.impl.EliminarRespuestaNovedadAsesorValidatorImpl;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEnRevisionException;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEsDestinatarioException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideException;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminarRespuestaNovedadAsesorValidatorImplTest {

    private static final String TIPO_OK = TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId();
    private static final String ESTADO_OK = "EN_REVISION";

    private final EliminarRespuestaNovedadAsesorValidatorImpl validator =
            new EliminarRespuestaNovedadAsesorValidatorImpl();

    @Test
    void debePasar_cuandoLasCincoReglasSeCumplen() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var asesor = UUID.randomUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(
                solicitud, true, TIPO_OK, asesor, asesor, true, ESTADO_OK))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudNoEncontrada_cuandoLaSolicitudNoExiste() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var asesor = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                solicitud, false, TIPO_OK, asesor, asesor, true, ESTADO_OK))
                .isInstanceOf(SolicitudNoEncontradaException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @Test
    void debeLanzarSolicitudTipoNoCoincide_cuandoLaSolicitudEsDeOtroTipo() {
        // Arrange
        var asesor = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), true, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId(),
                asesor, asesor, true, ESTADO_OK))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeLanzarSolicitudNoEsDestinatario_cuandoElAsesorNoEsElDestinatario() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), true, TIPO_OK, UUID.randomUUID(), UUID.randomUUID(), true, ESTADO_OK))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }

    @Test
    void debeLanzarRespuestaNoEncontrada_cuandoNoExisteRespuestaParaLaSolicitud() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var asesor = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                solicitud, true, TIPO_OK, asesor, asesor, false, ESTADO_OK))
                .isInstanceOf(RespuestaNoEncontradaException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"APROBADA", "NO_APROBADA"})
    void debeLanzarRespuestaNoEnRevision_cuandoElEstadoNoEsEnRevision(String estado) {
        // Arrange
        var solicitud = UUID.randomUUID();
        var asesor = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                solicitud, true, TIPO_OK, asesor, asesor, true, estado))
                .isInstanceOf(RespuestaNoEnRevisionException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @Test
    void debeLanzarSolicitudNoEncontrada_cuandoFallanLaSolicitudYLaRespuesta() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), false, null, null, UUID.randomUUID(), false, null))
                .isInstanceOf(SolicitudNoEncontradaException.class);
    }
}
