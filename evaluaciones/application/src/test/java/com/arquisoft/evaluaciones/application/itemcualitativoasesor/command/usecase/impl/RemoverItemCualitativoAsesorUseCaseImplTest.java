package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.finder.ItemCualitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.ItemCualitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator.RemoverItemCualitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.RemocionItemCualitativoAsesorDomain;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.ItemCualitativoAsesorNoEncontradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoAsesorKey;
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
class RemoverItemCualitativoAsesorUseCaseImplTest {

    @Mock
    private ItemCualitativoAsesorOutputPort outputPort;

    @Mock
    private ItemCualitativoAsesorExisteFinder itemExisteFinder;

    @Mock
    private RemoverItemCualitativoAsesorValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverItemCualitativoAsesorUseCaseImpl useCase;

    @Test
    void debeMarcarEliminado_cuandoItemExiste() {
        // Arrange
        var itemCualitativoAsesor = UUID.randomUUID();
        var remocion = RemocionItemCualitativoAsesorDomain.crear(itemCualitativoAsesor);
        when(itemExisteFinder.obtener(itemCualitativoAsesor)).thenReturn(true);

        // Act
        useCase.ejecutar(remocion);

        // Assert
        var orden = inOrder(logger, itemExisteFinder, validator, outputPort);
        orden.verify(logger).info(ItemCualitativoAsesorKey.LOG_REMOVIENDO, itemCualitativoAsesor);
        orden.verify(itemExisteFinder).obtener(itemCualitativoAsesor);
        orden.verify(logger).debug(ItemCualitativoAsesorKey.LOG_VERIFICACION_REMOVER, true);
        orden.verify(validator).validar(itemCualitativoAsesor, true);
        orden.verify(outputPort).marcarEliminado(itemCualitativoAsesor, remocion.getEliminadoEn());
        orden.verify(logger).info(ItemCualitativoAsesorKey.LOG_REMOVIDO, itemCualitativoAsesor);
    }

    @Test
    void debeRegistrarFechaDeEliminacion_cuandoSeCreaLaRemocion() {
        // Arrange
        var antes = Instant.now();

        // Act
        var remocion = RemocionItemCualitativoAsesorDomain.crear(UUID.randomUUID());

        // Assert
        assertThat(remocion.getEliminadoEn()).isBetween(antes, Instant.now());
    }

    @Test
    void debeNoMarcarEliminado_cuandoValidatorLanza() {
        // Arrange
        var itemCualitativoAsesor = UUID.randomUUID();
        var remocion = RemocionItemCualitativoAsesorDomain.crear(itemCualitativoAsesor);
        when(itemExisteFinder.obtener(itemCualitativoAsesor)).thenReturn(false);
        doThrow(new ItemCualitativoAsesorNoEncontradoException(itemCualitativoAsesor))
                .when(validator).validar(itemCualitativoAsesor, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(remocion))
                .isInstanceOf(ItemCualitativoAsesorNoEncontradoException.class);
        verify(outputPort, never()).marcarEliminado(any(), any());
        verify(logger, never()).info(ItemCualitativoAsesorKey.LOG_REMOVIDO, itemCualitativoAsesor);
    }
}
