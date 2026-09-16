package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.application.respuesta.command.validator.impl.ModificarEstadoRespuestaNovedadCoordinadorValidatorImpl;
import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.respuesta.exception.EstadoRespuestaNoResolutivoException;
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

class ModificarEstadoRespuestaNovedadCoordinadorValidatorImplTest {

    private final ModificarEstadoRespuestaNovedadCoordinadorValidatorImpl validator =
            new ModificarEstadoRespuestaNovedadCoordinadorValidatorImpl();

    private static final String TIPO_OK = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId();
    private static final String ESTADO_ACTUAL_OK = "EN_REVISION";
    private static final String NUEVO_ESTADO_OK = "APROBADA";

    private ModificacionEstadoRespuestaNovedadCoordinadorDomain entrada(UUID solicitud, UUID coordinador) {
        return ModificacionEstadoRespuestaNovedadCoordinadorDomain.crear(
                solicitud, coordinador, NUEVO_ESTADO_OK);
    }

    @Test
    void debePasar_cuandoLasSeisReglasSeCumplen() {
        // Arrange
        var coordinador = UUID.randomUUID();
        var entrada = entrada(UUID.randomUUID(), coordinador);

        // Act & Assert
        assertThatCode(() -> validator.validar(
                entrada, true, TIPO_OK, coordinador, true, ESTADO_ACTUAL_OK))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudNoEncontrada_cuandoLaSolicitudNoExiste() {
        // Arrange
        var coordinador = UUID.randomUUID();
        var entrada = entrada(UUID.randomUUID(), coordinador);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, false, TIPO_OK, coordinador, true, ESTADO_ACTUAL_OK))
                .isInstanceOf(SolicitudNoEncontradaException.class)
                .hasMessageContaining(entrada.getSolicitud().toString());
    }

    @Test
    void debeLanzarSolicitudTipoNoCoincide_cuandoLaSolicitudEsDeOtroTipo() {
        // Arrange
        var coordinador = UUID.randomUUID();
        var entrada = entrada(UUID.randomUUID(), coordinador);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, true, TipoSolicitud.CAMBIO_DE_ASESOR.getId(),
                coordinador, true, ESTADO_ACTUAL_OK))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeLanzarSolicitudNoEsDestinatario_cuandoElCoordinadorNoEsElDestinatario() {
        // Arrange
        var entrada = entrada(UUID.randomUUID(), UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, true, TIPO_OK, UUID.randomUUID(), true, ESTADO_ACTUAL_OK))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }

    @Test
    void debeLanzarRespuestaNoEncontrada_cuandoNoExisteRespuestaParaLaSolicitud() {
        // Arrange
        var coordinador = UUID.randomUUID();
        var entrada = entrada(UUID.randomUUID(), coordinador);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, true, TIPO_OK, coordinador, false, ESTADO_ACTUAL_OK))
                .isInstanceOf(RespuestaNoEncontradaException.class)
                .hasMessageContaining(entrada.getSolicitud().toString());
    }

    @Test
    void debeLanzarRespuestaNoEnRevision_cuandoLaRespuestaYaFueDecidida() {
        // Arrange
        var coordinador = UUID.randomUUID();
        var entrada = entrada(UUID.randomUUID(), coordinador);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, true, TIPO_OK, coordinador, true, "APROBADA"))
                .isInstanceOf(RespuestaNoEnRevisionException.class)
                .hasMessageContaining(entrada.getSolicitud().toString());
    }

    @Test
    void debeLanzarEstadoRespuestaNoResolutivo_cuandoElNuevoEstadoEsEnRevision() {
        // Arrange
        var coordinador = UUID.randomUUID();
        var entrada = ModificacionEstadoRespuestaNovedadCoordinadorDomain.crear(
                UUID.randomUUID(), coordinador, "EN_REVISION");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, true, TIPO_OK, coordinador, true, ESTADO_ACTUAL_OK))
                .isInstanceOf(EstadoRespuestaNoResolutivoException.class)
                .hasMessageContaining(entrada.getSolicitud().toString());
    }
}
