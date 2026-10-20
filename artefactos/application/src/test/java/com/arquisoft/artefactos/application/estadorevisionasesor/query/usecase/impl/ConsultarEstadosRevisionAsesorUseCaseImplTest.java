package com.arquisoft.artefactos.application.estadorevisionasesor.query.usecase.impl;

import com.arquisoft.artefactos.application.estadorevisionasesor.query.readmodel.EstadoRevisionAsesorReadModel;
import com.arquisoft.artefactos.application.estadorevisionasesor.query.secondaryport.EstadoRevisionAsesorQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosRevisionAsesorUseCaseImplTest {

    @Mock
    private EstadoRevisionAsesorQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosRevisionAsesorUseCaseImpl useCase;

    @Test
    void debeRetornarLosEstadosYLogearElTotal_cuandoElPuertoLosDevuelve() {
        // Arrange
        var estados = List.of(
                new EstadoRevisionAsesorReadModel("PENDIENTE", "Pendiente", "Nueva revisión"),
                new EstadoRevisionAsesorReadModel("EN_PROGRESO", "En progreso", "Ajustes iniciados"),
                new EstadoRevisionAsesorReadModel("RESUELTA", "Resuelta", "Ajustes finalizados"));
        when(queryOutputPort.consultarTodos()).thenReturn(estados);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).containsExactlyElementsOf(estados);
        verify(queryOutputPort, times(1)).consultarTodos();
        verify(logger).debug(any(ClaveMensaje.class), eq(3));
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayEstados() {
        // Arrange
        when(queryOutputPort.consultarTodos()).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(any(ClaveMensaje.class), eq(0));
    }
}
