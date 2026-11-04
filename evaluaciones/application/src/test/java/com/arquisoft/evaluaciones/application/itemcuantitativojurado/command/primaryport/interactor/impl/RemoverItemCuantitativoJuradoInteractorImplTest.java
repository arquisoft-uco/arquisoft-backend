package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model.RemoverItemCuantitativoJuradoCommand;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.usecase.RemoverItemCuantitativoJuradoUseCase;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.RemocionItemCuantitativoJuradoDomain;
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
class RemoverItemCuantitativoJuradoInteractorImplTest {

    @Mock
    private RemoverItemCuantitativoJuradoUseCase useCase;

    @InjectMocks
    private RemoverItemCuantitativoJuradoInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCaseConRemocionMapeada_cuandoCommandEsValido() {
        // Arrange
        var itemId = UUID.randomUUID();
        var command = RemoverItemCuantitativoJuradoCommand.crear(itemId);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(RemocionItemCuantitativoJuradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getItemCuantitativoJurado()).isEqualTo(itemId);
    }
}
