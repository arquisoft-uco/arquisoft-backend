package com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.EntregableProyectoGradoPorIdFinder;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoDeEntregableFinder;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.CambiarEstadoEntregableProyectoGradoValidator;
import com.arquisoft.entregables.domain.entregableproyectogrado.CambioEstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoCanceladoEvent;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoCerradoEvent;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock
    private EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @Mock
    private EntregableProyectoGradoPorIdFinder entregableProyectoGradoPorIdFinder;

    @Mock
    private ProyectoGradoDeEntregableFinder proyectoGradoDeEntregableFinder;

    @Mock
    private CambiarEstadoEntregableProyectoGradoValidator cambiarEstadoEntregableProyectoGradoValidator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CambiarEstadoEntregableProyectoGradoUseCaseImpl useCase;

    @Test
    void debePublicarEventoCerrado_cuandoElEstadoDestinoEsCerrado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoEntregableProyectoGradoDomain.crear(id, "CERRADO");
        var entregable = EntregableProyectoGradoDomain.reconstruir(id, proyecto, 2,
                UtilUUID.generarNuevoUUID(), EstadoEntregable.APROBADO);
        when(entregableProyectoGradoPorIdFinder.obtener(id)).thenReturn(entregable);
        when(proyectoGradoDeEntregableFinder.obtener(id)).thenReturn(ProyectoGradoDomain.VACIO);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        var orden = inOrder(entregableProyectoGradoPorIdFinder, proyectoGradoDeEntregableFinder,
                cambiarEstadoEntregableProyectoGradoValidator, entregableProyectoGradoOutputPort, eventPublisher,
                logger);
        var captor = ArgumentCaptor.forClass(EntregableProyectoGradoCerradoEvent.class);
        orden.verify(entregableProyectoGradoPorIdFinder, times(1)).obtener(id);
        orden.verify(proyectoGradoDeEntregableFinder, times(1)).obtener(id);
        orden.verify(cambiarEstadoEntregableProyectoGradoValidator).validar(cambio, entregable, ProyectoGradoDomain.VACIO);
        orden.verify(entregableProyectoGradoOutputPort).actualizarEstado(id, "CERRADO");
        orden.verify(eventPublisher).publish(captor.capture());
        orden.verify(logger).info(EntregableProyectoGradoKey.LOG_ESTADO_CAMBIADO, id, "APROBADO", "CERRADO");
        assertThat(captor.getValue().getEntregableProyectoGrado()).isEqualTo(id);
        assertThat(captor.getValue().getProyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().getVersion()).isEqualTo(2);
    }

    @Test
    void debePublicarEventoCancelado_cuandoElEstadoDestinoEsCancelado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoEntregableProyectoGradoDomain.crear(id, "CANCELADO");
        var entregable = EntregableProyectoGradoDomain.reconstruir(id, proyecto, 4,
                UtilUUID.generarNuevoUUID(), EstadoEntregable.EN_EVALUACION);
        when(entregableProyectoGradoPorIdFinder.obtener(id)).thenReturn(entregable);
        when(proyectoGradoDeEntregableFinder.obtener(id)).thenReturn(ProyectoGradoDomain.VACIO);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        var orden = inOrder(entregableProyectoGradoOutputPort, eventPublisher, logger);
        var captor = ArgumentCaptor.forClass(DomainEvent.class);
        orden.verify(entregableProyectoGradoOutputPort).actualizarEstado(id, "CANCELADO");
        orden.verify(eventPublisher).publish(captor.capture());
        orden.verify(logger).info(EntregableProyectoGradoKey.LOG_ESTADO_CAMBIADO, id, "EN_EVALUACION", "CANCELADO");
        verify(eventPublisher, times(1)).publish(any());
        assertThat(captor.getValue()).isInstanceOfSatisfying(EntregableProyectoGradoCanceladoEvent.class, evento -> {
            assertThat(evento.getEntregableProyectoGrado()).isEqualTo(id);
            assertThat(evento.getProyectoGrado()).isEqualTo(proyecto);
            assertThat(evento.getVersion()).isEqualTo(4);
        });
    }

    @Test
    void noDebePublicarEvento_cuandoElValidatorRechaza() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoEntregableProyectoGradoDomain.crear(id, "CERRADO");
        var entregable = EntregableProyectoGradoDomain.reconstruir(id, UtilUUID.generarNuevoUUID(), 1,
                UtilUUID.generarNuevoUUID(), EstadoEntregable.CERRADO);
        when(entregableProyectoGradoPorIdFinder.obtener(id)).thenReturn(entregable);
        when(proyectoGradoDeEntregableFinder.obtener(id)).thenReturn(ProyectoGradoDomain.VACIO);
        doThrow(new DomainException("rechazado", "COD"))
                .when(cambiarEstadoEntregableProyectoGradoValidator).validar(cambio, entregable, ProyectoGradoDomain.VACIO);

        // Act
        assertThatThrownBy(() -> useCase.ejecutar(cambio)).isInstanceOf(DomainException.class);

        // Assert
        verify(entregableProyectoGradoOutputPort, never()).actualizarEstado(any(), anyString());
        verifyNoInteractions(eventPublisher);
        verify(logger, never()).info(any(ClaveMensaje.class), eq(id), any(), any());
    }
}
