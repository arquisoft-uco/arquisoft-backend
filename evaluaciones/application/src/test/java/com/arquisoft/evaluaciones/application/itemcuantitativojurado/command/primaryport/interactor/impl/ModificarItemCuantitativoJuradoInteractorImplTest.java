package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model.ModificarItemCuantitativoJuradoCommand;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.usecase.ModificarItemCuantitativoJuradoUseCase;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ModificacionItemCuantitativoJuradoDomain;
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
class ModificarItemCuantitativoJuradoInteractorImplTest {

    @Mock
    private ModificarItemCuantitativoJuradoUseCase useCase;

    @InjectMocks
    private ModificarItemCuantitativoJuradoInteractorImpl interactor;

    @Test
    void debeMapearYDelegar_cuandoEjecutaCommand() {
        // Arrange
        var itemId = UUID.randomUUID();
        var command = ModificarItemCuantitativoJuradoCommand.crear(itemId, "Nueva descripción");

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(ModificacionItemCuantitativoJuradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getItemCuantitativoJurado()).isEqualTo(itemId);
        assertThat(captor.getValue().getDescripcion()).isEqualTo(command.descripcion());
    }
}
