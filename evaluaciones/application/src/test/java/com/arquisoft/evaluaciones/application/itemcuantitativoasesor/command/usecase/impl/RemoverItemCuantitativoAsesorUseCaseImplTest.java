package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.ItemCuantitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.ItemCuantitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator.RemoverItemCuantitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.RemocionItemCuantitativoAsesorDomain;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.ItemCuantitativoAsesorNoEncontradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoAsesorKey;
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
class RemoverItemCuantitativoAsesorUseCaseImplTest {

    @Mock
    private ItemCuantitativoAsesorOutputPort outputPort;

    @Mock
    private ItemCuantitativoAsesorExisteFinder itemExisteFinder;

    @Mock
    private RemoverItemCuantitativoAsesorValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverItemCuantitativoAsesorUseCaseImpl useCase;

    @Test
    void debeMarcarEliminado_cuandoItemExiste() {
        // Arrange
        var itemCuantitativoAsesor = UUID.randomUUID();
        var remocion = RemocionItemCuantitativoAsesorDomain.crear(itemCuantitativoAsesor);
        when(itemExisteFinder.obtener(itemCuantitativoAsesor)).thenReturn(true);

        // Act
        useCase.ejecutar(remocion);

        // Assert
        var orden = inOrder(logger, itemExisteFinder, validator, outputPort);
        orden.verify(logger).info(ItemCuantitativoAsesorKey.LOG_REMOVIENDO, itemCuantitativoAsesor);
        orden.verify(itemExisteFinder).obtener(itemCuantitativoAsesor);
        orden.verify(logger).debug(ItemCuantitativoAsesorKey.LOG_VERIFICACION_REMOVER, true);
        orden.verify(validator).validar(itemCuantitativoAsesor, true);
        orden.verify(outputPort).marcarEliminado(itemCuantitativoAsesor, remocion.getEliminadoEn());
        orden.verify(logger).info(ItemCuantitativoAsesorKey.LOG_REMOVIDO, itemCuantitativoAsesor);
    }

    @Test
    void debeRegistrarFechaDeEliminacion_cuandoSeCreaLaRemocion() {
        // Arrange
        var antes = Instant.now();

        // Act
        var remocion = RemocionItemCuantitativoAsesorDomain.crear(UUID.randomUUID());

        // Assert
        assertThat(remocion.getEliminadoEn()).isBetween(antes, Instant.now());
    }

    @Test
    void debeNoMarcarEliminado_cuandoValidatorLanza() {
        // Arrange
        var itemCuantitativoAsesor = UUID.randomUUID();
        var remocion = RemocionItemCuantitativoAsesorDomain.crear(itemCuantitativoAsesor);
        when(itemExisteFinder.obtener(itemCuantitativoAsesor)).thenReturn(false);
        doThrow(new ItemCuantitativoAsesorNoEncontradoException(itemCuantitativoAsesor))
                .when(validator).validar(itemCuantitativoAsesor, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(remocion))
                .isInstanceOf(ItemCuantitativoAsesorNoEncontradoException.class);
        verify(outputPort, never()).marcarEliminado(any(), any());
        verify(logger, never()).info(ItemCuantitativoAsesorKey.LOG_REMOVIDO, itemCuantitativoAsesor);
    }
}
