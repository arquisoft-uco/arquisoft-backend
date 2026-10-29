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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosAsesorUseCaseImplTest {

    @Mock
    private EstadoAsesorQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosAsesorUseCaseImpl useCase;

    @Test
    void debeRetornarLosEstadosDelPuertoYLoguearElTotal_cuandoExisten() {
        // Arrange
        var estados = List.of(
                new EstadoAsesorReadModel("ASIGNADO", "Asignado", "Asesor asignado"),
                new EstadoAsesorReadModel("FINALIZADO", "Finalizado", "Asesoria finalizada"),
                new EstadoAsesorReadModel("OTRO", "Otro", "Otro estado"));
        when(queryOutputPort.consultarTodos()).thenReturn(estados);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(estados);
        verify(queryOutputPort, times(1)).consultarTodos();
        verify(logger).debug(EstadoAsesorKey.LOG_CONSULTA_COMPLETADA, 3);
    }
}
