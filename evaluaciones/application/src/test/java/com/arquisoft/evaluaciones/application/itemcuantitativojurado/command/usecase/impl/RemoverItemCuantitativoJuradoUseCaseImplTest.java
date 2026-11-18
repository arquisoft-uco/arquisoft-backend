package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.ItemCuantitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.ItemCuantitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator.RemoverItemCuantitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.RemocionItemCuantitativoJuradoDomain;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.ItemCuantitativoJuradoNoEncontradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoJuradoKey;
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
class RemoverItemCuantitativoJuradoUseCaseImplTest {

    @Mock
    private ItemCuantitativoJuradoOutputPort outputPort;

    @Mock
    private ItemCuantitativoJuradoExisteFinder itemExisteFinder;

    @Mock
    private RemoverItemCuantitativoJuradoValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverItemCuantitativoJuradoUseCaseImpl useCase;

    @Test
    void debeMarcarEliminado_cuandoItemExiste() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();
        var remocion = RemocionItemCuantitativoJuradoDomain.crear(itemCuantitativoJurado);
        when(itemExisteFinder.obtener(itemCuantitativoJurado)).thenReturn(true);

        // Act
        useCase.ejecutar(remocion);

        // Assert
        var orden = inOrder(logger, itemExisteFinder, validator, outputPort);
        orden.verify(logger).info(ItemCuantitativoJuradoKey.LOG_REMOVIENDO, itemCuantitativoJurado);
        orden.verify(itemExisteFinder).obtener(itemCuantitativoJurado);
        orden.verify(logger).debug(ItemCuantitativoJuradoKey.LOG_VERIFICACION_REMOVER, true);
        orden.verify(validator).validar(itemCuantitativoJurado, true);
        orden.verify(outputPort).marcarEliminado(itemCuantitativoJurado, remocion.getEliminadoEn());
        orden.verify(logger).info(ItemCuantitativoJuradoKey.LOG_REMOVIDO, itemCuantitativoJurado);
    }

    @Test
    void debeRegistrarFechaDeEliminacion_cuandoSeCreaLaRemocion() {
        // Arrange
        var antes = Instant.now();

        // Act
        var remocion = RemocionItemCuantitativoJuradoDomain.crear(UUID.randomUUID());

        // Assert
        assertThat(remocion.getEliminadoEn()).isBetween(antes, Instant.now());
    }

    @Test
    void debeNoMarcarEliminado_cuandoValidatorLanza() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();
        var remocion = RemocionItemCuantitativoJuradoDomain.crear(itemCuantitativoJurado);
        when(itemExisteFinder.obtener(itemCuantitativoJurado)).thenReturn(false);
        doThrow(new ItemCuantitativoJuradoNoEncontradoException(itemCuantitativoJurado))
                .when(validator).validar(itemCuantitativoJurado, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(remocion))
                .isInstanceOf(ItemCuantitativoJuradoNoEncontradoException.class);
        verify(outputPort, never()).marcarEliminado(any(), any());
        verify(logger, never()).info(ItemCuantitativoJuradoKey.LOG_REMOVIDO, itemCuantitativoJurado);
    }
}
