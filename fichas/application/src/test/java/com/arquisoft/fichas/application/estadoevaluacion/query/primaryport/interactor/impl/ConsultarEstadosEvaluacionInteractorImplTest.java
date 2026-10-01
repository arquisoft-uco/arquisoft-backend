package com.arquisoft.fichas.application.estadoevaluacion.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.fichas.application.estadoevaluacion.query.usecase.ConsultarEstadosEvaluacionUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosEvaluacionInteractorImplTest {

    @Mock
    private ConsultarEstadosEvaluacionUseCase consultarEstadosEvaluacionUseCase;

    @InjectMocks
    private ConsultarEstadosEvaluacionInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        List<EstadoEvaluacionReadModel> esperados = List.of(
                new EstadoEvaluacionReadModel("EN_EVALUACION", "En Evaluacion", "Descripcion de ejemplo."));
        when(consultarEstadosEvaluacionUseCase.ejecutar()).thenReturn(esperados);

        // Act
        List<EstadoEvaluacionReadModel> resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(esperados);
        verify(consultarEstadosEvaluacionUseCase).ejecutar();
    }
}
