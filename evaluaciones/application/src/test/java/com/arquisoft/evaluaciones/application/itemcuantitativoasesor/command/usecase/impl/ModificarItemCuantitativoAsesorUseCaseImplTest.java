package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.ItemCuantitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.ItemCuantitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator.ModificarItemCuantitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ModificacionItemCuantitativoAsesorDomain;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.ItemCuantitativoAsesorNoEncontradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoAsesorKey;
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
class ModificarItemCuantitativoAsesorUseCaseImplTest {

    @Mock
    private ItemCuantitativoAsesorOutputPort outputPort;

    @Mock
    private ItemCuantitativoAsesorExisteFinder finder;

    @Mock
    private ModificarItemCuantitativoAsesorValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ModificarItemCuantitativoAsesorUseCaseImpl useCase;

    @Test
    void debeActualizarDescripcionEnOrden_cuandoItemExiste() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var modificacion = ModificacionItemCuantitativoAsesorDomain.crear(item, "Descripción ampliada");
        when(finder.obtener(item)).thenReturn(true);

        // Act
        useCase.ejecutar(modificacion);

        // Assert
        var orden = inOrder(logger, finder, validator, outputPort);
        orden.verify(logger).info(ItemCuantitativoAsesorKey.LOG_MODIFICANDO, item);
        orden.verify(finder).obtener(item);
        orden.verify(logger).debug(ItemCuantitativoAsesorKey.LOG_VERIFICACION_MODIFICAR, true);
        orden.verify(validator).validar(item, true);
        orden.verify(outputPort).actualizarDescripcion(item, "Descripción ampliada");
        orden.verify(logger).info(ItemCuantitativoAsesorKey.LOG_MODIFICADO, item);
        verify(finder, times(1)).obtener(item);
    }

    @Test
    void noDebePersistirNiCerrar_cuandoElValidatorRechaza() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var modificacion = ModificacionItemCuantitativoAsesorDomain.crear(item, "Descripción ampliada");
        when(finder.obtener(item)).thenReturn(false);
        doThrow(new ItemCuantitativoAsesorNoEncontradoException(item))
                .when(validator).validar(item, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(modificacion))
                .isInstanceOf(ItemCuantitativoAsesorNoEncontradoException.class);
        verify(outputPort, never()).actualizarDescripcion(any(), anyString());
        verify(logger, never()).info(ItemCuantitativoAsesorKey.LOG_MODIFICADO, item);
    }
}
