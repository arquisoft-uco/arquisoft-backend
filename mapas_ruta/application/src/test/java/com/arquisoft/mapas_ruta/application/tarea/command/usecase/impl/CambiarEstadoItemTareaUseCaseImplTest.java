package com.arquisoft.mapas_ruta.application.tarea.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.finder.TareaPorIdFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.TareaOutputPort;
import com.arquisoft.mapas_ruta.application.tarea.command.validator.CambiarEstadoItemTareaValidator;
import com.arquisoft.mapas_ruta.domain.tarea.CambioEstadoItemTareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.event.TareaFinalizadaEvent;
import com.arquisoft.mapas_ruta.domain.tarea.exception.TransicionEstadoItemNoPermitidaException;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoItemTareaUseCaseImplTest {

    @Mock
    private TareaOutputPort tareaOutputPort;

    @Mock
    private TareaPorIdFinder tareaPorIdFinder;

    @Mock
    private CambiarEstadoItemTareaValidator validator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CambiarEstadoItemTareaUseCaseImpl useCase;

    @Test
    void debeActualizar_conOrdenFinderValidatorPersistencia() {
        // Arrange
        var cambio = CambioEstadoItemTareaDomain.crear(UtilUUID.generarNuevoUUID(), "FINALIZADA");
        var tarea = TareaDomain.reconstruir(cambio.getTarea(), UtilUUID.generarNuevoUUID(),
                "Redactar capitulo", UtilUUID.generarNuevoUUID(), EstadoItem.NUEVA);
        when(tareaPorIdFinder.obtener(cambio.getTarea())).thenReturn(tarea);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        var captor = ArgumentCaptor.forClass(TareaFinalizadaEvent.class);
        var orden = inOrder(tareaPorIdFinder, validator, tareaOutputPort, eventPublisher);
        orden.verify(tareaPorIdFinder).obtener(cambio.getTarea());
        orden.verify(validator).validar(cambio, tarea);
        orden.verify(tareaOutputPort).actualizarEstadoItem(cambio.getTarea(), "FINALIZADA");
        orden.verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue().getTarea()).isEqualTo(cambio.getTarea());
        verify(tareaPorIdFinder, times(1)).obtener(any());
        verify(logger).info(any(ClaveMensaje.class), eq(cambio.getTarea()), eq("NUEVA"), eq("FINALIZADA"));
    }

    @Test
    void noDebePublicar_cuandoPasaAEnProgreso() {
        // Arrange
        var cambio = CambioEstadoItemTareaDomain.crear(UtilUUID.generarNuevoUUID(), "EN_PROGRESO");
        var tarea = TareaDomain.reconstruir(cambio.getTarea(), UtilUUID.generarNuevoUUID(),
                "Redactar capitulo", UtilUUID.generarNuevoUUID(), EstadoItem.NUEVA);
        when(tareaPorIdFinder.obtener(cambio.getTarea())).thenReturn(tarea);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        verify(tareaOutputPort).actualizarEstadoItem(cambio.getTarea(), "EN_PROGRESO");
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void noDebeActualizarNiLogearCierre_cuandoElValidatorRechaza() {
        // Arrange
        var cambio = CambioEstadoItemTareaDomain.crear(UtilUUID.generarNuevoUUID(), "NUEVA");
        var tarea = TareaDomain.reconstruir(cambio.getTarea(), UtilUUID.generarNuevoUUID(),
                "Redactar capitulo", UtilUUID.generarNuevoUUID(), EstadoItem.FINALIZADA);
        when(tareaPorIdFinder.obtener(cambio.getTarea())).thenReturn(tarea);
        doThrow(new TransicionEstadoItemNoPermitidaException(cambio.getTarea(), EstadoItem.FINALIZADA,
                EstadoItem.NUEVA)).when(validator).validar(cambio, tarea);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio))
                .isInstanceOf(TransicionEstadoItemNoPermitidaException.class);
        verify(tareaOutputPort, never()).actualizarEstadoItem(any(), any());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(cambio.getTarea()), eq("FINALIZADA"),
                eq("NUEVA"));
    }
}
