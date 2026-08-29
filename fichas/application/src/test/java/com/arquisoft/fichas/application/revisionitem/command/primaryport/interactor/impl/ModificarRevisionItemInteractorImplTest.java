package com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.ModificarRevisionItemCommand;
import com.arquisoft.fichas.application.revisionitem.command.usecase.ModificarRevisionItemUseCase;
import com.arquisoft.fichas.domain.revisionitem.ModificacionRevisionItemDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModificarRevisionItemInteractorImplTest {

    @Mock
    private ModificarRevisionItemUseCase modificarRevisionItemUseCase;

    @InjectMocks
    private ModificarRevisionItemInteractorImpl modificarRevisionItemInteractor;

    @Test
    void debeDelegarEnElUseCase_cuandoEjecuta() {
        // Arrange
        var command = ModificarRevisionItemCommand.crear(
                UUID.randomUUID(), "VISUALIZADA", UUID.randomUUID());
        doNothing().when(modificarRevisionItemUseCase).ejecutar(any(ModificacionRevisionItemDomain.class));

        // Act
        modificarRevisionItemInteractor.ejecutar(command);

        // Assert
        verify(modificarRevisionItemUseCase).ejecutar(any(ModificacionRevisionItemDomain.class));
    }

    @Test
    void debeConstruirLaModificacionConLosDatosDelCommand_cuandoEjecuta() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID asesorFicha = UUID.randomUUID();
        var command = ModificarRevisionItemCommand.crear(item, "VISUALIZADA", asesorFicha);
        doNothing().when(modificarRevisionItemUseCase).ejecutar(any(ModificacionRevisionItemDomain.class));

        // Act
        modificarRevisionItemInteractor.ejecutar(command);

        // Assert
        ArgumentCaptor<ModificacionRevisionItemDomain> captor =
                ArgumentCaptor.forClass(ModificacionRevisionItemDomain.class);
        verify(modificarRevisionItemUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getItem()).isEqualTo(item);
        assertThat(captor.getValue().getAsesorFicha()).isEqualTo(asesorFicha);
    }
}
