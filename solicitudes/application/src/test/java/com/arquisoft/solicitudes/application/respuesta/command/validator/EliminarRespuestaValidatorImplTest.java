package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.application.respuesta.command.validator.impl.EliminarRespuestaValidatorImpl;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaDomain;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEnRevisionException;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEncontradaException;
import com.arquisoft.solicitudes.domain.respuesta.model.ResumenRespuesta;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEsDestinatarioException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideAsesorException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideException;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminarRespuestaValidatorImplTest {

    private static final String ESTADO_OK = "EN_REVISION";

    private final EliminarRespuestaValidatorImpl validator = new EliminarRespuestaValidatorImpl();

    private static ResumenSolicitud resumenSolicitud(UUID solicitud, UUID destinatario, String tipo) {
        return new ResumenSolicitud(solicitud, UUID.randomUUID(), destinatario, tipo);
    }

    @ParameterizedTest
    @EnumSource(value = TipoSolicitud.class,
            names = {"NOVEDAD_PARA_EL_ASESOR", "NOVEDAD_PARA_EL_COORDINADOR"})
    void debePasar_cuandoLasCincoReglasSeCumplen(TipoSolicitud tipo) {
        // Arrange
        var solicitud = UUID.randomUUID();
        var responsable = UUID.randomUUID();
        var entrada = EliminacionRespuestaDomain.crear(solicitud, responsable, tipo);

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, responsable, tipo.getId()),
                new ResumenRespuesta(solicitud, ESTADO_OK)))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = TipoSolicitud.class,
            names = {"NOVEDAD_PARA_EL_ASESOR", "NOVEDAD_PARA_EL_COORDINADOR"})
    void debeLanzarSolicitudNoEncontrada_cuandoLaSolicitudNoExiste(TipoSolicitud tipo) {
        // Arrange
        var solicitud = UUID.randomUUID();
        var entrada = EliminacionRespuestaDomain.crear(solicitud, UUID.randomUUID(), tipo);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                ResumenSolicitud.VACIO, new ResumenRespuesta(solicitud, ESTADO_OK)))
                .isInstanceOf(SolicitudNoEncontradaException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @Test
    void debeLanzarSolicitudTipoNoCoincideAsesor_cuandoSeEliminaComoAsesorUnaSolicitudDeOtroTipo() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var asesor = UUID.randomUUID();
        var entrada = EliminacionRespuestaDomain.crear(
                solicitud, asesor, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, asesor, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId()),
                new ResumenRespuesta(solicitud, ESTADO_OK)))
                .isInstanceOf(SolicitudTipoNoCoincideAsesorException.class);
    }

    @Test
    void debeLanzarSolicitudTipoNoCoincide_cuandoSeEliminaComoCoordinadorUnaSolicitudDeOtroTipo() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var entrada = EliminacionRespuestaDomain.crear(
                solicitud, coordinador, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, coordinador, TipoSolicitud.CAMBIO_DE_ASESOR.getId()),
                new ResumenRespuesta(solicitud, ESTADO_OK)))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);
    }

    @ParameterizedTest
    @EnumSource(value = TipoSolicitud.class,
            names = {"NOVEDAD_PARA_EL_ASESOR", "NOVEDAD_PARA_EL_COORDINADOR"})
    void debeLanzarSolicitudNoEsDestinatario_cuandoElResponsableNoEsElDestinatario(TipoSolicitud tipo) {
        // Arrange
        var solicitud = UUID.randomUUID();
        var entrada = EliminacionRespuestaDomain.crear(solicitud, UUID.randomUUID(), tipo);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, UUID.randomUUID(), tipo.getId()),
                new ResumenRespuesta(solicitud, ESTADO_OK)))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }

    @ParameterizedTest
    @EnumSource(value = TipoSolicitud.class,
            names = {"NOVEDAD_PARA_EL_ASESOR", "NOVEDAD_PARA_EL_COORDINADOR"})
    void debeLanzarRespuestaNoEncontrada_cuandoNoExisteRespuestaParaLaSolicitud(TipoSolicitud tipo) {
        // Arrange
        var solicitud = UUID.randomUUID();
        var responsable = UUID.randomUUID();
        var entrada = EliminacionRespuestaDomain.crear(solicitud, responsable, tipo);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, responsable, tipo.getId()), ResumenRespuesta.VACIO))
                .isInstanceOf(RespuestaNoEncontradaException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"APROBADA", "NO_APROBADA"})
    void debeLanzarRespuestaNoEnRevision_cuandoElEstadoNoEsEnRevision(String estado) {
        // Arrange
        var solicitud = UUID.randomUUID();
        var responsable = UUID.randomUUID();
        var tipo = TipoSolicitud.NOVEDAD_PARA_EL_ASESOR;
        var entrada = EliminacionRespuestaDomain.crear(solicitud, responsable, tipo);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, responsable, tipo.getId()),
                new ResumenRespuesta(solicitud, estado)))
                .isInstanceOf(RespuestaNoEnRevisionException.class)
                .hasMessageContaining(solicitud.toString());
    }

    @Test
    void debeLanzarSolicitudNoEncontrada_cuandoFallanLaExistenciaYElTipo() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var entrada = EliminacionRespuestaDomain.crear(
                solicitud, UUID.randomUUID(), TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                ResumenSolicitud.VACIO, new ResumenRespuesta(solicitud, ESTADO_OK)))
                .isInstanceOf(SolicitudNoEncontradaException.class);
    }

    @Test
    void debeLanzarSolicitudTipoNoCoincide_cuandoFallanElTipoYElDestinatario() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var entrada = EliminacionRespuestaDomain.crear(
                solicitud, UUID.randomUUID(), TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);

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
        var tipo = TipoSolicitud.NOVEDAD_PARA_EL_ASESOR;
        var entrada = EliminacionRespuestaDomain.crear(solicitud, UUID.randomUUID(), tipo);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, UUID.randomUUID(), tipo.getId()), ResumenRespuesta.VACIO))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);
    }

    @Test
    void debeLanzarRespuestaNoEncontrada_cuandoFallanLaExistenciaDeLaRespuestaYElEstado() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var responsable = UUID.randomUUID();
        var tipo = TipoSolicitud.NOVEDAD_PARA_EL_ASESOR;
        var entrada = EliminacionRespuestaDomain.crear(solicitud, responsable, tipo);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada,
                resumenSolicitud(solicitud, responsable, tipo.getId()), ResumenRespuesta.VACIO))
                .isInstanceOf(RespuestaNoEncontradaException.class);
    }
}
