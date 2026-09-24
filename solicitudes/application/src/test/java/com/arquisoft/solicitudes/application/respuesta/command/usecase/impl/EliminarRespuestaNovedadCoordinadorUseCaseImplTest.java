package com.arquisoft.solicitudes.application.respuesta.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.solicitudes.application.respuesta.command.finder.DatosRespuestaFinder;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.respuesta.command.validator.EliminarRespuestaNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEnRevisionException;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEncontradaException;
import com.arquisoft.solicitudes.domain.respuesta.model.ResumenRespuesta;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEsDestinatarioException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideException;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarRespuestaNovedadCoordinadorUseCaseImplTest {

    @Mock private DatosSolicitudFinder datosSolicitudFinder;
    @Mock private DatosRespuestaFinder datosRespuestaFinder;
    @Mock private RespuestaOutputPort respuestaOutputPort;
    @Mock private EliminarRespuestaNovedadCoordinadorValidator validator;
    @Mock private AppLogger logger;

    private EliminarRespuestaNovedadCoordinadorUseCaseImpl useCase;

    private UUID solicitud;
    private UUID coordinadorUsuario;
    private EliminacionRespuestaNovedadCoordinadorDomain entrada;

    @BeforeEach
    void setUp() {
        useCase = new EliminarRespuestaNovedadCoordinadorUseCaseImpl(
                datosSolicitudFinder, datosRespuestaFinder, respuestaOutputPort,
                validator, logger);

        solicitud = UUID.randomUUID();
        coordinadorUsuario = UUID.randomUUID();
        entrada = EliminacionRespuestaNovedadCoordinadorDomain.crear(solicitud, coordinadorUsuario);
    }

    private void stubFlujoValido() {
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(new ResumenSolicitud(
                solicitud, UUID.randomUUID(), coordinadorUsuario,
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId()));
        when(datosRespuestaFinder.obtener(solicitud))
                .thenReturn(new ResumenRespuesta(solicitud, "EN_REVISION"));
    }

    @Test
    void debeEliminarConLosLogsEnOrden_cuandoElFlujoEsValido() {
        // Arrange
        stubFlujoValido();

        // Act
        useCase.ejecutar(entrada);

        // Assert
        verify(respuestaOutputPort).eliminarPorSolicitud(solicitud);

        verify(logger).info(eq(RespuestaKey.LOG_ELIMINANDO), eq(solicitud), eq(coordinadorUsuario));
        verify(logger).debug(eq(RespuestaKey.LOG_VERIFICACION_ELIMINACION), eq(true), eq(true));
        verify(logger).info(eq(RespuestaKey.LOG_ELIMINADA), eq(solicitud));

        var inOrder = inOrder(datosSolicitudFinder, datosRespuestaFinder,
                validator, respuestaOutputPort);
        inOrder.verify(datosSolicitudFinder).obtener(solicitud);
        inOrder.verify(datosRespuestaFinder).obtener(solicitud);
        inOrder.verify(validator).validar(eq(solicitud), eq(true),
                eq(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId()), eq(coordinadorUsuario),
                eq(coordinadorUsuario), eq(true), eq("EN_REVISION"));
        inOrder.verify(respuestaOutputPort).eliminarPorSolicitud(solicitud);
    }

    @Test
    void debeAbortarSinEliminar_cuandoLaSolicitudNoExiste() {
        // Arrange
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(ResumenSolicitud.VACIO);
        when(datosRespuestaFinder.obtener(solicitud)).thenReturn(ResumenRespuesta.VACIO);
        doThrow(new SolicitudNoEncontradaException(solicitud))
                .when(validator).validar(any(), anyBoolean(), any(), any(), any(), anyBoolean(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(SolicitudNoEncontradaException.class);

        verify(validator).validar(eq(solicitud), eq(false), eq(ResumenSolicitud.VACIO.tipoSolicitud()),
                eq(ResumenSolicitud.VACIO.destinatarioUsuario()), eq(coordinadorUsuario),
                eq(false), eq(ResumenRespuesta.VACIO.estado()));
        verify(respuestaOutputPort, never()).eliminarPorSolicitud(any());
    }

    static Stream<RuntimeException> excepcionesDelValidator() {
        return Stream.of(
                new SolicitudTipoNoCoincideException(UUID.randomUUID()),
                new SolicitudNoEsDestinatarioException(UUID.randomUUID()),
                new RespuestaNoEncontradaException(UUID.randomUUID()),
                new RespuestaNoEnRevisionException(UUID.randomUUID()));
    }

    @ParameterizedTest
    @MethodSource("excepcionesDelValidator")
    void debeAbortarSinEliminar_cuandoElValidatorRechazaPorReglaDeNegocio(RuntimeException excepcion) {
        // Arrange
        stubFlujoValido();
        doThrow(excepcion)
                .when(validator).validar(any(), anyBoolean(), any(), any(), any(), anyBoolean(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada)).isSameAs(excepcion);

        verify(respuestaOutputPort, never()).eliminarPorSolicitud(any());
    }
}
