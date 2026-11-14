package com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.asesorproyectogrado.command.finder.AsesorVigenteDeEntregableFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.EntregableProyectoGradoPorIdFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.CambiarEstadoEntregableProyectoGradoValidator;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.finder.EstadoActualEntregableProyectoGradoFinder;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.EstadoEntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.entity.EstadoEntregableProyectoGradoEntity;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoBloqueadoDeEntregableFinder;
import com.arquisoft.entregables.domain.entregableproyectogrado.CambioEstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoCanceladoEvent;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoCerradoEvent;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoDisponibleEvaluacionEvent;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoEntregableProyectoGradoUseCaseImplTest {

    private static final UUID ASESOR_VIGENTE = UtilUUID.generarNuevoUUID();

    @Mock
    private EstadoEntregableProyectoGradoOutputPort estadoEntregableProyectoGradoOutputPort;

    @Mock
    private EntregableProyectoGradoPorIdFinder entregableProyectoGradoPorIdFinder;

    @Mock
    private EstadoActualEntregableProyectoGradoFinder estadoActualEntregableProyectoGradoFinder;

    @Mock
    private ProyectoGradoBloqueadoDeEntregableFinder proyectoGradoBloqueadoDeEntregableFinder;

    @Mock
    private AsesorVigenteDeEntregableFinder asesorVigenteDeEntregableFinder;

    @Mock
    private CambiarEstadoEntregableProyectoGradoValidator cambiarEstadoEntregableProyectoGradoValidator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CambiarEstadoEntregableProyectoGradoUseCaseImpl useCase;

    private static EstadoEntregableProyectoGradoDomain estadoActual(UUID id, EstadoEntregable estado) {
        return EstadoEntregableProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), id, estado, Instant.now());
    }

    @Test
    void debePublicarEventoCerrado_cuandoElEstadoDestinoEsCerrado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoEntregableProyectoGradoDomain.crear(id, "CERRADO", UtilUUID.generarNuevoUUID());
        var entregable = EntregableProyectoGradoDomain.reconstruir(id, proyecto, 2, UtilUUID.generarNuevoUUID());
        var estado = estadoActual(id, EstadoEntregable.APROBADO);
        when(entregableProyectoGradoPorIdFinder.obtener(id)).thenReturn(entregable);
        when(estadoActualEntregableProyectoGradoFinder.obtener(id)).thenReturn(estado);
        when(proyectoGradoBloqueadoDeEntregableFinder.obtener(id)).thenReturn(ProyectoGradoDomain.VACIO);
        when(asesorVigenteDeEntregableFinder.obtener(id)).thenReturn(ASESOR_VIGENTE);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        var orden = inOrder(proyectoGradoBloqueadoDeEntregableFinder, entregableProyectoGradoPorIdFinder,
                estadoActualEntregableProyectoGradoFinder, cambiarEstadoEntregableProyectoGradoValidator,
                estadoEntregableProyectoGradoOutputPort, eventPublisher, logger);
        var captor = ArgumentCaptor.forClass(EntregableProyectoGradoCerradoEvent.class);
        var captorEstado = ArgumentCaptor.forClass(EstadoEntregableProyectoGradoEntity.class);
        orden.verify(proyectoGradoBloqueadoDeEntregableFinder, times(1)).obtener(id);
        orden.verify(entregableProyectoGradoPorIdFinder, times(1)).obtener(id);
        orden.verify(estadoActualEntregableProyectoGradoFinder, times(1)).obtener(id);
        orden.verify(cambiarEstadoEntregableProyectoGradoValidator)
                .validar(cambio, entregable, estado, ProyectoGradoDomain.VACIO, ASESOR_VIGENTE);
        orden.verify(estadoEntregableProyectoGradoOutputPort).registrar(captorEstado.capture());
        orden.verify(eventPublisher).publish(captor.capture());
        orden.verify(logger).info(EntregableProyectoGradoKey.LOG_ESTADO_CAMBIADO, id, "APROBADO", "CERRADO");
        assertThat(captorEstado.getValue().entregableProyectoGrado()).isEqualTo(id);
        assertThat(captorEstado.getValue().estadoEntregable()).isEqualTo("CERRADO");
        assertThat(captorEstado.getValue().fechaActualizacion()).isNotNull();
        assertThat(captor.getValue().getEntregableProyectoGrado()).isEqualTo(id);
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().getVersion()).isEqualTo(2);
    }

    @Test
    void debePublicarEventoCancelado_cuandoElEstadoDestinoEsCancelado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoEntregableProyectoGradoDomain.crear(id, "CANCELADO", UtilUUID.generarNuevoUUID());
        var entregable = EntregableProyectoGradoDomain.reconstruir(id, proyecto, 4, UtilUUID.generarNuevoUUID());
        when(entregableProyectoGradoPorIdFinder.obtener(id)).thenReturn(entregable);
        when(estadoActualEntregableProyectoGradoFinder.obtener(id))
                .thenReturn(estadoActual(id, EstadoEntregable.NUEVO));
        when(proyectoGradoBloqueadoDeEntregableFinder.obtener(id)).thenReturn(ProyectoGradoDomain.VACIO);
        when(asesorVigenteDeEntregableFinder.obtener(id)).thenReturn(ASESOR_VIGENTE);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        var orden = inOrder(estadoEntregableProyectoGradoOutputPort, eventPublisher, logger);
        var captor = ArgumentCaptor.forClass(DomainEvent.class);
        var captorEstado = ArgumentCaptor.forClass(EstadoEntregableProyectoGradoEntity.class);
        orden.verify(estadoEntregableProyectoGradoOutputPort).registrar(captorEstado.capture());
        orden.verify(eventPublisher).publish(captor.capture());
        orden.verify(logger).info(EntregableProyectoGradoKey.LOG_ESTADO_CAMBIADO, id, "NUEVO", "CANCELADO");
        verify(eventPublisher, never()).publish(any(EntregableProyectoGradoDisponibleEvaluacionEvent.class));
        verify(eventPublisher, times(1)).publish(any());
        assertThat(captorEstado.getValue().estadoEntregable()).isEqualTo("CANCELADO");
        assertThat(captor.getValue()).isInstanceOfSatisfying(EntregableProyectoGradoCanceladoEvent.class, evento -> {
            assertThat(evento.getEntregableProyectoGrado()).isEqualTo(id);
            assertThat(evento.getProyectoGrado()).isEqualTo(proyecto);
            assertThat(evento.getVersion()).isEqualTo(4);
        });
    }

    @Test
    void debePublicarEventoDisponibleEvaluacionConNombreProyecto_cuandoPasaADisponibleEvaluacion() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyectoId = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoEntregableProyectoGradoDomain.crear(
                id, "DISPONIBLE_EVALUACION", UtilUUID.generarNuevoUUID());
        var entregable = EntregableProyectoGradoDomain.reconstruir(id, proyectoId, 3, UtilUUID.generarNuevoUUID());
        var estado = estadoActual(id, EstadoEntregable.NUEVO);
        var proyecto = ProyectoGradoDomain.reconstruir(proyectoId, "Sistema de riego", EstadoProyecto.EN_PROCESO, null);
        when(entregableProyectoGradoPorIdFinder.obtener(id)).thenReturn(entregable);
        when(estadoActualEntregableProyectoGradoFinder.obtener(id)).thenReturn(estado);
        when(proyectoGradoBloqueadoDeEntregableFinder.obtener(id)).thenReturn(proyecto);
        when(asesorVigenteDeEntregableFinder.obtener(id)).thenReturn(ASESOR_VIGENTE);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        var orden = inOrder(cambiarEstadoEntregableProyectoGradoValidator, estadoEntregableProyectoGradoOutputPort,
                eventPublisher, logger);
        var captor = ArgumentCaptor.forClass(DomainEvent.class);
        var captorEstado = ArgumentCaptor.forClass(EstadoEntregableProyectoGradoEntity.class);
        orden.verify(cambiarEstadoEntregableProyectoGradoValidator).validar(cambio, entregable, estado, proyecto, ASESOR_VIGENTE);
        orden.verify(estadoEntregableProyectoGradoOutputPort).registrar(captorEstado.capture());
        orden.verify(eventPublisher).publish(captor.capture());
        orden.verify(logger).info(EntregableProyectoGradoKey.LOG_ESTADO_CAMBIADO, id, "NUEVO", "DISPONIBLE_EVALUACION");
        verify(eventPublisher, times(1)).publish(any());
        assertThat(captorEstado.getValue().estadoEntregable()).isEqualTo("DISPONIBLE_EVALUACION");
        assertThat(captor.getValue()).isInstanceOfSatisfying(EntregableProyectoGradoDisponibleEvaluacionEvent.class, evento -> {
            assertThat(evento.getEntregableProyectoGrado()).isEqualTo(id);
            assertThat(evento.getProyectoGrado()).isEqualTo(proyectoId);
            assertThat(evento.getNombreProyecto()).isEqualTo("Sistema de riego");
            assertThat(evento.getVersion()).isEqualTo(3);
        });
    }

    @Test
    void noDebePublicarEvento_cuandoElValidatorRechaza() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoEntregableProyectoGradoDomain.crear(id, "CERRADO", UtilUUID.generarNuevoUUID());
        var entregable = EntregableProyectoGradoDomain.reconstruir(id, UtilUUID.generarNuevoUUID(), 1,
                UtilUUID.generarNuevoUUID());
        var estado = estadoActual(id, EstadoEntregable.CERRADO);
        when(entregableProyectoGradoPorIdFinder.obtener(id)).thenReturn(entregable);
        when(estadoActualEntregableProyectoGradoFinder.obtener(id)).thenReturn(estado);
        when(proyectoGradoBloqueadoDeEntregableFinder.obtener(id)).thenReturn(ProyectoGradoDomain.VACIO);
        when(asesorVigenteDeEntregableFinder.obtener(id)).thenReturn(ASESOR_VIGENTE);
        doThrow(new DomainException("rechazado", "COD"))
                .when(cambiarEstadoEntregableProyectoGradoValidator)
                .validar(cambio, entregable, estado, ProyectoGradoDomain.VACIO, ASESOR_VIGENTE);

        // Act
        assertThatThrownBy(() -> useCase.ejecutar(cambio)).isInstanceOf(DomainException.class);

        // Assert
        verify(estadoEntregableProyectoGradoOutputPort, never()).registrar(any());
        verifyNoInteractions(eventPublisher);
        verify(logger, never()).info(any(ClaveMensaje.class), eq(id), any(), any());
    }
}
