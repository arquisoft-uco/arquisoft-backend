package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web.dto.CambiarCriterioEvaluacionCualitativaAsesorRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarCriterioEvaluacionCualitativaAsesorRequestMapperTest {

    @Test
    void debeConstruirCommand() {
        // Arrange
        var evaluacionCualitativa = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        var dto = new CambiarCriterioEvaluacionCualitativaAsesorRequestDTO(criterio.toString());

        // Act
        var command = CambiarCriterioEvaluacionCualitativaAsesorRequestMapper.toCommand(
                dto, evaluacionCualitativa, asesor);

        // Assert
        assertThat(command.evaluacionCualitativaAsesor()).isEqualTo(evaluacionCualitativa);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.criterio()).isEqualTo(criterio);
    }
}
