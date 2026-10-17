package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.ContextoRegistroEvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.ContextoRegistroEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContextoRegistroEvaluacionJuradoFinderImplTest {

    @Mock
    private ContextoRegistroEvaluacionJuradoOutputPort outputPort;

    @InjectMocks
    private ContextoRegistroEvaluacionJuradoFinderImpl finder;

    @Test
    void debeRetornarDominio_cuandoElContextoExiste() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var evaluacion = UUID.randomUUID();
        var entregable = UUID.randomUUID();
        var contexto = new ContextoRegistroEvaluacionJuradoEntity(
                evaluacionJurado, evaluacion, "PENDIENTE", entregable);
        when(outputPort.obtenerContextoBloqueado(evaluacionJurado)).thenReturn(Optional.of(contexto));

        // Act
        var resultado = finder.obtener(evaluacionJurado);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(evaluacionJurado);
        assertThat(resultado.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(resultado.getEstado()).isEqualTo(EstadoEvaluacion.PENDIENTE);
        assertThat(resultado.getEntregable()).isEqualTo(entregable);
        verify(outputPort).obtenerContextoBloqueado(evaluacionJurado);
    }

    @Test
    void debeRetornarVacio_cuandoElContextoNoExiste() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        when(outputPort.obtenerContextoBloqueado(evaluacionJurado)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(evaluacionJurado);

        // Assert
        assertThat(resultado.esVacio()).isTrue();
    }
}
