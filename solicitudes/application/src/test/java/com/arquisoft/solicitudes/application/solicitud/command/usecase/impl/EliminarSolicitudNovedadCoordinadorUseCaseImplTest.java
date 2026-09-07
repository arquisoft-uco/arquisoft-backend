package com.arquisoft.solicitudes.application.solicitud.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudTieneRespuestasFinder;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.validator.EliminarSolicitudNovedadCoordinadorValidator;
import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudNovedadCoordinadorEliminadaEvent;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudConRespuestasException;
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
class EliminarSolicitudNovedadCoordinadorUseCaseImplTest {

    @Mock private SolicitudOutputPort solicitudOutputPort;
    @Mock private DatosSolicitudFinder datosSolicitudFinder;
    @Mock private SolicitudTieneRespuestasFinder solicitudTieneRespuestasFinder;
    @Mock private EliminarSolicitudNovedadCoordinadorValidator validator;
    @Mock private EventPublisher eventPublisher;
    @Mock private AppLogger logger;

    private EliminarSolicitudNovedadCoordinadorUseCaseImpl useCase;

    private UUID solicitud;
    private UUID remitenteUsuario;
    private EliminacionSolicitudNovedadCoordinadorDomain entrada;

    @BeforeEach
    void setUp() {
        useCase = new EliminarSolicitudNovedadCoordinadorUseCaseImpl(
                solicitudOutputPort, datosSolicitudFinder, solicitudTieneRespuestasFinder,
                validator, eventPublisher, logger);

        solicitud = UUID.randomUUID();
        remitenteUsuario = UUID.randomUUID();
        entrada = EliminacionSolicitudNovedadCoordinadorDomain.crear(solicitud, remitenteUsuario);
    }

    private void stubSolicitudPropiaSinRespuestas() {
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(Optional.of(new ResumenSolicitud(
                solicitud, remitenteUsuario, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId())));
        when(solicitudTieneRespuestasFinder.obtener(solicitud)).thenReturn(false);
    }

    @Test
    void debeEliminarYPublicarElEvento_cuandoElFlujoEsValido() {
        // Arrange
        stubSolicitudPropiaSinRespuestas();

        // Act
        useCase.ejecutar(entrada);

        // Assert
        verify(solicitudOutputPort).eliminar(solicitud);

        ArgumentCaptor<SolicitudNovedadCoordinadorEliminadaEvent> captor =
                ArgumentCaptor.forClass(SolicitudNovedadCoordinadorEliminadaEvent.class);
        verify(eventPublisher).publish(captor.capture());
        SolicitudNovedadCoordinadorEliminadaEvent evento = captor.getValue();
        assertThat(evento.getSolicitudId()).isEqualTo(solicitud);
        assertThat(evento.getRemitenteUsuario()).isEqualTo(remitenteUsuario);
        assertThat(evento.getTipoSolicitud())
                .isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId());
        assertThat(evento.getTemaEvento())
                .isEqualTo(SolicitudNovedadCoordinadorEliminadaEvent.EVENT_TOPIC);

        verify(logger).info(eq(SolicitudKey.LOG_ELIMINANDO), eq(solicitud), eq(remitenteUsuario));
        verify(logger).info(eq(SolicitudKey.LOG_ELIMINADA), eq(solicitud));
    }

    @Test
    void debeAbortarSinEliminarNiPublicar_cuandoElValidatorLanza() {
        // Arrange
        stubSolicitudPropiaSinRespuestas();
        doThrow(new SolicitudConRespuestasException(solicitud))
                .when(validator).validar(any(), anyBoolean(), any(), any(), any(), anyBoolean());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(SolicitudConRespuestasException.class);

        verify(solicitudOutputPort, never()).eliminar(any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(eq(SolicitudKey.LOG_ELIMINADA), any());
    }

    @Test
    void debePasarLosDatosProyectadosAlValidator_cuandoLaSolicitudExiste() {
        // Arrange — el remitente de la solicitud difiere del actor del JWT
        UUID remitenteDeLaFila = UUID.randomUUID();
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(Optional.of(new ResumenSolicitud(
                solicitud, remitenteDeLaFila, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId())));
        when(solicitudTieneRespuestasFinder.obtener(solicitud)).thenReturn(false);

        // Act
        useCase.ejecutar(entrada);

        // Assert
        verify(validator).validar(
                eq(solicitud), eq(true), eq(remitenteDeLaFila),
                eq(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId()), eq(remitenteUsuario), eq(false));
    }

    @Test
    void debePasarLosValoresPorDefectoAlValidator_cuandoLaSolicitudNoExiste() {
        // Arrange
        when(datosSolicitudFinder.obtener(solicitud)).thenReturn(Optional.empty());
        when(solicitudTieneRespuestasFinder.obtener(solicitud)).thenReturn(false);

        // Act
        useCase.ejecutar(entrada);

        // Assert
        verify(validator).validar(
                eq(solicitud), eq(false), eq(UtilUUID.obtenerUUIDPorDefecto()),
                eq(UtilTexto.VACIO), eq(remitenteUsuario), eq(false));
    }

    @Test
    void debeConsultarValidarEliminarYPublicarEnOrden_cuandoElFlujoEsValido() {
        // Arrange
        stubSolicitudPropiaSinRespuestas();

        // Act
        useCase.ejecutar(entrada);

        // Assert
        InOrder inOrder = inOrder(datosSolicitudFinder, solicitudTieneRespuestasFinder,
                validator, solicitudOutputPort, eventPublisher);
        inOrder.verify(datosSolicitudFinder).obtener(solicitud);
        inOrder.verify(solicitudTieneRespuestasFinder).obtener(solicitud);
        inOrder.verify(validator).validar(any(), anyBoolean(), any(), any(), any(), anyBoolean());
        inOrder.verify(solicitudOutputPort).eliminar(solicitud);
        inOrder.verify(eventPublisher).publish(any());
    }
}
