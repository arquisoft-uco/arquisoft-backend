package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.ItemCuantitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.ItemCuantitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator.ModificarItemCuantitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ModificacionItemCuantitativoJuradoDomain;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.ItemCuantitativoJuradoNoEncontradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoJuradoKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificarItemCuantitativoJuradoUseCaseImplTest {

    @Mock
    private ItemCuantitativoJuradoOutputPort outputPort;

    @Mock
    private ItemCuantitativoJuradoExisteFinder finder;

    @Mock
    private ModificarItemCuantitativoJuradoValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ModificarItemCuantitativoJuradoUseCaseImpl useCase;

    @Test
    void debeActualizarDescripcionEnOrden_cuandoItemExiste() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();
        var modificacion = ModificacionItemCuantitativoJuradoDomain.crear(
                itemCuantitativoJurado, "Nueva descripción");
        when(finder.obtener(itemCuantitativoJurado)).thenReturn(true);

        // Act
        useCase.ejecutar(modificacion);

        // Assert
        var orden = inOrder(logger, finder, validator, outputPort);
        orden.verify(logger).info(ItemCuantitativoJuradoKey.LOG_MODIFICANDO, itemCuantitativoJurado);
        orden.verify(finder).obtener(itemCuantitativoJurado);
        orden.verify(logger).debug(ItemCuantitativoJuradoKey.LOG_VERIFICACION_MODIFICAR, true);
        orden.verify(validator).validar(itemCuantitativoJurado, true);
        orden.verify(outputPort).actualizarDescripcion(
                itemCuantitativoJurado, modificacion.getDescripcion());
        orden.verify(logger).info(ItemCuantitativoJuradoKey.LOG_MODIFICADO, itemCuantitativoJurado);
        verify(finder, times(1)).obtener(itemCuantitativoJurado);
    }

    @Test
    void debeDetenerFlujo_cuandoItemNoExiste() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();
        var modificacion = ModificacionItemCuantitativoJuradoDomain.crear(
                itemCuantitativoJurado, "Nueva descripción");
        when(finder.obtener(itemCuantitativoJurado)).thenReturn(false);
        doThrow(new ItemCuantitativoJuradoNoEncontradoException(itemCuantitativoJurado))
                .when(validator).validar(itemCuantitativoJurado, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(modificacion))
                .isInstanceOf(ItemCuantitativoJuradoNoEncontradoException.class);
        verify(outputPort, never()).actualizarDescripcion(any(), anyString());
        verify(logger, never()).info(ItemCuantitativoJuradoKey.LOG_MODIFICADO, itemCuantitativoJurado);
    }
}
