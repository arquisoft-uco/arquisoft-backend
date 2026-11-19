package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.primaryadapter.web.dto.AsignarAsesorEvaluacionRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsignarAsesorEvaluacionRequestMapperTest {

    @Test
    void debeMapearAlCommand_cuandoElDtoEsValido() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = AsignarAsesorEvaluacionRequestMapper.toCommand(
                new AsignarAsesorEvaluacionRequestDTO(asesor.toString()), evaluacion);

        // Assert
        assertThat(command.evaluacion()).isEqualTo(evaluacion);
        assertThat(command.asesor()).isEqualTo(asesor);
    }
}
