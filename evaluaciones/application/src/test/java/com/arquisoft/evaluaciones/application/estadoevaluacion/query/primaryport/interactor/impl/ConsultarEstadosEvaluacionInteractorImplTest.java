package com.arquisoft.evaluaciones.application.estadoevaluacion.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.evaluaciones.application.estadoevaluacion.query.usecase.ConsultarEstadosEvaluacionUseCase;
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
    private ConsultarEstadosEvaluacionUseCase useCase;

    @InjectMocks
    private ConsultarEstadosEvaluacionInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_cuandoSeEjecuta() {
        // Arrange
        var esperados = List.of(
                new EstadoEvaluacionReadModel("PENDIENTE", "Pendiente", "Indica que una evaluación está pendiente por realizar")
        );
        when(useCase.ejecutar()).thenReturn(esperados);

        // Act
        var resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(useCase).ejecutar();
    }

    @Test
    void debePropagarListaVacia_cuandoElUseCaseNoEncuentraRegistros() {
        // Arrange
        when(useCase.ejecutar()).thenReturn(List.of());

        // Act
        var resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).isEmpty();
        verify(useCase).ejecutar();
    }
}
