package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web.dto.ModificarObservacionItemAsesorRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarObservacionItemAsesorRequestMapperTest {

    @Test
    void debeConstruirCommand_conObservacionAsesorYDescripcion() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var dto = new ModificarObservacionItemAsesorRequestDTO("  Sustento  ");

        // Act
        var command = ModificarObservacionItemAsesorRequestMapper.toCommand(dto, observacion, asesor);

        // Assert
        assertThat(command.observacion()).isEqualTo(observacion);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.descripcion()).isEqualTo("Sustento");
    }
}
