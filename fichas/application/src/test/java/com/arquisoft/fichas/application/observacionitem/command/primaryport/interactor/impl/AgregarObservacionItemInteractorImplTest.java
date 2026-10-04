package com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.AgregarObservacionItemCommand;
import com.arquisoft.fichas.application.observacionitem.command.usecase.AgregarObservacionItemUseCase;
import com.arquisoft.fichas.domain.observacionitem.AgregacionObservacionItemDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarObservacionItemInteractorImplTest {

    @Mock
    private AgregarObservacionItemUseCase agregarObservacionItemUseCase;

    @InjectMocks
    private AgregarObservacionItemInteractorImpl agregarObservacionItemInteractor;

    @Test
    void debeDelegarEnElUseCase_cuandoEjecuta() {
        // Arrange
        var command = AgregarObservacionItemCommand.crear(
                UUID.randomUUID(), "Observación válida", UUID.randomUUID());
        var observacionItemId = UUID.randomUUID();
        when(agregarObservacionItemUseCase.ejecutar(any(AgregacionObservacionItemDomain.class)))
                .thenReturn(observacionItemId);

        // Act
        var resultado = agregarObservacionItemInteractor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(observacionItemId);
        verify(agregarObservacionItemUseCase).ejecutar(any(AgregacionObservacionItemDomain.class));
    }
}
