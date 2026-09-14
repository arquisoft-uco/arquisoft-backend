package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.EvaluacionCuantitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.entity.EvaluacionCuantitativaJuradoEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionCuantitativaJuradoPorIdFinderImplTest {

    @Mock
    private EvaluacionCuantitativaJuradoOutputPort outputPort;

    @InjectMocks
    private EvaluacionCuantitativaJuradoPorIdFinderImpl finder;

    @Test
    void debeRetornarDomain_cuandoLaEvaluacionExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        var entity = new EvaluacionCuantitativaJuradoEntity(id, UUID.randomUUID(), UUID.randomUUID(), 300);
        when(outputPort.obtenerPorId(id)).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(id);
        assertThat(resultado.get().getEvaluacionJurado()).isEqualTo(entity.evaluacionJurado());
        assertThat(resultado.get().getItem()).isEqualTo(entity.item());
        assertThat(resultado.get().getPuntaje()).isEqualTo(300);
    }

    @Test
    void debeRetornarVacio_cuandoLaEvaluacionNoExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(outputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThat(finder.obtener(id)).isEmpty();
    }
}
