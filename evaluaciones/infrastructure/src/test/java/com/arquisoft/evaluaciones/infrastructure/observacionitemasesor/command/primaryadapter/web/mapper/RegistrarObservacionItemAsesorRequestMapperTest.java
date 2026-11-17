package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web.dto.RegistrarObservacionItemAsesorRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarObservacionItemAsesorRequestMapperTest {

    @Test
    void debeConstruirCommand() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var dto = new RegistrarObservacionItemAsesorRequestDTO("  Sustento  ");

        // Act
        var command = RegistrarObservacionItemAsesorRequestMapper.toCommand(dto, evaluacion, asesor);

        // Assert
        assertThat(command.evaluacionCuantitativaAsesor()).isEqualTo(evaluacion);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.descripcion()).isEqualTo("Sustento");
    }
}
