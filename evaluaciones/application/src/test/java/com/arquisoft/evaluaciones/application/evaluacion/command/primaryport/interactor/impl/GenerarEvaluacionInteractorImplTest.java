package com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.model.GenerarEvaluacionCommand;
import com.arquisoft.evaluaciones.application.evaluacion.command.result.GeneracionEvaluacionResult;
import com.arquisoft.evaluaciones.application.evaluacion.command.usecase.GenerarEvaluacionUseCase;
import com.arquisoft.evaluaciones.domain.evaluacion.GeneracionEvaluacionDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerarEvaluacionInteractorImplTest {

    @Mock
    private GenerarEvaluacionUseCase generarEvaluacionUseCase;

    @InjectMocks
    private GenerarEvaluacionInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCaseConElDominioMapeado() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var command = GenerarEvaluacionCommand.crear(
                entregable.toString(), UtilUUID.generarNuevoUUID().toString(), "Sistema de riego", 1,
                UtilUUID.generarNuevoUUID().toString());
        var esperado = new GeneracionEvaluacionResult.Duplicada(entregable);
        when(generarEvaluacionUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(GeneracionEvaluacionDomain.class);
        verify(generarEvaluacionUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getEvaluacion().getEntregable()).isEqualTo(entregable);
        assertThat(resultado).isSameAs(esperado);
    }
}
