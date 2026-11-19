package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.ItemCualitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.ItemCualitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.RemoverItemCualitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.RemocionItemCualitativoJuradoDomain;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception.ItemCualitativoJuradoNoEncontradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoJuradoKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
    private RemoverItemCualitativoJuradoValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverItemCualitativoJuradoUseCaseImpl useCase;

    @Test
    void debeMarcarEliminado_cuandoItemExiste() {
        // Arrange
        var itemCualitativoJurado = UUID.randomUUID();
        var remocion = RemocionItemCualitativoJuradoDomain.crear(itemCualitativoJurado);
        when(itemExisteFinder.obtener(itemCualitativoJurado)).thenReturn(true);

        // Act
        useCase.ejecutar(remocion);

        // Assert
        var orden = inOrder(logger, itemExisteFinder, validator, outputPort);
        orden.verify(logger).info(ItemCualitativoJuradoKey.LOG_REMOVIENDO, itemCualitativoJurado);
        orden.verify(itemExisteFinder).obtener(itemCualitativoJurado);
        orden.verify(logger).debug(ItemCualitativoJuradoKey.LOG_VERIFICACION_REMOVER, true);
        orden.verify(validator).validar(itemCualitativoJurado, true);
        orden.verify(outputPort).marcarEliminado(itemCualitativoJurado, remocion.getEliminadoEn());
        orden.verify(logger).info(ItemCualitativoJuradoKey.LOG_REMOVIDO, itemCualitativoJurado);
    }

    @Test
    void debeRegistrarFechaDeEliminacion_cuandoSeCreaLaRemocion() {
        // Arrange
        var antes = Instant.now();

        // Act
        var remocion = RemocionItemCualitativoJuradoDomain.crear(UUID.randomUUID());

        // Assert
        assertThat(remocion.getEliminadoEn()).isBetween(antes, Instant.now());
    }

    @Test
    void debeNoMarcarEliminado_cuandoValidatorLanza() {
        // Arrange
        var itemCualitativoJurado = UUID.randomUUID();
        var remocion = RemocionItemCualitativoJuradoDomain.crear(itemCualitativoJurado);
        when(itemExisteFinder.obtener(itemCualitativoJurado)).thenReturn(false);
        doThrow(new ItemCualitativoJuradoNoEncontradoException(itemCualitativoJurado))
                .when(validator).validar(itemCualitativoJurado, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(remocion))
                .isInstanceOf(ItemCualitativoJuradoNoEncontradoException.class);
        verify(outputPort, never()).marcarEliminado(any(), any());
        verify(logger, never()).info(ItemCualitativoJuradoKey.LOG_REMOVIDO, itemCualitativoJurado);
    }
}
