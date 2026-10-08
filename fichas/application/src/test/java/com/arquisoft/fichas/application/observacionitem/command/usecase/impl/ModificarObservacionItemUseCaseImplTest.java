package com.arquisoft.fichas.application.observacionitem.command.usecase.impl;

import com.arquisoft.fichas.application.observacionitem.command.finder.ContextoObservacionItemFinder;
import com.arquisoft.fichas.application.observacionitem.command.finder.OtrasObservacionesIgualesEnRevisionFinder;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.application.observacionitem.command.validator.ModificarObservacionItemValidator;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.observacionitem.ModificacionObservacionItemDomain;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemNoEncontradaException;
import com.arquisoft.fichas.domain.observacionitem.model.ContextoObservacionItem;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificarObservacionItemUseCaseImplTest {

    @Mock
    private ContextoObservacionItemFinder contextoObservacionItemFinder;

    @Mock
    private OtrasObservacionesIgualesEnRevisionFinder otrasObservacionesIgualesEnRevisionFinder;

    @Mock
    private ModificarObservacionItemValidator modificarObservacionItemValidator;

    @Mock
    private ObservacionItemOutputPort observacionItemOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ModificarObservacionItemUseCaseImpl modificarObservacionItemUseCase;

    private final UUID observacionItem = UtilUUID.generarNuevoUUID();
    private final UUID asesorFicha = UtilUUID.generarNuevoUUID();

    @Test
    void debeActualizarLaObservacionRecortada_cuandoLaValidacionPasa() {
        // Arrange
        var entrada = ModificacionObservacionItemDomain.crear(observacionItem, "  Observación nueva  ", asesorFicha);
        var contexto = new ContextoObservacionItem(
                UtilUUID.generarNuevoUUID(), EstadoRevision.NUEVA, UtilUUID.generarNuevoUUID(), asesorFicha);
        when(contextoObservacionItemFinder.obtener(observacionItem)).thenReturn(contexto);
        when(otrasObservacionesIgualesEnRevisionFinder.obtener(entrada)).thenReturn(0L);

        // Act
        modificarObservacionItemUseCase.ejecutar(entrada);

        // Assert — presupuesto de I/O: una llamada por finder, y se escribe el texto ya recortado
        verify(contextoObservacionItemFinder, times(1)).obtener(observacionItem);
        verify(otrasObservacionesIgualesEnRevisionFinder, times(1)).obtener(entrada);
        verify(observacionItemOutputPort, times(1)).actualizarObservacion(observacionItem, "Observación nueva");
        verify(logger).info(eq(ObservacionItemKey.LOG_MODIFICADA), eq(observacionItem));

        var orden = inOrder(contextoObservacionItemFinder, otrasObservacionesIgualesEnRevisionFinder,
                modificarObservacionItemValidator, observacionItemOutputPort);
        orden.verify(contextoObservacionItemFinder).obtener(observacionItem);
        orden.verify(otrasObservacionesIgualesEnRevisionFinder).obtener(entrada);
        orden.verify(modificarObservacionItemValidator).validar(entrada, contexto, 0L);
        orden.verify(observacionItemOutputPort).actualizarObservacion(observacionItem, "Observación nueva");
    }

    @Test
    void debeLanzarYNoActualizar_cuandoElValidatorRechaza() {
        // Arrange — la observación no existe: el finder devuelve VACIO y la validación la rechaza
        var entrada = ModificacionObservacionItemDomain.crear(observacionItem, "Observación nueva", asesorFicha);
        when(contextoObservacionItemFinder.obtener(observacionItem)).thenReturn(ContextoObservacionItem.VACIO);
        when(otrasObservacionesIgualesEnRevisionFinder.obtener(entrada)).thenReturn(0L);
        doThrow(new ObservacionItemNoEncontradaException(observacionItem))
                .when(modificarObservacionItemValidator)
                .validar(entrada, ContextoObservacionItem.VACIO, 0L);

        // Act & Assert
        assertThatThrownBy(() -> modificarObservacionItemUseCase.ejecutar(entrada))
                .isInstanceOf(ObservacionItemNoEncontradaException.class);

        verify(observacionItemOutputPort, never()).actualizarObservacion(any(), anyString());
        verify(logger, never()).info(eq(ObservacionItemKey.LOG_MODIFICADA), eq(observacionItem));
    }
}
