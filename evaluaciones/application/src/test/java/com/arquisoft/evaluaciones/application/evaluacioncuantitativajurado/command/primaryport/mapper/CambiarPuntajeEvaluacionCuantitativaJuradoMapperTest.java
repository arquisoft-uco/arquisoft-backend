package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.CambiarPuntajeEvaluacionCuantitativaJuradoCommand;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarPuntajeEvaluacionCuantitativaJuradoMapperTest {

    @Test
    void debeMapearCommandAObjetoDeAccion() {
        // Arrange
        UUID evaluacionCuantitativaJurado = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        var command = CambiarPuntajeEvaluacionCuantitativaJuradoCommand.crear(
                evaluacionCuantitativaJurado, 275, jurado.toString());

        // Act
        var cambio = CambiarPuntajeEvaluacionCuantitativaJuradoMapper.toDomain(command);

        // Assert
        assertThat(cambio.getEvaluacionCuantitativaJurado()).isEqualTo(evaluacionCuantitativaJurado);
        assertThat(cambio.getJurado()).isEqualTo(jurado);
        assertThat(cambio.getNuevoPuntaje()).isEqualTo(275);
    }
}
