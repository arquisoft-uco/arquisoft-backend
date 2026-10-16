package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.RegistrarObservacionItemJuradoCommand;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarObservacionItemJuradoMapperTest {

    @Test
    void debeConstruirObservacion_cuandoCommandEsValido() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var command = RegistrarObservacionItemJuradoCommand.crear(
                evaluacionCuantitativaJurado, "Sustenta el puntaje otorgado");

        // Act
        var observacion = RegistrarObservacionItemJuradoMapper.toDomain(command);

        // Assert
        assertThat(observacion.getId()).isNotNull();
        assertThat(observacion.getEvaluacionCuantitativaJurado()).isEqualTo(evaluacionCuantitativaJurado);
        assertThat(observacion.getDescripcion()).isEqualTo("Sustenta el puntaje otorgado");
    }
}
