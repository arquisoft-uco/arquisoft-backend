package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web.dto.CambiarPuntajeEvaluacionCuantitativaAsesorRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarPuntajeEvaluacionCuantitativaAsesorRequestMapperTest {

    @Test
    void debeMapearACommand_cuandoDtoValido() {
        // Arrange
        var evaluacionCuantitativaAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var dto = new CambiarPuntajeEvaluacionCuantitativaAsesorRequestDTO(450);

        // Act
        var command = CambiarPuntajeEvaluacionCuantitativaAsesorRequestMapper.toCommand(
                dto, evaluacionCuantitativaAsesor, asesor);

        // Assert
        assertThat(command.evaluacionCuantitativaAsesor()).isEqualTo(evaluacionCuantitativaAsesor);
        assertThat(command.nuevoPuntaje()).isEqualTo(450);
        assertThat(command.asesor()).isEqualTo(asesor);
    }
}
