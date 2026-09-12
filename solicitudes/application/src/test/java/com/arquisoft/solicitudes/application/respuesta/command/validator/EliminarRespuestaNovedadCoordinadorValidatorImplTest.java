package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.application.respuesta.command.validator.impl.EliminarRespuestaNovedadCoordinadorValidatorImpl;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEnRevisionException;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEsDestinatarioException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideException;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminarRespuestaNovedadCoordinadorValidatorImplTest {

    private final EliminarRespuestaNovedadCoordinadorValidatorImpl validator =
            new EliminarRespuestaNovedadCoordinadorValidatorImpl();

    private static final String TIPO_OK = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId();
    private static final String ESTADO_OK = "EN_REVISION";

    @Test
    void debePasar_cuandoLasCincoReglasSeCumplen() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(
                solicitud, true, TIPO_OK, coordinador, coordinador, true, ESTADO_OK))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudNoEncontrada_cuandoLaSolicitudNoExiste() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                solicitud, false, TIPO_OK, coordinador, coordinador, true, ESTADO_OK))
                .isInstanceOf(SolicitudNoEncontradaException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @Test
    void debeLanzarSolicitudTipoNoCoincide_cuandoLaSolicitudEsDeOtroTipo() {
        // Arrange
        UUID coordinador = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), true, TipoSolicitud.CAMBIO_DE_ASESOR.getId(),
                coordinador, coordinador, true, ESTADO_OK))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeLanzarSolicitudNoEsDestinatario_cuandoElCoordinadorNoEsElDestinatario() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), true, TIPO_OK, UUID.randomUUID(), UUID.randomUUID(), true, ESTADO_OK))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }

    @Test
    void debeLanzarRespuestaNoEncontrada_cuandoNoExisteRespuestaParaLaSolicitud() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                solicitud, true, TIPO_OK, coordinador, coordinador, false, ESTADO_OK))
                .isInstanceOf(RespuestaNoEncontradaException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @Test
    void debeLanzarRespuestaNoEnRevision_cuandoElEstadoNoEsEnRevision() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                solicitud, true, TIPO_OK, coordinador, coordinador, true, "APROBADA"))
                .isInstanceOf(RespuestaNoEnRevisionException.class)
                .hasMessageContaining(solicitud.toString());
    }
}
