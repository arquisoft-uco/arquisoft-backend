package com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.model.RemoverJuradosEvaluacionCommand;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.RemoverJuradosEvaluacionUseCase;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.RemocionJuradosEvaluacionDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoverJuradosEvaluacionInteractorImplTest {

    @Mock
    private RemoverJuradosEvaluacionUseCase useCase;

    @InjectMocks
    private RemoverJuradosEvaluacionInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConLaRemocion_cuandoSeEjecuta() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var command = new RemoverJuradosEvaluacionCommand(evaluacion, List.of(jurado));

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(RemocionJuradosEvaluacionDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getEvaluacion()).isEqualTo(evaluacion);
        assertThat(captor.getValue().getJurados()).containsExactly(jurado);
    }
}
