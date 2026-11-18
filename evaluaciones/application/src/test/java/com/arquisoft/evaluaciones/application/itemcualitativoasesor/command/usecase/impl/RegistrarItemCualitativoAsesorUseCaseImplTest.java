package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.finder.NombreItemCualitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.ItemCualitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.entity.ItemCualitativoAsesorEntity;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator.RegistrarItemCualitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.ItemCualitativoAsesorDomain;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.NombreItemCualitativoAsesorDuplicadoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarItemCualitativoAsesorUseCaseImplTest {

    @Mock
    private ItemCualitativoAsesorOutputPort outputPort;

    @Mock
    private NombreItemCualitativoAsesorExisteFinder finder;

    @Mock
    private RegistrarItemCualitativoAsesorValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarItemCualitativoAsesorUseCaseImpl useCase;

    @Test
    void debeRegistrarYRetornarId_cuandoNombreDisponible() {
        // Arrange
        var item = itemValido();
        when(finder.obtener(item.getNombre())).thenReturn(false);

        // Act
        var resultado = useCase.ejecutar(item);

        // Assert
        assertThat(resultado).isEqualTo(item.getId());
        var orden = inOrder(finder, validator, outputPort, logger);
        orden.verify(finder).obtener(item.getNombre());
        orden.verify(validator).validar(item, false);
        orden.verify(outputPort).registrar(entidadDe(item));
        orden.verify(logger).info(any(ClaveMensaje.class), eq(item.getId()));
    }

    @Test
    void noDebePersistir_cuandoValidatorRechaza() {
        // Arrange
        var item = itemValido();
        when(finder.obtener(item.getNombre())).thenReturn(true);
        doThrow(new NombreItemCualitativoAsesorDuplicadoException(item.getNombre()))
                .when(validator).validar(item, true);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(item))
                .isInstanceOf(NombreItemCualitativoAsesorDuplicadoException.class);
        verify(outputPort, never()).registrar(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(item.getId()));
    }

    private static ItemCualitativoAsesorDomain itemValido() {
        return ItemCualitativoAsesorDomain.crear("Claridad", "Descripción");
    }

    private static ItemCualitativoAsesorEntity entidadDe(ItemCualitativoAsesorDomain item) {
        return argThat(entity -> entity.id().equals(item.getId())
                && entity.nombre().equals(item.getNombre())
                && entity.descripcion().equals(item.getDescripcion()));
    }
}
