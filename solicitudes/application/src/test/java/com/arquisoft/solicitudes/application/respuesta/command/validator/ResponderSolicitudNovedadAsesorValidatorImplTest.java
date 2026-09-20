package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.application.respuesta.command.validator.impl.ResponderSolicitudNovedadAsesorValidatorImpl;
import com.arquisoft.solicitudes.domain.respuesta.exception.SolicitudYaRespondidaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEsDestinatarioException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideException;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResponderSolicitudNovedadAsesorValidatorImplTest {

    private final ResponderSolicitudNovedadAsesorValidatorImpl validator =
            new ResponderSolicitudNovedadAsesorValidatorImpl();

    private static final String TIPO_OK = TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId();

    @Test
    void debePasar_cuandoLasCuatroReglasSeCumplen() {
        UUID solicitud = UUID.randomUUID();
        UUID asesor = UUID.randomUUID();

        assertThatCode(() -> validator.validar(
                solicitud, true, TIPO_OK, asesor, asesor, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudNoEncontrada_cuandoLaSolicitudNoExiste() {
        UUID asesor = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), false, TIPO_OK, asesor, asesor, false))
                .isInstanceOf(SolicitudNoEncontradaException.class);
    }

    @Test
    void debeLanzarTipoNoCoincide_cuandoLaSolicitudEsDeOtroTipo() {
        UUID asesor = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), true, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId(),
                asesor, asesor, false))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @Test
    void debeLanzarNoEsDestinatario_cuandoElAsesorNoEsElDestinatario() {
        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), true, TIPO_OK, UUID.randomUUID(), UUID.randomUUID(), false))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }

    @Test
    void debeLanzarYaRespondida_cuandoLaSolicitudTieneRespuesta() {
        UUID asesor = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), true, TIPO_OK, asesor, asesor, true))
                .isInstanceOf(SolicitudYaRespondidaException.class);
    }

    @Test
    void debeValidarExistenciaAntesQueTipo_cuandoAmbasFallan() {
        UUID asesor = UUID.randomUUID();

        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), false, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId(),
                asesor, asesor, true))
                .isInstanceOf(SolicitudNoEncontradaException.class);
    }

    @Test
    void debeValidarTipoAntesQueDestinatario_cuandoAmbosFallan() {
        assertThatThrownBy(() -> validator.validar(
                UUID.randomUUID(), true, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId(),
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
