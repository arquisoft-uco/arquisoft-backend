package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.EvaluacionCualitativaAsesorPorItemExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.finder.ItemCualitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.ItemCualitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator.RemoverItemCualitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.RemocionItemCualitativoAsesorDomain;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.ItemCualitativoAsesorNoEncontradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoAsesorKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class RemoverItemCualitativoAsesorUseCaseImplTest {

    @Mock
    private ItemCualitativoAsesorOutputPort outputPort;

    @Mock
    private ItemCualitativoAsesorExisteFinder itemExisteFinder;

    @Mock
    private EvaluacionCualitativaAsesorPorItemExisteFinder evaluacionPorItemExisteFinder;

    @Mock
    private RemoverItemCualitativoAsesorValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverItemCualitativoAsesorUseCaseImpl useCase;

    @Test
    void debeEliminar_cuandoItemExisteYNoEstaEnUso() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var remocion = RemocionItemCualitativoAsesorDomain.crear(item);
        when(itemExisteFinder.obtener(item)).thenReturn(true);
        when(evaluacionPorItemExisteFinder.obtener(item)).thenReturn(false);

        // Act
        useCase.ejecutar(remocion);

        // Assert
        var orden = inOrder(logger, itemExisteFinder, evaluacionPorItemExisteFinder, validator, outputPort);
        orden.verify(logger).info(ItemCualitativoAsesorKey.LOG_REMOVIENDO, item);
        orden.verify(itemExisteFinder).obtener(item);
        orden.verify(evaluacionPorItemExisteFinder).obtener(item);
        orden.verify(logger).debug(ItemCualitativoAsesorKey.LOG_VERIFICACION_REMOVER, true, false);
        orden.verify(validator).validar(item, true, false);
        orden.verify(outputPort).eliminar(item);
        orden.verify(logger).info(ItemCualitativoAsesorKey.LOG_REMOVIDO, item);
        verify(itemExisteFinder, times(1)).obtener(item);
        verify(evaluacionPorItemExisteFinder, times(1)).obtener(item);
    }

    @Test
    void debeNoEliminar_cuandoElValidatorRechaza() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var remocion = RemocionItemCualitativoAsesorDomain.crear(item);
        when(itemExisteFinder.obtener(item)).thenReturn(false);
        when(evaluacionPorItemExisteFinder.obtener(item)).thenReturn(false);
        doThrow(new ItemCualitativoAsesorNoEncontradoException(item)).when(validator).validar(item, false, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(remocion))
                .isInstanceOf(ItemCualitativoAsesorNoEncontradoException.class);
        verify(outputPort, never()).eliminar(any());
        verify(logger, never()).info(eq(ItemCualitativoAsesorKey.LOG_REMOVIDO), eq(item));
    }
}
