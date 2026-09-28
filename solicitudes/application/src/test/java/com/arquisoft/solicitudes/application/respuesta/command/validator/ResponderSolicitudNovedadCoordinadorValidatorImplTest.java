package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.application.respuesta.command.validator.impl.ResponderSolicitudNovedadCoordinadorValidatorImpl;
import com.arquisoft.solicitudes.domain.respuesta.exception.SolicitudYaRespondidaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.DestinatarioNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.exception.RemitenteNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEsDestinatarioException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideException;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResponderSolicitudNovedadCoordinadorValidatorImplTest {

    private final ResponderSolicitudNovedadCoordinadorValidatorImpl validator =
            new ResponderSolicitudNovedadCoordinadorValidatorImpl();

    private static final String TIPO_OK = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId();

    private static UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "EST-1", "Nombre", "correo@uco.edu.co", Instant.now());
    }

    @Test
    void debePasarValidarExistencia_cuandoSolicitudRemitenteYCoordinadorExisten() {
        UUID solicitud = UUID.randomUUID();
        UUID remitente = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        assertThatCode(() -> validator.validarExistencia(
                solicitud, true, remitente, usuario(remitente), coordinador, usuario(coordinador)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudNoEncontrada_cuandoLaSolicitudNoExiste() {
        UUID remitente = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validarExistencia(UUID.randomUUID(), false,
                remitente, UsuarioDomain.VACIO, coordinador, UsuarioDomain.VACIO))
                .isInstanceOf(SolicitudNoEncontradaException.class);
    }

    @Test
    void debeLanzarRemitenteNoEncontrado_cuandoElRemitenteNoTieneReplicaLocal() {
        UUID remitente = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validarExistencia(UUID.randomUUID(), true,
                remitente, UsuarioDomain.VACIO, coordinador, usuario(coordinador)))
                .isInstanceOf(RemitenteNoEncontradoException.class);
    }

    @Test
    void debeLanzarDestinatarioNoEncontrado_cuandoElCoordinadorNoTieneReplicaLocal() {
        UUID remitente = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validarExistencia(UUID.randomUUID(), true,
                remitente, usuario(remitente), coordinador, UsuarioDomain.VACIO))
                .isInstanceOf(DestinatarioNoEncontradoException.class);
    }

    @Test
    void debeValidarSolicitudAntesQueRemitente_cuandoAmbasFallan() {
        assertThatThrownBy(() -> validator.validarExistencia(UUID.randomUUID(), false,
                UUID.randomUUID(), UsuarioDomain.VACIO, UUID.randomUUID(), UsuarioDomain.VACIO))
                .isInstanceOf(SolicitudNoEncontradaException.class);
    }

    @Test
    void debeValidarRemitenteAntesQueDestinatario_cuandoAmbosFallan() {
        assertThatThrownBy(() -> validator.validarExistencia(UUID.randomUUID(), true,
                UUID.randomUUID(), UsuarioDomain.VACIO, UUID.randomUUID(), UsuarioDomain.VACIO))
                .isInstanceOf(RemitenteNoEncontradoException.class);
    }

    @Test
    void debePasarValidarReglasDeNegocio_cuandoLasTresReglasSeCumplen() {
        UUID solicitud = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        assertThatCode(() -> validator.validarReglasDeNegocio(
                solicitud, TIPO_OK, coordinador, coordinador, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarTipoNoCoincide_cuandoLaSolicitudEsDeOtroTipo() {
        UUID coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validarReglasDeNegocio(UUID.randomUUID(),
                TipoSolicitud.CAMBIO_DE_ASESOR.getId(), coordinador, coordinador, false))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeLanzarNoEsDestinatario_cuandoElCoordinadorNoEsElDestinatario() {
        assertThatThrownBy(() -> validator.validarReglasDeNegocio(
                UUID.randomUUID(), TIPO_OK, UUID.randomUUID(), UUID.randomUUID(), false))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }

    @Test
    void debeLanzarYaRespondida_cuandoLaSolicitudTieneRespuesta() {
        UUID coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validarReglasDeNegocio(
                UUID.randomUUID(), TIPO_OK, coordinador, coordinador, true))
                .isInstanceOf(SolicitudYaRespondidaException.class);
    }

    @Test
    void debeValidarTipoAntesQueDestinatario_cuandoAmbosFallan() {
        assertThatThrownBy(() -> validator.validarReglasDeNegocio(UUID.randomUUID(),
                TipoSolicitud.CAMBIO_DE_ASESOR.getId(), UUID.randomUUID(), UUID.randomUUID(), true))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeValidarDestinatarioAntesQueUnicidad_cuandoAmbosFallan() {
        assertThatThrownBy(() -> validator.validarReglasDeNegocio(
                UUID.randomUUID(), TIPO_OK, UUID.randomUUID(), UUID.randomUUID(), true))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }
}
