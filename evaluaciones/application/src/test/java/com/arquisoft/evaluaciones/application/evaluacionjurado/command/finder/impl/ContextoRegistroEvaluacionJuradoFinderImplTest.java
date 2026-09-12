package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.ContextoRegistroEvaluacionJuradoEntity;
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
    private EvaluacionJuradoOutputPort outputPort;

    @InjectMocks
    private ContextoRegistroEvaluacionJuradoFinderImpl finder;

    @Test
    void debeDelegarEnPuerto_cuandoElContextoExiste() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        var contexto = new ContextoRegistroEvaluacionJuradoEntity(
                evaluacionJurado, UUID.randomUUID(), UUID.randomUUID(), "PENDIENTE",
                UUID.randomUUID(), "Proyecto X", 1);
        when(outputPort.obtenerContextoBloqueado(evaluacionJurado)).thenReturn(Optional.of(contexto));

        // Act
        Optional<ContextoRegistroEvaluacionJuradoEntity> resultado = finder.obtener(evaluacionJurado);

        // Assert
        assertThat(resultado).contains(contexto);
        verify(outputPort).obtenerContextoBloqueado(evaluacionJurado);
    }

    @Test
    void debeRetornarVacio_cuandoElContextoNoExiste() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        when(outputPort.obtenerContextoBloqueado(evaluacionJurado)).thenReturn(Optional.empty());

        // Act
        Optional<ContextoRegistroEvaluacionJuradoEntity> resultado = finder.obtener(evaluacionJurado);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
