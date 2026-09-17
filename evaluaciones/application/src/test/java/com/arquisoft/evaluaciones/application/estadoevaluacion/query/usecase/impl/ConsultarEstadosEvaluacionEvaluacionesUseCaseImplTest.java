package com.arquisoft.evaluaciones.application.estadoevaluacion.query.usecase.impl;

import com.arquisoft.evaluaciones.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.evaluaciones.application.estadoevaluacion.query.secondaryport.EstadoEvaluacionQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosEvaluacionEvaluacionesUseCaseImplTest {

    @Mock
    private EstadoEvaluacionQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosEvaluacionEvaluacionesUseCaseImpl useCase;

    @Test
    void debeRetornarTodosLosEstados_cuandoElPuertoDevuelveResultados() {
        // Arrange
        List<EstadoEvaluacionReadModel> esperados = List.of(
                new EstadoEvaluacionReadModel("PENDIENTE", "Pendiente", "Indica que una evaluación está pendiente por realizar"),
                new EstadoEvaluacionReadModel("EN_PROGRESO", "En progreso", "Indica que una evaluación está en curso"),
                new EstadoEvaluacionReadModel("FINALIZADA", "Finalizada", "Indica que una evaluación ha sido finalizada")
        );
        when(queryOutputPort.consultarTodos()).thenReturn(esperados);

        // Act
        List<EstadoEvaluacionReadModel> resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
    }

    @Test
    void debeRetornarListaVacia_cuandoElPuertoNoDevuelveResultados() {
        // Arrange
        when(queryOutputPort.consultarTodos()).thenReturn(List.of());

        // Act
        List<EstadoEvaluacionReadModel> resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeInvocarElPuertoUnaSolaVez() {
        // Arrange
        when(queryOutputPort.consultarTodos()).thenReturn(List.of());

        // Act
        useCase.ejecutar();

        // Assert
        verify(queryOutputPort, times(1)).consultarTodos();
        verifyNoMoreInteractions(queryOutputPort);
    }
}
