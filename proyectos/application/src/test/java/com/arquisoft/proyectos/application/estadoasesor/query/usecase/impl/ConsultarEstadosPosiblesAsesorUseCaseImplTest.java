package com.arquisoft.proyectos.application.estadoasesor.query.usecase.impl;

import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.proyectos.application.estadoasesor.query.secondaryport.EstadoAsesorQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstadoAsesorKey;
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
class ConsultarEstadosPosiblesAsesorUseCaseImplTest {

    @Mock
    private EstadoAsesorQueryOutputPort estadoAsesorQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosPosiblesAsesorUseCaseImpl useCase;

    @Test
    void debeRetornarLosEstadosDelPuertoYLoguearElCierre_cuandoExisten() {
        // Arrange
        var esperado = List.of(
                new EstadoAsesorReadModel("ASIGNADO", "Asignado", "Asesor asignado"),
                new EstadoAsesorReadModel("FINALIZADO", "Finalizado", "Asesoria finalizada"));
        when(estadoAsesorQueryOutputPort.consultarTodos()).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(estadoAsesorQueryOutputPort, times(1)).consultarTodos();
        verify(logger).debug(eq(EstadoAsesorKey.LOG_CONSULTA_COMPLETADA), eq(2));
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayEstados() {
        // Arrange
        when(estadoAsesorQueryOutputPort.consultarTodos()).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(EstadoAsesorKey.LOG_CONSULTA_COMPLETADA), eq(0));
    }
}
