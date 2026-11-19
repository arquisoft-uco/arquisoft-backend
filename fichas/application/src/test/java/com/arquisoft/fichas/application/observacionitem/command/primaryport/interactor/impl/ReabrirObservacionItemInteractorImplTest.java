package com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.ReabrirObservacionItemCommand;
import com.arquisoft.fichas.application.observacionitem.command.usecase.ReabrirObservacionItemUseCase;
import com.arquisoft.fichas.domain.observacionitem.ReaperturaObservacionItemDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReabrirObservacionItemInteractorImplTest {

    @Mock
    private ReabrirObservacionItemUseCase reabrirObservacionItemUseCase;

    @InjectMocks
    private ReabrirObservacionItemInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCase_conElObjetoDeAccion() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var command = ReabrirObservacionItemCommand.crear(observacionItem, asesorFicha);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(ReaperturaObservacionItemDomain.class);
        verify(reabrirObservacionItemUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getObservacionItem()).isEqualTo(observacionItem);
        assertThat(captor.getValue().getAsesorFicha()).isEqualTo(asesorFicha);
    }
}
