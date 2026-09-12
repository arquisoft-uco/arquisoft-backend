package com.arquisoft.solicitudes.application.respuesta.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.RespuestaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.solicitudes.application.respuesta.command.finder.DatosRespuestaFinder;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.respuesta.command.validator.EliminarRespuestaNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.respuesta.event.SolicitudNovedadCoordinadorRespuestaEliminadaEvent;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
    @Mock private EventPublisher eventPublisher;
    @Mock private AppLogger logger;

    private EliminarRespuestaNovedadCoordinadorUseCaseImpl useCase;

    private UUID solicitud;
    private UUID coordinadorUsuario;
    private EliminacionRespuestaNovedadCoordinadorDomain entrada;

    @BeforeEach
    void setUp() {
        useCase = new EliminarRespuestaNovedadCoordinadorUseCaseImpl(
                datosSolicitudFinder, datosRespuestaFinder, respuestaOutputPort,
                validator, eventPublisher, logger);

        solicitud = UUID.randomUUID();
        coordinadorUsuario = UUID.randomUUID();
        entrada = EliminacionRespuestaNovedadCoordinadorDomain.crear(solicitud, coordinadorUsuario);
    }

    private void stubFlujoValido() {
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(Optional.of(new ResumenSolicitud(
                solicitud, UUID.randomUUID(), coordinadorUsuario,
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId())));
        when(datosRespuestaFinder.obtener(solicitud))
                .thenReturn(Optional.of(new ResumenRespuesta(solicitud, "EN_REVISION")));
    }

    @Test
    void debeEliminarYPublicarElEventoConLosLogsEnOrden_cuandoElFlujoEsValido() {
        // Arrange
        stubFlujoValido();

        // Act
        useCase.ejecutar(entrada);

        // Assert
        verify(respuestaOutputPort).eliminarPorSolicitud(solicitud);

        ArgumentCaptor<SolicitudNovedadCoordinadorRespuestaEliminadaEvent> captor =
                ArgumentCaptor.forClass(SolicitudNovedadCoordinadorRespuestaEliminadaEvent.class);
        verify(eventPublisher).publish(captor.capture());
        SolicitudNovedadCoordinadorRespuestaEliminadaEvent evento = captor.getValue();
        assertThat(evento.getSolicitudId()).isEqualTo(solicitud);
        assertThat(evento.getCoordinadorUsuario()).isEqualTo(coordinadorUsuario);
        assertThat(evento.getTipoSolicitud())
                .isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId());

        verify(logger).info(eq(RespuestaKey.LOG_ELIMINANDO), eq(solicitud), eq(coordinadorUsuario));
        verify(logger).debug(eq(RespuestaKey.LOG_VERIFICACION_ELIMINACION), eq(true), eq(true));
        verify(logger).info(eq(RespuestaKey.LOG_ELIMINADA), eq(solicitud));

        InOrder inOrder = inOrder(datosSolicitudFinder, datosRespuestaFinder,
                validator, respuestaOutputPort, eventPublisher);
        inOrder.verify(datosSolicitudFinder).obtener(solicitud);
        inOrder.verify(datosRespuestaFinder).obtener(solicitud);
        inOrder.verify(validator).validar(any(), anyBoolean(), any(), any(), any(), anyBoolean(), any());
        inOrder.verify(respuestaOutputPort).eliminarPorSolicitud(solicitud);
        inOrder.verify(eventPublisher).publish(any());
    }

    @Test
    void debeAbortarSinEliminarNiPublicar_cuandoLaSolicitudNoExiste() {
        // Arrange
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(Optional.empty());
        when(datosRespuestaFinder.obtener(solicitud)).thenReturn(Optional.empty());
        doThrow(new SolicitudNoEncontradaException(solicitud))
                .when(validator).validar(any(), anyBoolean(), any(), any(), any(), anyBoolean(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(SolicitudNoEncontradaException.class);

        verify(respuestaOutputPort, never()).eliminarPorSolicitud(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeAbortarSinEliminar_cuandoElTipoNoCoincide() {
        // Arrange
        stubFlujoValido();
        doThrow(new SolicitudTipoNoCoincideException(solicitud))
                .when(validator).validar(any(), anyBoolean(), any(), any(), any(), anyBoolean(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(SolicitudTipoNoCoincideException.class);

        verify(respuestaOutputPort, never()).eliminarPorSolicitud(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeAbortarSinEliminar_cuandoNoEsElDestinatario() {
        // Arrange
        stubFlujoValido();
        doThrow(new SolicitudNoEsDestinatarioException(solicitud))
                .when(validator).validar(any(), anyBoolean(), any(), any(), any(), anyBoolean(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(SolicitudNoEsDestinatarioException.class);

        verify(respuestaOutputPort, never()).eliminarPorSolicitud(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeAbortarSinEliminar_cuandoLaRespuestaNoExiste() {
        // Arrange
        stubFlujoValido();
        doThrow(new RespuestaNoEncontradaException(solicitud))
                .when(validator).validar(any(), anyBoolean(), any(), any(), any(), anyBoolean(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(RespuestaNoEncontradaException.class);

        verify(respuestaOutputPort, never()).eliminarPorSolicitud(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeAbortarSinEliminar_cuandoLaRespuestaNoEstaEnRevision() {
        // Arrange
        stubFlujoValido();
        doThrow(new RespuestaNoEnRevisionException(solicitud))
                .when(validator).validar(any(), anyBoolean(), any(), any(), any(), anyBoolean(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(RespuestaNoEnRevisionException.class);

        verify(respuestaOutputPort, never()).eliminarPorSolicitud(any());
        verify(eventPublisher, never()).publish(any());
    }
}
