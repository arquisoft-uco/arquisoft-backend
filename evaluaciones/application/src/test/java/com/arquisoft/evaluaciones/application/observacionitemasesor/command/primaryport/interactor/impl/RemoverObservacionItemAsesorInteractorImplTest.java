package com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model.RemoverObservacionItemAsesorCommand;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.usecase.RemoverObservacionItemAsesorUseCase;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.RemocionObservacionItemAsesorDomain;
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
class RemoverObservacionItemAsesorInteractorImplTest {

    @Mock
    private RemoverObservacionItemAsesorUseCase useCase;

    @InjectMocks
    private RemoverObservacionItemAsesorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_cuandoEjecuta() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = new RemoverObservacionItemAsesorCommand(observacion, asesor);
        var captor = ArgumentCaptor.forClass(RemocionObservacionItemAsesorDomain.class);

        // Act
        interactor.ejecutar(command);

        // Assert
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getObservacion()).isEqualTo(observacion);
        assertThat(captor.getValue().getAsesor()).isEqualTo(asesor);
    }
}
