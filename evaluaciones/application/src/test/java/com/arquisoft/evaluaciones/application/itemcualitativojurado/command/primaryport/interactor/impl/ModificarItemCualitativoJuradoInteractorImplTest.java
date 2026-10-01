package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model.ModificarItemCualitativoJuradoCommand;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase.ModificarItemCualitativoJuradoUseCase;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.ModificacionItemCualitativoJuradoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModificarItemCualitativoJuradoInteractorImplTest {

    @Mock
    private ModificarItemCualitativoJuradoUseCase useCase;

    @InjectMocks
    private ModificarItemCualitativoJuradoInteractorImpl interactor;

    @Test
    void debeMapearYDelegar_cuandoEjecutaCommand() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        var command = ModificarItemCualitativoJuradoCommand.crear(itemId, "Nueva descripción");

        // Act
        interactor.ejecutar(command);

        // Assert
        ArgumentCaptor<ModificacionItemCualitativoJuradoDomain> captor =
                ArgumentCaptor.forClass(ModificacionItemCualitativoJuradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getItemCualitativoJurado()).isEqualTo(itemId);
        assertThat(captor.getValue().getDescripcion()).isEqualTo(command.descripcion());
    }
}
