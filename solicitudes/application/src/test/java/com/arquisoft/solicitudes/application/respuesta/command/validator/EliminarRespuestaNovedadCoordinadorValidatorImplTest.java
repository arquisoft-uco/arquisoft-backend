package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.application.respuesta.command.validator.impl.EliminarRespuestaNovedadCoordinadorValidatorImpl;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEnRevisionException;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEncontradaException;
import com.arquisoft.solicitudes.domain.respuesta.model.ResumenRespuesta;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEsDestinatarioException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideException;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
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

    private static ResumenSolicitud resumenSolicitud(UUID solicitud, UUID destinatario, String tipo) {
        return new ResumenSolicitud(solicitud, UUID.randomUUID(), destinatario, tipo);
    }

    @Test
    void debePasar_cuandoLasCincoReglasSeCumplen() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var entrada = EliminacionRespuestaNovedadCoordinadorDomain.crear(solicitud, coordinador);

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, coordinador, TIPO_OK),
                new ResumenRespuesta(solicitud, ESTADO_OK)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudNoEncontrada_cuandoLaSolicitudNoExiste() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var entrada = EliminacionRespuestaNovedadCoordinadorDomain.crear(solicitud, coordinador);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                ResumenSolicitud.VACIO, new ResumenRespuesta(solicitud, ESTADO_OK)))
                .isInstanceOf(SolicitudNoEncontradaException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @Test
    void debeLanzarSolicitudTipoNoCoincide_cuandoLaSolicitudEsDeOtroTipo() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var entrada = EliminacionRespuestaNovedadCoordinadorDomain.crear(solicitud, coordinador);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, coordinador, TipoSolicitud.CAMBIO_DE_ASESOR.getId()),
                new ResumenRespuesta(solicitud, ESTADO_OK)))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeLanzarSolicitudNoEsDestinatario_cuandoElCoordinadorNoEsElDestinatario() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var entrada = EliminacionRespuestaNovedadCoordinadorDomain.crear(solicitud, UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, UUID.randomUUID(), TIPO_OK),
                new ResumenRespuesta(solicitud, ESTADO_OK)))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }

    @Test
    void debeLanzarRespuestaNoEncontrada_cuandoNoExisteRespuestaParaLaSolicitud() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var entrada = EliminacionRespuestaNovedadCoordinadorDomain.crear(solicitud, coordinador);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, coordinador, TIPO_OK), ResumenRespuesta.VACIO))
                .isInstanceOf(RespuestaNoEncontradaException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @Test
    void debeLanzarRespuestaNoEnRevision_cuandoElEstadoNoEsEnRevision() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var entrada = EliminacionRespuestaNovedadCoordinadorDomain.crear(solicitud, coordinador);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, coordinador, TIPO_OK),
                new ResumenRespuesta(solicitud, "APROBADA")))
                .isInstanceOf(RespuestaNoEnRevisionException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @Test
    void debeLanzarSolicitudNoEncontrada_cuandoFallanLaExistenciaYElTipo() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var entrada = EliminacionRespuestaNovedadCoordinadorDomain.crear(solicitud, UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                ResumenSolicitud.VACIO, new ResumenRespuesta(solicitud, ESTADO_OK)))
                .isInstanceOf(SolicitudNoEncontradaException.class);
    }

    @Test
    void debeLanzarSolicitudTipoNoCoincide_cuandoFallanElTipoYElDestinatario() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var entrada = EliminacionRespuestaNovedadCoordinadorDomain.crear(solicitud, UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, UUID.randomUUID(), TipoSolicitud.CAMBIO_DE_ASESOR.getId()),
                new ResumenRespuesta(solicitud, ESTADO_OK)))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeLanzarSolicitudNoEsDestinatario_cuandoFallanElDestinatarioYLaExistenciaDeLaRespuesta() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var entrada = EliminacionRespuestaNovedadCoordinadorDomain.crear(solicitud, UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, UUID.randomUUID(), TIPO_OK), ResumenRespuesta.VACIO))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }

    @Test
    void debeLanzarRespuestaNoEncontrada_cuandoFallanLaExistenciaDeLaRespuestaYElEstado() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var entrada = EliminacionRespuestaNovedadCoordinadorDomain.crear(solicitud, coordinador);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, coordinador, TIPO_OK), ResumenRespuesta.VACIO))
                .isInstanceOf(RespuestaNoEncontradaException.class);
    }
}
