package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.application.respuesta.command.validator.impl.ResponderSolicitudNovedadCoordinadorValidatorImpl;
import com.arquisoft.solicitudes.domain.respuesta.exception.SolicitudYaRespondidaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.DestinatarioNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.exception.RemitenteNoEncontradoException;
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

class ResponderSolicitudNovedadCoordinadorValidatorImplTest {

    private final ResponderSolicitudNovedadCoordinadorValidatorImpl validator =
            new ResponderSolicitudNovedadCoordinadorValidatorImpl();

    private static final UUID REMITENTE = UUID.randomUUID();
    private static final String TIPO_OK = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId();

    private static ResumenSolicitud resumen(boolean existe, String tipo, UUID remitente, UUID destinatario) {
        return existe ? new ResumenSolicitud(UUID.randomUUID(), remitente, destinatario, tipo)
                : ResumenSolicitud.VACIO;
    }

    private static UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "EST-1", "Nombre", "correo@uco.edu.co", Instant.now());
    }

    @Test
    void debePasarValidarExistencia_cuandoSolicitudRemitenteYCoordinadorExisten() {
        UUID solicitud = UUID.randomUUID();
        UUID remitente = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        assertThatCode(() -> validator.validar(solicitud, resumen(true, TIPO_OK, remitente, coordinador),
                usuario(remitente), usuario(coordinador), coordinador, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudNoEncontrada_cuandoLaSolicitudNoExiste() {
        UUID remitente = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(UUID.randomUUID(), resumen(false, TIPO_OK, remitente, coordinador),
                UsuarioDomain.VACIO, UsuarioDomain.VACIO, coordinador, false))
                .isInstanceOf(SolicitudNoEncontradaException.class);
    }

    @Test
    void debeLanzarRemitenteNoEncontrado_cuandoElRemitenteNoTieneReplicaLocal() {
        UUID remitente = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(UUID.randomUUID(), resumen(true, TIPO_OK, remitente, coordinador),
                UsuarioDomain.VACIO, usuario(coordinador), coordinador, false))
                .isInstanceOf(RemitenteNoEncontradoException.class);
    }

    @Test
    void debeLanzarDestinatarioNoEncontrado_cuandoElCoordinadorNoTieneReplicaLocal() {
        UUID remitente = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(UUID.randomUUID(), resumen(true, TIPO_OK, remitente, coordinador),
                usuario(remitente), UsuarioDomain.VACIO, coordinador, false))
                .isInstanceOf(DestinatarioNoEncontradoException.class);
    }

    @Test
    void debeValidarSolicitudAntesQueRemitente_cuandoAmbasFallan() {
        assertThatThrownBy(() -> validator.validar(UUID.randomUUID(), resumen(false, TIPO_OK, UUID.randomUUID(), UUID.randomUUID()),
                UsuarioDomain.VACIO, UsuarioDomain.VACIO, UUID.randomUUID(), false))
                .isInstanceOf(SolicitudNoEncontradaException.class);
    }

    @Test
    void debeValidarRemitenteAntesQueDestinatario_cuandoAmbosFallan() {
        assertThatThrownBy(() -> validator.validar(UUID.randomUUID(), resumen(true, TIPO_OK, UUID.randomUUID(), UUID.randomUUID()),
                UsuarioDomain.VACIO, UsuarioDomain.VACIO, UUID.randomUUID(), false))
                .isInstanceOf(RemitenteNoEncontradoException.class);
    }

    @Test
    void debePasarValidarReglasDeNegocio_cuandoLasTresReglasSeCumplen() {
        UUID solicitud = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        assertThatCode(() -> validator.validar(solicitud, resumen(true, TIPO_OK, REMITENTE, coordinador),
                usuario(REMITENTE), usuario(coordinador), coordinador, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarTipoNoCoincide_cuandoLaSolicitudEsDeOtroTipo() {
        UUID coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(UUID.randomUUID(), resumen(true, TipoSolicitud.CAMBIO_DE_ASESOR.getId(), REMITENTE, coordinador),
                usuario(REMITENTE), usuario(coordinador), coordinador, false))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeLanzarNoEsDestinatario_cuandoElCoordinadorNoEsElDestinatario() {
        assertThatThrownBy(() -> validator.validar(UUID.randomUUID(), resumen(true, TIPO_OK, REMITENTE, UUID.randomUUID()),
                usuario(REMITENTE), usuario(UUID.randomUUID()), UUID.randomUUID(), false))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }

    @Test
    void debeLanzarYaRespondida_cuandoLaSolicitudTieneRespuesta() {
        UUID coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(UUID.randomUUID(), resumen(true, TIPO_OK, REMITENTE, coordinador),
                usuario(REMITENTE), usuario(coordinador), coordinador, true))
                .isInstanceOf(SolicitudYaRespondidaException.class);
    }

    @Test
    void debeValidarTipoAntesQueDestinatario_cuandoAmbosFallan() {
        assertThatThrownBy(() -> validator.validar(UUID.randomUUID(), resumen(true, TipoSolicitud.CAMBIO_DE_ASESOR.getId(),
                        REMITENTE, UUID.randomUUID()),
                usuario(REMITENTE), usuario(UUID.randomUUID()), UUID.randomUUID(), true))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeValidarDestinatarioAntesQueUnicidad_cuandoAmbosFallan() {
        assertThatThrownBy(() -> validator.validar(UUID.randomUUID(), resumen(true, TIPO_OK, REMITENTE, UUID.randomUUID()),
                usuario(REMITENTE), usuario(UUID.randomUUID()), UUID.randomUUID(), true))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }
}
