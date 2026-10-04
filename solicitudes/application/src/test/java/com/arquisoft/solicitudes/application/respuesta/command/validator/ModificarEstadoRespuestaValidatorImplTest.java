package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.application.respuesta.command.validator.impl.ModificarEstadoRespuestaValidatorImpl;
import com.arquisoft.solicitudes.domain.destinatario.exception.DestinatarioNoEncontradoException;
import com.arquisoft.solicitudes.domain.remitente.exception.RemitenteNoEncontradoException;
import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaDomain;
import com.arquisoft.solicitudes.domain.respuesta.exception.EstadoRespuestaNoResolutivoException;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEnRevisionException;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEncontradaException;
import com.arquisoft.solicitudes.domain.respuesta.model.ResumenRespuesta;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEsDestinatarioException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideException;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarEstadoRespuestaValidatorImplTest {

    private final ModificarEstadoRespuestaValidatorImpl validator =
            new ModificarEstadoRespuestaValidatorImpl();

    private static final String TIPO_OK = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId();
    private static final String ESTADO_ACTUAL_OK = "EN_REVISION";

    private static ModificacionEstadoRespuestaDomain entrada(
            UUID solicitud, UUID coordinador, String nuevoEstado) {
        return ModificacionEstadoRespuestaDomain.crear(
                solicitud, coordinador, nuevoEstado, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);
    }

    private static ResumenSolicitud resumenSolicitud(
            UUID solicitud, UUID remitente, UUID destinatario, String tipo) {
        return new ResumenSolicitud(solicitud, remitente, destinatario, tipo);
    }

    private static UsuarioDomain usuario(UUID id, String nombre) {
        return UsuarioDomain.reconstruir(id, "COD-1", nombre, nombre + "@uco.edu.co", Instant.now());
    }

    @Test
    void debePasar_cuandoLasOchoReglasSeCumplen() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var remitente = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var entrada = entrada(solicitud, coordinador, "APROBADA");

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, remitente, coordinador, TIPO_OK),
                new ResumenRespuesta(solicitud, ESTADO_ACTUAL_OK),
                usuario(remitente, "ana"), usuario(coordinador, "pedro")))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudNoEncontrada_cuandoLaSolicitudNoExiste() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var entrada = entrada(solicitud, UUID.randomUUID(), "APROBADA");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                ResumenSolicitud.VACIO, new ResumenRespuesta(solicitud, ESTADO_ACTUAL_OK),
                UsuarioDomain.VACIO, UsuarioDomain.VACIO))
                .isInstanceOf(SolicitudNoEncontradaException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @Test
    void debeLanzarRemitenteNoEncontrado_cuandoLaReplicaDelRemitenteEstaVacia() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var entrada = entrada(solicitud, coordinador, "APROBADA");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, UUID.randomUUID(), coordinador, TIPO_OK),
                new ResumenRespuesta(solicitud, ESTADO_ACTUAL_OK),
                UsuarioDomain.VACIO, usuario(coordinador, "pedro")))
                .isInstanceOf(RemitenteNoEncontradoException.class);
    }

    @Test
    void debeLanzarDestinatarioNoEncontrado_cuandoLaReplicaDelCoordinadorEstaVacia() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var remitente = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var entrada = entrada(solicitud, coordinador, "APROBADA");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, remitente, coordinador, TIPO_OK),
                new ResumenRespuesta(solicitud, ESTADO_ACTUAL_OK),
                usuario(remitente, "ana"), UsuarioDomain.VACIO))
                .isInstanceOf(DestinatarioNoEncontradoException.class);
    }

    @Test
    void debeLanzarSolicitudTipoNoCoincide_cuandoLaSolicitudEsDeOtroTipo() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var remitente = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var entrada = entrada(solicitud, coordinador, "APROBADA");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, remitente, coordinador, TipoSolicitud.CAMBIO_DE_ASESOR.getId()),
                new ResumenRespuesta(solicitud, ESTADO_ACTUAL_OK),
                usuario(remitente, "ana"), usuario(coordinador, "pedro")))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeLanzarSolicitudNoEsDestinatario_cuandoElCoordinadorNoEsElDestinatario() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var remitente = UUID.randomUUID();
        var destinatario = UUID.randomUUID();
        var entrada = entrada(solicitud, UUID.randomUUID(), "APROBADA");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, remitente, destinatario, TIPO_OK),
                new ResumenRespuesta(solicitud, ESTADO_ACTUAL_OK),
                usuario(remitente, "ana"), usuario(destinatario, "pedro")))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }

    @Test
    void debeLanzarRespuestaNoEncontrada_cuandoNoExisteRespuestaParaLaSolicitud() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var remitente = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var entrada = entrada(solicitud, coordinador, "APROBADA");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, remitente, coordinador, TIPO_OK),
                ResumenRespuesta.VACIO,
                usuario(remitente, "ana"), usuario(coordinador, "pedro")))
                .isInstanceOf(RespuestaNoEncontradaException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @Test
    void debeLanzarRespuestaNoEnRevision_cuandoLaRespuestaYaFueDecidida() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var remitente = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var entrada = entrada(solicitud, coordinador, "APROBADA");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, remitente, coordinador, TIPO_OK),
                new ResumenRespuesta(solicitud, "APROBADA"),
                usuario(remitente, "ana"), usuario(coordinador, "pedro")))
                .isInstanceOf(RespuestaNoEnRevisionException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @Test
    void debeLanzarEstadoRespuestaNoResolutivo_cuandoElNuevoEstadoEsEnRevision() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var remitente = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var entrada = entrada(solicitud, coordinador, "EN_REVISION");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, remitente, coordinador, TIPO_OK),
                new ResumenRespuesta(solicitud, ESTADO_ACTUAL_OK),
                usuario(remitente, "ana"), usuario(coordinador, "pedro")))
                .isInstanceOf(EstadoRespuestaNoResolutivoException.class)
                .hasMessageContaining(solicitud.toString());
    }
}
