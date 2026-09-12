package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.application.respuesta.command.validator.impl.ResponderSolicitudNovedadCoordinadorValidatorImpl;
import com.arquisoft.solicitudes.domain.respuesta.exception.SolicitudYaRespondidaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEsDestinatarioException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideException;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResponderSolicitudNovedadCoordinadorValidatorImplTest {

    private final ResponderSolicitudNovedadCoordinadorValidatorImpl validator =
            new ResponderSolicitudNovedadCoordinadorValidatorImpl();

    private static final String TIPO_OK = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId();

    @Test
    void debePasar_cuandoLasCuatroReglasSeCumplen() {
        UUID solicitud = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        assertThatCode(() -> validator.validar(
                solicitud, true, TIPO_OK, coordinador, coordinador, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudNoEncontrada_cuandoLaSolicitudNoExiste() {
        UUID coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), false, TIPO_OK, coordinador, coordinador, false))
                .isInstanceOf(SolicitudNoEncontradaException.class);
    }

    @Test
    void debeLanzarTipoNoCoincide_cuandoLaSolicitudEsDeOtroTipo() {
        UUID coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), true, TipoSolicitud.CAMBIO_DE_ASESOR.getId(),
                coordinador, coordinador, false))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeLanzarNoEsDestinatario_cuandoElCoordinadorNoEsElDestinatario() {
        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), true, TIPO_OK, UUID.randomUUID(), UUID.randomUUID(), false))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }

    @Test
    void debeLanzarYaRespondida_cuandoLaSolicitudTieneRespuesta() {
        UUID coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), true, TIPO_OK, coordinador, coordinador, true))
                .isInstanceOf(SolicitudYaRespondidaException.class);
    }

    @Test
    void debeValidarExistenciaAntesQueTipo_cuandoAmbasFallan() {
        UUID coordinador = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), false, TipoSolicitud.CAMBIO_DE_ASESOR.getId(),
                coordinador, coordinador, true))
                .isInstanceOf(SolicitudNoEncontradaException.class);
    }

    @Test
    void debeValidarTipoAntesQueDestinatario_cuandoAmbosFallan() {
        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), true, TipoSolicitud.CAMBIO_DE_ASESOR.getId(),
                UUID.randomUUID(), UUID.randomUUID(), true))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeValidarDestinatarioAntesQueUnicidad_cuandoAmbosFallan() {
        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), true, TIPO_OK, UUID.randomUUID(), UUID.randomUUID(), true))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }
}
