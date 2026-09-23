package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.EvaluacionCualitativaJuradoPorItemExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.ItemCualitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.ItemCualitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.RemoverItemCualitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.RemocionItemCualitativoJuradoDomain;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception.ItemCualitativoJuradoEnUsoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoJuradoKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverItemCualitativoJuradoUseCaseImplTest {

    @Mock
    private ItemCualitativoJuradoOutputPort outputPort;

    @Mock
    private ItemCualitativoJuradoExisteFinder itemExisteFinder;

    @Mock
    private EvaluacionCualitativaJuradoPorItemExisteFinder evaluacionPorItemExisteFinder;

    @Mock
    private RemoverItemCualitativoJuradoValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverItemCualitativoJuradoUseCaseImpl useCase;

    @Test
    void debeEliminar_cuandoItemExisteYNoEstaEnUso() {
        // Arrange
        var itemCualitativoJurado = UUID.randomUUID();
        var remocion = RemocionItemCualitativoJuradoDomain.crear(itemCualitativoJurado);
        when(itemExisteFinder.obtener(itemCualitativoJurado)).thenReturn(true);
        when(evaluacionPorItemExisteFinder.obtener(itemCualitativoJurado)).thenReturn(false);

        // Act
        useCase.ejecutar(remocion);

        // Assert
        var orden = inOrder(logger, itemExisteFinder, evaluacionPorItemExisteFinder, validator, outputPort);
        orden.verify(logger).info(ItemCualitativoJuradoKey.LOG_REMOVIENDO, itemCualitativoJurado);
        orden.verify(itemExisteFinder).obtener(itemCualitativoJurado);
        orden.verify(evaluacionPorItemExisteFinder).obtener(itemCualitativoJurado);
        orden.verify(logger).debug(ItemCualitativoJuradoKey.LOG_VERIFICACION_REMOVER, true, false);
        orden.verify(validator).validar(itemCualitativoJurado, true, false);
        orden.verify(outputPort).eliminar(itemCualitativoJurado);
        orden.verify(logger).info(ItemCualitativoJuradoKey.LOG_REMOVIDO, itemCualitativoJurado);
    }

    @Test
    void debeNoEliminar_cuandoValidatorLanza() {
        // Arrange
        var itemCualitativoJurado = UUID.randomUUID();
        var remocion = RemocionItemCualitativoJuradoDomain.crear(itemCualitativoJurado);
        when(itemExisteFinder.obtener(itemCualitativoJurado)).thenReturn(true);
        when(evaluacionPorItemExisteFinder.obtener(itemCualitativoJurado)).thenReturn(true);
        doThrow(new ItemCualitativoJuradoEnUsoException(itemCualitativoJurado))
                .when(validator).validar(itemCualitativoJurado, true, true);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(remocion))
                .isInstanceOf(ItemCualitativoJuradoEnUsoException.class);
        verify(outputPort, never()).eliminar(any());
        verify(logger, never()).info(ItemCualitativoJuradoKey.LOG_REMOVIDO, itemCualitativoJurado);
    }
}
