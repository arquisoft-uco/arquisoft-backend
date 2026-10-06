package com.arquisoft.fichas.application.observacionitem.command.usecase.impl;

import com.arquisoft.fichas.application.observacionitem.command.finder.ContextoObservacionItemFinder;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.application.observacionitem.command.validator.RemoverObservacionItemValidator;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.observacionitem.RemocionObservacionItemDomain;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverObservacionItemUseCaseImplTest {

    @Mock
    private ContextoObservacionItemFinder contextoObservacionItemFinder;

    @Mock
    private RemoverObservacionItemValidator removerObservacionItemValidator;

    @Mock
    private ObservacionItemOutputPort observacionItemOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverObservacionItemUseCaseImpl removerObservacionItemUseCase;

    private final UUID observacionItem = UtilUUID.generarNuevoUUID();
    private final UUID asesorFicha = UtilUUID.generarNuevoUUID();

    @Test
    void debeRemoverLaObservacion_cuandoLaValidacionPasa() {
        // Arrange
        var entrada = RemocionObservacionItemDomain.crear(observacionItem, asesorFicha);
        var contexto = new ContextoObservacionItem(
                UtilUUID.generarNuevoUUID(), EstadoRevision.EN_PROGRESO, UtilUUID.generarNuevoUUID(), asesorFicha);
        when(contextoObservacionItemFinder.obtener(observacionItem)).thenReturn(contexto);

        // Act
        removerObservacionItemUseCase.ejecutar(entrada);

        // Assert — presupuesto de I/O: una consulta de contexto y un borrado, en ese orden
        verify(contextoObservacionItemFinder, times(1)).obtener(observacionItem);
        verify(observacionItemOutputPort, times(1)).removerObservacion(observacionItem);
        verify(logger).info(eq(ObservacionItemKey.LOG_REMOVIDA), eq(observacionItem));

        var orden = inOrder(contextoObservacionItemFinder, removerObservacionItemValidator,
                observacionItemOutputPort);
        orden.verify(contextoObservacionItemFinder).obtener(observacionItem);
        orden.verify(removerObservacionItemValidator).validar(entrada, contexto);
        orden.verify(observacionItemOutputPort).removerObservacion(observacionItem);
    }

    @Test
    void debeLanzarYNoRemover_cuandoElValidatorRechaza() {
        // Arrange — la observación no existe: el finder devuelve VACIO y la validación la rechaza
        var entrada = RemocionObservacionItemDomain.crear(observacionItem, asesorFicha);
        when(contextoObservacionItemFinder.obtener(observacionItem)).thenReturn(ContextoObservacionItem.VACIO);
        doThrow(new ObservacionItemNoEncontradaException(observacionItem))
                .when(removerObservacionItemValidator)
                .validar(entrada, ContextoObservacionItem.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> removerObservacionItemUseCase.ejecutar(entrada))
                .isInstanceOf(ObservacionItemNoEncontradaException.class);

        verify(observacionItemOutputPort, never()).removerObservacion(any());
        verify(logger, never()).info(eq(ObservacionItemKey.LOG_REMOVIDA), eq(observacionItem));
    }
}
