package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.model.SolicitudEstadoEvaluacionJurado;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionJuradoEstadoFinderImplTest {

    @Mock
    private EvaluacionJuradoOutputPort outputPort;

    @InjectMocks
    private EvaluacionJuradoEstadoFinderImpl finder;

    @Test
    void debeDelegarConsultaDeEstado() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        var estado = new EstadoEvaluacionJuradoEntity(true, false);
        when(outputPort.obtenerEstado(evaluacionJurado, jurado)).thenReturn(estado);

        // Act
        var resultado = finder.obtener(new SolicitudEstadoEvaluacionJurado(evaluacionJurado, jurado));

        // Assert
        assertThat(resultado).isEqualTo(estado);
        verify(outputPort).obtenerEstado(evaluacionJurado, jurado);
    }
}
