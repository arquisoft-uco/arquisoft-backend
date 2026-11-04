package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model.RemoverItemCualitativoJuradoCommand;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase.RemoverItemCualitativoJuradoUseCase;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.RemocionItemCualitativoJuradoDomain;
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
class RemoverItemCualitativoJuradoInteractorImplTest {

    @Mock
    private RemoverItemCualitativoJuradoUseCase useCase;

    @InjectMocks
    private RemoverItemCualitativoJuradoInteractorImpl interactor;

    @Test
    void debeMapearYDelegar_cuandoCommandEsValido() {
        // Arrange
        var itemId = UUID.randomUUID();
        var command = RemoverItemCualitativoJuradoCommand.crear(itemId);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(RemocionItemCualitativoJuradoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getItemCualitativoJurado()).isEqualTo(itemId);
    }
}
