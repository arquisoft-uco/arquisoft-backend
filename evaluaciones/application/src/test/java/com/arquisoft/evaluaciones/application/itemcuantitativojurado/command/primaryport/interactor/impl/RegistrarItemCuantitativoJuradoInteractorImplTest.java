package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model.RegistrarItemCuantitativoJuradoCommand;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.usecase.RegistrarItemCuantitativoJuradoUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarItemCuantitativoJuradoInteractorImplTest {

    @Mock
    private RegistrarItemCuantitativoJuradoUseCase useCase;

    @InjectMocks
    private RegistrarItemCuantitativoJuradoInteractorImpl interactor;

    @Test
    void debeMapearDelegarYRetornarId() {
        // Arrange
        UUID categoria = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        var command = RegistrarItemCuantitativoJuradoCommand.crear(
                "Calidad", "Descripción", categoria.toString(), 100);
        when(useCase.ejecutar(org.mockito.ArgumentMatchers.any())).thenReturn(id);

        // Act
        UUID resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(
                com.arquisoft.evaluaciones.domain.itemcuantitativojurado
                        .ItemCuantitativoJuradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(resultado).isEqualTo(id);
        assertThat(captor.getValue().getNombre()).isEqualTo(command.nombre());
        assertThat(captor.getValue().getCategoria()).isEqualTo(categoria);
        assertThat(captor.getValue().getValor()).isEqualTo(100);
    }
}
