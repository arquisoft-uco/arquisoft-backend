package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model.RegistrarItemCualitativoJuradoCommand;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase.RegistrarItemCualitativoJuradoUseCase;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.ItemCualitativoJuradoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarItemCualitativoJuradoInteractorImplTest {

    @Mock
    private RegistrarItemCualitativoJuradoUseCase useCase;

    @InjectMocks
    private RegistrarItemCualitativoJuradoInteractorImpl interactor;

    @Test
    void debeMapearDelegarYRetornarId_cuandoEjecutaCommand() {
        // Arrange
        var command = new RegistrarItemCualitativoJuradoCommand("Claridad", "Descripción");
        UUID id = UUID.randomUUID();
        when(useCase.ejecutar(any(ItemCualitativoJuradoDomain.class))).thenReturn(id);

        // Act
        UUID resultado = interactor.ejecutar(command);

        // Assert
        ArgumentCaptor<ItemCualitativoJuradoDomain> captor =
                ArgumentCaptor.forClass(ItemCualitativoJuradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(resultado).isEqualTo(id);
        assertThat(captor.getValue().getNombre()).isEqualTo(command.nombre());
        assertThat(captor.getValue().getDescripcion()).isEqualTo(command.descripcion());
    }
}
