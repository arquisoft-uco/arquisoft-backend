package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web.dto.CambiarCriterioEvaluacionCualitativaJuradoRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarCriterioEvaluacionCualitativaJuradoRequestMapperTest {

    @Test
    void debeConstruirCommand_cuandoDtoValido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        var dto = new CambiarCriterioEvaluacionCualitativaJuradoRequestDTO(criterio.toString());

        // Act
        var command = CambiarCriterioEvaluacionCualitativaJuradoRequestMapper.toCommand(dto, id, jurado);

        // Assert
        assertThat(command.evaluacionCualitativaJurado()).isEqualTo(id);
        assertThat(command.criterio()).isEqualTo(criterio);
        assertThat(command.jurado()).isEqualTo(jurado);
    }
}
