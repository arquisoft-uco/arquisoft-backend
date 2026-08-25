package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.NombreItemCualitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.ItemCualitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.entity.ItemCualitativoJuradoEntity;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.RegistrarItemCualitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.ItemCualitativoJuradoDomain;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception.NombreItemCualitativoJuradoDuplicadoException;
import com.arquisoft.shared.events.EventPublisher;
import com.arquisoft.shared.exception.InfrastructureException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
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
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarItemCualitativoJuradoUseCaseImpl useCase;

    @Test
    void debePersistirPublicarYRetornarId_cuandoNombreEstaDisponible() {
        // Arrange
        ItemCualitativoJuradoDomain item = itemValido();
        when(finder.obtener(item.getNombre())).thenReturn(false);

        // Act
        UUID resultado = useCase.ejecutar(item);

        // Assert
        assertThat(resultado).isEqualTo(item.getId());
        InOrder orden = inOrder(finder, validator, outputPort, eventPublisher, logger);
        orden.verify(finder).obtener(item.getNombre());
        orden.verify(validator).validar(item, false);
        orden.verify(outputPort).registrar(entidadDe(item));
        orden.verify(eventPublisher).publish(any());
        orden.verify(logger).info(anyString(), eq(item.getId()));
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
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(anyString(), any());
    }

    @Test
    void debeEvitarPublicacion_cuandoPersistenciaFalla() {
        // Arrange
        ItemCualitativoJuradoDomain item = itemValido();
        when(finder.obtener(item.getNombre())).thenReturn(false);
        doThrow(new InfrastructureException("Error de persistencia", "ERROR_DB"))
                .when(outputPort).registrar(entidadDe(item));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(item))
                .isInstanceOf(InfrastructureException.class);
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(anyString(), any());
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
