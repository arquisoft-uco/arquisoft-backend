package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.dto.CambiarPuntajeEvaluacionCuantitativaJuradoRequestDTO;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarPuntajeEvaluacionCuantitativaJuradoRequestMapperTest {

    @Test
    void debeArmarCommand_conIdDelPathPuntajeDelBodyYSubjectDelJwt() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();
        var dto = new CambiarPuntajeEvaluacionCuantitativaJuradoRequestDTO(280);

        // Act
        var command = CambiarPuntajeEvaluacionCuantitativaJuradoRequestMapper.toCommand(
                dto, id, jurado.toString());

        // Assert
        assertThat(command.evaluacionCuantitativaJurado()).isEqualTo(id);
        assertThat(command.nuevoPuntaje()).isEqualTo(280);
        assertThat(command.jurado()).isEqualTo(jurado);
    }
}
