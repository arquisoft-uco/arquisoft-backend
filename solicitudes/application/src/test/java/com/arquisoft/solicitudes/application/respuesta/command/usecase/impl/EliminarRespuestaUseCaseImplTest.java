package com.arquisoft.solicitudes.application.respuesta.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.solicitudes.application.respuesta.command.finder.DatosRespuestaFinder;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.respuesta.command.validator.EliminarRespuestaValidator;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarRespuestaUseCaseImplTest {

    @Mock private DatosSolicitudFinder datosSolicitudFinder;
    @Mock private DatosRespuestaFinder datosRespuestaFinder;
    @Mock private RespuestaOutputPort respuestaOutputPort;
    @Mock private EliminarRespuestaValidator validator;
    @Mock private AppLogger logger;

    private EliminarRespuestaUseCaseImpl useCase;

    private UUID solicitud;
    private UUID responsableUsuario;

    @BeforeEach
    void setUp() {
        useCase = new EliminarRespuestaUseCaseImpl(
                datosSolicitudFinder, datosRespuestaFinder, respuestaOutputPort,
                validator, logger);

        solicitud = UUID.randomUUID();
        responsableUsuario = UUID.randomUUID();
    }

    private ResumenSolicitud resumenSolicitud(TipoSolicitud tipo) {
        return new ResumenSolicitud(solicitud, UUID.randomUUID(), responsableUsuario, tipo.getId());
    }

    private ResumenRespuesta resumenRespuesta() {
        return new ResumenRespuesta(solicitud, "EN_REVISION");
    }

    @ParameterizedTest
    @EnumSource(value = TipoSolicitud.class,
            names = {"NOVEDAD_PARA_EL_ASESOR", "NOVEDAD_PARA_EL_COORDINADOR"})
    void debeEliminarConLosLogsEnOrden_cuandoElFlujoEsValido(TipoSolicitud tipo) {
        // Arrange
        var entrada = EliminacionRespuestaDomain.crear(solicitud, responsableUsuario, tipo);
        var resumenSolicitud = resumenSolicitud(tipo);
        var resumenRespuesta = resumenRespuesta();
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(resumenSolicitud);
        when(datosRespuestaFinder.obtener(solicitud)).thenReturn(resumenRespuesta);

        // Act
        useCase.ejecutar(entrada);

        // Assert
        verify(respuestaOutputPort).eliminarPorSolicitud(solicitud);

        verify(logger).info(eq(RespuestaKey.LOG_ELIMINANDO), eq(tipo.getId()), eq(solicitud),
                eq(responsableUsuario));
        verify(logger).debug(eq(RespuestaKey.LOG_VERIFICACION_ELIMINACION), eq(true), eq(true));
        verify(logger).info(eq(RespuestaKey.LOG_ELIMINADA), eq(tipo.getId()), eq(solicitud));

        var inOrder = inOrder(datosSolicitudFinder, datosRespuestaFinder,
                validator, respuestaOutputPort);
        inOrder.verify(datosSolicitudFinder).obtener(solicitud);
        inOrder.verify(datosRespuestaFinder).obtener(solicitud);
        inOrder.verify(validator).validar(entrada, resumenSolicitud, resumenRespuesta);
        inOrder.verify(respuestaOutputPort).eliminarPorSolicitud(solicitud);
    }

    @Test
    void debeAbortarSinEliminar_cuandoLaSolicitudNoExiste() {
        // Arrange
        var entrada = EliminacionRespuestaDomain.crear(
                solicitud, responsableUsuario, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR);
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(ResumenSolicitud.VACIO);
        when(datosRespuestaFinder.obtener(solicitud)).thenReturn(ResumenRespuesta.VACIO);
        doThrow(new SolicitudNoEncontradaException(solicitud))
                .when(validator).validar(entrada, ResumenSolicitud.VACIO, ResumenRespuesta.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(SolicitudNoEncontradaException.class);

        verify(logger).debug(eq(RespuestaKey.LOG_VERIFICACION_ELIMINACION), eq(false), eq(false));
        verify(respuestaOutputPort, never()).eliminarPorSolicitud(any());
    }

    static Stream<RuntimeException> excepcionesDelValidator() {
        return Stream.of(
                new SolicitudTipoNoCoincideAsesorException(UUID.randomUUID()),
                new SolicitudTipoNoCoincideException(UUID.randomUUID()),
                new SolicitudNoEsDestinatarioException(UUID.randomUUID()),
                new RespuestaNoEncontradaException(UUID.randomUUID()),
                new RespuestaNoEnRevisionException(UUID.randomUUID()));
    }

    @ParameterizedTest
    @MethodSource("excepcionesDelValidator")
    void debeAbortarSinEliminar_cuandoElValidatorRechazaPorReglaDeNegocio(RuntimeException excepcion) {
        // Arrange
        var tipo = TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR;
        var entrada = EliminacionRespuestaDomain.crear(solicitud, responsableUsuario, tipo);
        var resumenSolicitud = resumenSolicitud(tipo);
        var resumenRespuesta = resumenRespuesta();
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(resumenSolicitud);
        when(datosRespuestaFinder.obtener(solicitud)).thenReturn(resumenRespuesta);
        doThrow(excepcion).when(validator).validar(entrada, resumenSolicitud, resumenRespuesta);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada)).isSameAs(excepcion);

        verify(respuestaOutputPort, never()).eliminarPorSolicitud(any());
    }
}
