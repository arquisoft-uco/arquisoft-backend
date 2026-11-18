package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.finder.ItemCualitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.ItemCualitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator.ModificarItemCualitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.ModificacionItemCualitativoAsesorDomain;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificarItemCualitativoAsesorUseCaseImplTest {

    @Mock
    private ItemCualitativoAsesorOutputPort outputPort;

    @Mock
    private ItemCualitativoAsesorExisteFinder finder;

    @Mock
    private ModificarItemCualitativoAsesorValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ModificarItemCualitativoAsesorUseCaseImpl useCase;

    @Test
    void debeActualizarDescripcionEnOrden_cuandoItemExiste() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var modificacion = ModificacionItemCualitativoAsesorDomain.crear(item, "Descripción ampliada");
        when(finder.obtener(item)).thenReturn(true);

        // Act
        useCase.ejecutar(modificacion);

        // Assert
        var orden = inOrder(logger, finder, validator, outputPort);
        orden.verify(logger).info(ItemCualitativoAsesorKey.LOG_MODIFICANDO, item);
        orden.verify(finder).obtener(item);
        orden.verify(logger).debug(ItemCualitativoAsesorKey.LOG_VERIFICACION_MODIFICAR, true);
        orden.verify(validator).validar(item, true);
        orden.verify(outputPort).actualizarDescripcion(item, "Descripción ampliada");
        orden.verify(logger).info(ItemCualitativoAsesorKey.LOG_MODIFICADO, item);
        verify(finder, times(1)).obtener(item);
    }

    @Test
    void noDebePersistirNiCerrar_cuandoElValidatorRechaza() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var modificacion = ModificacionItemCualitativoAsesorDomain.crear(item, "Descripción ampliada");
        when(finder.obtener(item)).thenReturn(false);
        doThrow(new ItemCualitativoAsesorNoEncontradoException(item))
                .when(validator).validar(item, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(modificacion))
                .isInstanceOf(ItemCualitativoAsesorNoEncontradoException.class);
        verify(outputPort, never()).actualizarDescripcion(any(), anyString());
        verify(logger, never()).info(ItemCualitativoAsesorKey.LOG_MODIFICADO, item);
    }
}
