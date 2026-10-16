package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web.dto.ModificarObservacionItemJuradoRequestDTO;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarObservacionItemJuradoRequestMapperTest {

    @Test
    void debeConstruirCommand_cuandoDatosValidos() {
        // Arrange
        var observacionId = UUID.randomUUID();
        var dto = new ModificarObservacionItemJuradoRequestDTO("Sustenta el puntaje otorgado");

        // Act
        var command = ModificarObservacionItemJuradoRequestMapper.toCommand(dto, observacionId);

        // Assert
        assertThat(command.observacionItemJurado()).isEqualTo(observacionId);
        assertThat(command.descripcion()).isEqualTo("Sustenta el puntaje otorgado");
    }
}
