package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.application.respuesta.command.validator.impl.ResponderSolicitudValidatorImpl;
import com.arquisoft.solicitudes.domain.destinatario.exception.DestinatarioNoEncontradoException;
import com.arquisoft.solicitudes.domain.remitente.exception.RemitenteNoEncontradoException;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaDomain;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaSolicitudDomain;
import com.arquisoft.solicitudes.domain.respuesta.exception.SolicitudYaRespondidaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEsDestinatarioException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideAsesorException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideException;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResponderSolicitudValidatorImplTest {

    private final ResponderSolicitudValidatorImpl validator = new ResponderSolicitudValidatorImpl();

    private static final UUID REMITENTE = UUID.randomUUID();
    private static final String TIPO_COORDINADOR = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId();

    private static ResumenSolicitud resumen(boolean existe, String tipo, UUID remitente, UUID destinatario) {
        return existe ? new ResumenSolicitud(UUID.randomUUID(), remitente, destinatario, tipo)
                : ResumenSolicitud.VACIO;
    }

    private static RespuestaSolicitudDomain entrada(UUID responsable, TipoSolicitud esperado) {
        return RespuestaSolicitudDomain.crear(
                RespuestaDomain.crear(UUID.randomUUID(), "contenido"), responsable, esperado);
    }

    private static RespuestaSolicitudDomain entradaCoordinador(UUID responsable) {
        return entrada(responsable, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);
    }

    private static UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "EST-1", "Nombre", "correo@uco.edu.co", Instant.now());
    }

    @Test
    void debePasarValidarExistencia_cuandoSolicitudRemitenteYCoordinadorExisten() {
        var remitente = UUID.randomUUID();
        var coordinador = UUID.randomUUID();

        assertThatCode(() -> validator.validar(entradaCoordinador(coordinador),
                resumen(true, TIPO_COORDINADOR, remitente, coordinador),
                usuario(remitente), usuario(coordinador), false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudNoEncontrada_cuandoLaSolicitudNoExiste() {
        var coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(entradaCoordinador(coordinador),
                resumen(false, TIPO_COORDINADOR, REMITENTE, coordinador),
                UsuarioDomain.VACIO, UsuarioDomain.VACIO, false))
                .isInstanceOf(SolicitudNoEncontradaException.class);
    }

    @Test
    void debeLanzarRemitenteNoEncontrado_cuandoElRemitenteNoTieneReplicaLocal() {
        var coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(entradaCoordinador(coordinador),
                resumen(true, TIPO_COORDINADOR, REMITENTE, coordinador),
                UsuarioDomain.VACIO, usuario(coordinador), false))
                .isInstanceOf(RemitenteNoEncontradoException.class);
    }

    @Test
    void debeLanzarDestinatarioNoEncontrado_cuandoElCoordinadorNoTieneReplicaLocal() {
        var coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(entradaCoordinador(coordinador),
                resumen(true, TIPO_COORDINADOR, REMITENTE, coordinador),
                usuario(REMITENTE), UsuarioDomain.VACIO, false))
                .isInstanceOf(DestinatarioNoEncontradoException.class);
    }

    @Test
    void debeValidarSolicitudAntesQueRemitente_cuandoAmbasFallan() {
        var coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(entradaCoordinador(coordinador),
                resumen(false, TIPO_COORDINADOR, REMITENTE, coordinador),
                UsuarioDomain.VACIO, UsuarioDomain.VACIO, false))
                .isInstanceOf(SolicitudNoEncontradaException.class);
    }

    @Test
    void debeValidarRemitenteAntesQueDestinatario_cuandoAmbosFallan() {
        var coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(entradaCoordinador(coordinador),
                resumen(true, TIPO_COORDINADOR, REMITENTE, coordinador),
                UsuarioDomain.VACIO, UsuarioDomain.VACIO, false))
                .isInstanceOf(RemitenteNoEncontradoException.class);
    }

    @Test
    void debePasarValidarReglasDeNegocio_cuandoLasTresReglasSeCumplen() {
        var coordinador = UUID.randomUUID();

        assertThatCode(() -> validator.validar(entradaCoordinador(coordinador),
                resumen(true, TIPO_COORDINADOR, REMITENTE, coordinador),
                usuario(REMITENTE), usuario(coordinador), false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarTipoNoCoincide_cuandoLaSolicitudEsDeOtroTipo() {
        var coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(entradaCoordinador(coordinador),
                resumen(true, TipoSolicitud.CAMBIO_DE_ASESOR.getId(), REMITENTE, coordinador),
                usuario(REMITENTE), usuario(coordinador), false))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeLanzarNoEsDestinatario_cuandoElResponsableNoEsElDestinatario() {
        var destinatario = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(entradaCoordinador(UUID.randomUUID()),
                resumen(true, TIPO_COORDINADOR, REMITENTE, destinatario),
                usuario(REMITENTE), usuario(destinatario), false))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }

    @Test
    void debeLanzarYaRespondida_cuandoLaSolicitudTieneRespuesta() {
        var coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(entradaCoordinador(coordinador),
                resumen(true, TIPO_COORDINADOR, REMITENTE, coordinador),
                usuario(REMITENTE), usuario(coordinador), true))
                .isInstanceOf(SolicitudYaRespondidaException.class);
    }

    @Test
    void debeValidarTipoAntesQueDestinatario_cuandoAmbosFallan() {
        var destinatario = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(entradaCoordinador(UUID.randomUUID()),
                resumen(true, TipoSolicitud.CAMBIO_DE_ASESOR.getId(), REMITENTE, destinatario),
                usuario(REMITENTE), usuario(destinatario), true))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeValidarDestinatarioAntesQueUnicidad_cuandoAmbosFallan() {
        var destinatario = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(entradaCoordinador(UUID.randomUUID()),
                resumen(true, TIPO_COORDINADOR, REMITENTE, destinatario),
                usuario(REMITENTE), usuario(destinatario), true))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }

    @Test
    void debeLanzarElErrorDelAsesor_cuandoSeEsperabaNovedadParaElAsesorYLaSolicitudEsDeOtroTipo() {
        var responsable = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(entrada(responsable, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR),
                resumen(true, TipoSolicitud.CAMBIO_DE_ASESOR.getId(), REMITENTE, responsable),
                usuario(REMITENTE), usuario(responsable), false))
                .isInstanceOf(SolicitudTipoNoCoincideAsesorException.class);
    }

    @Test
    void debePasar_cuandoSeEsperabaNovedadParaElAsesorYLaSolicitudEsDeEseTipo() {
        var responsable = UUID.randomUUID();

        assertThatCode(() -> validator.validar(entrada(responsable, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR),
                resumen(true, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId(), REMITENTE, responsable),
                usuario(REMITENTE), usuario(responsable), false))
                .doesNotThrowAnyException();
    }
}
