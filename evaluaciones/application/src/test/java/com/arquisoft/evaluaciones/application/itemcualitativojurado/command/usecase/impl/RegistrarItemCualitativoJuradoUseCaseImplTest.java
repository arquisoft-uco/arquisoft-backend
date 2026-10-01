package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase.impl;

import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.NombreItemCualitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.ItemCualitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.entity.ItemCualitativoJuradoEntity;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.RegistrarItemCualitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.ItemCualitativoJuradoDomain;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception.NombreItemCualitativoJuradoDuplicadoException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

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
class RegistrarItemCualitativoJuradoUseCaseImplTest {

    @Mock
    private ItemCualitativoJuradoOutputPort outputPort;

    @Mock
    private NombreItemCualitativoJuradoExisteFinder finder;

    @Mock
    private RegistrarItemCualitativoJuradoValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarItemCualitativoJuradoUseCaseImpl useCase;

    @Test
    void debePersistirYRetornarId_cuandoNombreEstaDisponible() {
        // Arrange
        ItemCualitativoJuradoDomain item = itemValido();
        when(finder.obtener(item.getNombre())).thenReturn(false);

        // Act
        UUID resultado = useCase.ejecutar(item);

        // Assert
        assertThat(resultado).isEqualTo(item.getId());
        InOrder orden = inOrder(finder, validator, outputPort, logger);
        orden.verify(finder).obtener(item.getNombre());
        orden.verify(validator).validar(item, false);
        orden.verify(outputPort).registrar(entidadDe(item));
        orden.verify(logger).info(any(ClaveMensaje.class), eq(item.getId()));
    }

    @Test
    void debeDetenerFlujo_cuandoNombreYaExiste() {
        // Arrange
        ItemCualitativoJuradoDomain item = itemValido();
        when(finder.obtener(item.getNombre())).thenReturn(true);
        doThrow(new NombreItemCualitativoJuradoDuplicadoException(item.getNombre()))
                .when(validator).validar(item, true);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(item))
                .isInstanceOf(NombreItemCualitativoJuradoDuplicadoException.class);
        verify(outputPort, never()).registrar(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(item.getId()));
    }

    @Test
    void debeDetenerFlujo_cuandoPersistenciaFalla() {
        // Arrange
        ItemCualitativoJuradoDomain item = itemValido();
        var errorPersistencia = new RuntimeException("Error de persistencia");
        when(finder.obtener(item.getNombre())).thenReturn(false);
        doThrow(errorPersistencia)
                .when(outputPort).registrar(entidadDe(item));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(item))
                .isSameAs(errorPersistencia);
        verify(logger, never()).info(any(ClaveMensaje.class), eq(item.getId()));
    }

    private static ItemCualitativoJuradoDomain itemValido() {
        return ItemCualitativoJuradoDomain.crear("Claridad", "Descripción");
    }

    private static ItemCualitativoJuradoEntity entidadDe(ItemCualitativoJuradoDomain item) {
        return argThat(entity -> entity.id().equals(item.getId())
                && entity.nombre().equals(item.getNombre())
                && entity.descripcion().equals(item.getDescripcion()));
    }
}
