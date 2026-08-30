package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.ItemCualitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.ItemCualitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.ModificarItemCualitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.ModificacionItemCualitativoJuradoDomain;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception.ItemCualitativoJuradoNoEncontradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoJuradoKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificarItemCualitativoJuradoUseCaseImplTest {

    @Mock
    private ItemCualitativoJuradoOutputPort outputPort;

    @Mock
    private ItemCualitativoJuradoExisteFinder finder;

    @Mock
    private ModificarItemCualitativoJuradoValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ModificarItemCualitativoJuradoUseCaseImpl useCase;

    @Test
    void debeActualizarDescripcionEnOrden_cuandoItemExiste() {
        // Arrange
        UUID itemCualitativoJurado = UUID.randomUUID();
        var modificacion = ModificacionItemCualitativoJuradoDomain.crear(
                itemCualitativoJurado, "Nueva descripción");
        when(finder.obtener(itemCualitativoJurado)).thenReturn(true);

        // Act
        useCase.ejecutar(modificacion);

        // Assert
        InOrder orden = inOrder(logger, finder, validator, outputPort);
        orden.verify(logger).info(
                Mensajes.obtener(ItemCualitativoJuradoKey.LOG_MODIFICANDO), itemCualitativoJurado);
        orden.verify(finder).obtener(itemCualitativoJurado);
        orden.verify(logger).debug(
                Mensajes.obtener(ItemCualitativoJuradoKey.LOG_VERIFICACION_MODIFICAR), true);
        orden.verify(validator).validar(itemCualitativoJurado, true);
        orden.verify(outputPort).actualizarDescripcion(
                itemCualitativoJurado, modificacion.getDescripcion());
        orden.verify(logger).info(
                Mensajes.obtener(ItemCualitativoJuradoKey.LOG_MODIFICADO), itemCualitativoJurado);
    }

    @Test
    void debeDetenerFlujo_cuandoItemNoExiste() {
        // Arrange
        UUID itemCualitativoJurado = UUID.randomUUID();
        var modificacion = ModificacionItemCualitativoJuradoDomain.crear(
                itemCualitativoJurado, "Nueva descripción");
        when(finder.obtener(itemCualitativoJurado)).thenReturn(false);
        doThrow(new ItemCualitativoJuradoNoEncontradoException(itemCualitativoJurado))
                .when(validator).validar(itemCualitativoJurado, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(modificacion))
                .isInstanceOf(ItemCualitativoJuradoNoEncontradoException.class);
        verify(outputPort, never()).actualizarDescripcion(any(), anyString());
        verify(logger, never()).info(
                Mensajes.obtener(ItemCualitativoJuradoKey.LOG_MODIFICADO), itemCualitativoJurado);
    }
}
