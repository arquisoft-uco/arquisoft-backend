package com.arquisoft.entregables.application.estadoentregable.query.usecase.impl;

import com.arquisoft.entregables.application.estadoentregable.query.readmodel.EstadoEntregableReadModel;
import com.arquisoft.entregables.application.estadoentregable.query.secondaryport.EstadoEntregableQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EstadoEntregableKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosEntregableUseCaseImplTest {

    @Mock
    private EstadoEntregableQueryOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosEntregableUseCaseImpl useCase;

    @Test
    void debeRetornarLosEstadosYRegistrarElTotal_cuandoElPuertoLosEntrega() {
        // Arrange
        var estados = List.of(
                new EstadoEntregableReadModel("DISPONIBLE_EVALUACION", "Disponible Evaluacion", "d1"),
                new EstadoEntregableReadModel("EN_EVALUACION", "En Evaluacion", "d2"),
                new EstadoEntregableReadModel("CERRADO", "Cerrado", "d3"));
        when(outputPort.findAll()).thenReturn(estados);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(estados);
        verify(outputPort, times(1)).findAll();
        verify(logger).debug(eq(EstadoEntregableKey.LOG_CONSULTA_COMPLETADA), eq(3));
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayEstados() {
        // Arrange
        when(outputPort.findAll()).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(EstadoEntregableKey.LOG_CONSULTA_COMPLETADA), eq(0));
    }
}
