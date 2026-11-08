package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.EvaluacionCuantitativaAsesorPorItemExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.ItemCuantitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.ItemCuantitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator.RemoverItemCuantitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.RemocionItemCuantitativoAsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoAsesorKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverItemCuantitativoAsesorUseCaseImplTest {

    @Mock
    private ItemCuantitativoAsesorOutputPort outputPort;

    @Mock
    private ItemCuantitativoAsesorExisteFinder itemExisteFinder;

    @Mock
    private EvaluacionCuantitativaAsesorPorItemExisteFinder evaluacionPorItemExisteFinder;

    @Mock
    private RemoverItemCuantitativoAsesorValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverItemCuantitativoAsesorUseCaseImpl useCase;

    @Test
    void debeEliminar_cuandoItemExisteYNoEstaEnUso() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var remocion = RemocionItemCuantitativoAsesorDomain.crear(item);
        when(itemExisteFinder.obtener(item)).thenReturn(true);
        when(evaluacionPorItemExisteFinder.obtener(item)).thenReturn(false);

        // Act
        useCase.ejecutar(remocion);

        // Assert
        var orden = inOrder(logger, itemExisteFinder, evaluacionPorItemExisteFinder, validator, outputPort);
        orden.verify(logger).info(ItemCuantitativoAsesorKey.LOG_REMOVIENDO, item);
        orden.verify(itemExisteFinder).obtener(item);
        orden.verify(evaluacionPorItemExisteFinder).obtener(item);
        orden.verify(logger).debug(ItemCuantitativoAsesorKey.LOG_VERIFICACION_REMOVER, true, false);
        orden.verify(validator).validar(item, true, false);
        orden.verify(outputPort).eliminar(item);
        orden.verify(logger).info(ItemCuantitativoAsesorKey.LOG_REMOVIDO, item);
        verify(itemExisteFinder, times(1)).obtener(item);
        verify(evaluacionPorItemExisteFinder, times(1)).obtener(item);
    }
}
