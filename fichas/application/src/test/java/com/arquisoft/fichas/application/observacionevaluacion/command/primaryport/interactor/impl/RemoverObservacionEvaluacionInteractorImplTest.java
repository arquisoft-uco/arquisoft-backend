package com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.RemoverObservacionEvaluacionCommand;
import com.arquisoft.fichas.application.observacionevaluacion.command.usecase.RemoverObservacionEvaluacionUseCase;
import com.arquisoft.fichas.domain.observacionevaluacion.RemocionObservacionEvaluacionDomain;
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
class RemoverObservacionEvaluacionInteractorImplTest {

    @Mock
    private RemoverObservacionEvaluacionUseCase removerObservacionEvaluacionUseCase;

    @InjectMocks
    private RemoverObservacionEvaluacionInteractorImpl removerObservacionEvaluacionInteractor;

    @Test
    void debeDelegarEnUseCase_conDominioMapeado() {
        // Arrange
        var observacionEvaluacion = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();
        var command = RemoverObservacionEvaluacionCommand.crear(observacionEvaluacion, representanteComite);

        // Act
        removerObservacionEvaluacionInteractor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(RemocionObservacionEvaluacionDomain.class);
        verify(removerObservacionEvaluacionUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getObservacionEvaluacion()).isEqualTo(observacionEvaluacion);
        assertThat(captor.getValue().getRepresentanteComite()).isEqualTo(representanteComite);
    }
}
