package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.RegistrarObservacionItemJuradoCommand;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarObservacionItemJuradoMapperTest {

    @Test
    void debeConstruirRegistro_cuandoCommandEsValido() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var jurado = UUID.randomUUID();
        var command = RegistrarObservacionItemJuradoCommand.crear(
                evaluacionCuantitativaJurado, "Sustenta el puntaje otorgado", jurado.toString());

        // Act
        var registro = RegistrarObservacionItemJuradoMapper.toDomain(command);

        // Assert
        assertThat(registro.getJurado()).isEqualTo(jurado);
        assertThat(registro.getObservacion().getId()).isNotNull();
        assertThat(registro.getObservacion().getEvaluacionCuantitativaJurado())
                .isEqualTo(evaluacionCuantitativaJurado);
        assertThat(registro.getObservacion().getDescripcion()).isEqualTo("Sustenta el puntaje otorgado");
    }
}
