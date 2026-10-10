package com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.RemoverObservacionItemCommand;
import com.arquisoft.fichas.application.observacionitem.command.usecase.RemoverObservacionItemUseCase;
import com.arquisoft.fichas.domain.observacionitem.RemocionObservacionItemDomain;
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
class RemoverObservacionItemInteractorImplTest {

    @Mock
    private RemoverObservacionItemUseCase removerObservacionItemUseCase;

    @InjectMocks
    private RemoverObservacionItemInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnElUseCase_cuandoEjecuta() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var command = RemoverObservacionItemCommand.crear(observacionItem, asesorFicha);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(RemocionObservacionItemDomain.class);
        verify(removerObservacionItemUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getObservacionItem()).isEqualTo(observacionItem);
        assertThat(captor.getValue().getAsesorFicha()).isEqualTo(asesorFicha);
    }
}
