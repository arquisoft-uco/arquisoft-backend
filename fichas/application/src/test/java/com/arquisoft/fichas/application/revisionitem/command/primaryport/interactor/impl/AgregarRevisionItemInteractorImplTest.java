package com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.AgregarRevisionItemCommand;
import com.arquisoft.fichas.application.revisionitem.command.usecase.AgregarRevisionItemUseCase;
import com.arquisoft.fichas.domain.revisionitem.AgregacionRevisionItemDomain;
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
class AgregarRevisionItemInteractorImplTest {

    @Mock
    private AgregarRevisionItemUseCase agregarRevisionItemUseCase;

    @InjectMocks
    private AgregarRevisionItemInteractorImpl agregarRevisionItemInteractor;

    @Test
    void debeDelegarEnElUseCase_cuandoEjecuta() {
        // Arrange
        var command = AgregarRevisionItemCommand.crear(
                UUID.randomUUID(), UUID.randomUUID());
        UUID revisionItemId = UUID.randomUUID();
        when(agregarRevisionItemUseCase.ejecutar(any(AgregacionRevisionItemDomain.class)))
                .thenReturn(revisionItemId);

        // Act
        UUID resultado = agregarRevisionItemInteractor.ejecutar(command);

        // Assert
        assertThat(resultado).isEqualTo(revisionItemId);
    }

    @Test
    void debeConstruirLaAgregacionConLosDatosDelCommand_cuandoEjecuta() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID asesorFicha = UUID.randomUUID();
        var command = AgregarRevisionItemCommand.crear(item, asesorFicha);
        when(agregarRevisionItemUseCase.ejecutar(any(AgregacionRevisionItemDomain.class)))
                .thenReturn(UUID.randomUUID());

        // Act
        agregarRevisionItemInteractor.ejecutar(command);

        // Assert
        ArgumentCaptor<AgregacionRevisionItemDomain> captor =
                ArgumentCaptor.forClass(AgregacionRevisionItemDomain.class);
        verify(agregarRevisionItemUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getItem()).isEqualTo(item);
        assertThat(captor.getValue().getAsesorFicha()).isEqualTo(asesorFicha);
    }
}
