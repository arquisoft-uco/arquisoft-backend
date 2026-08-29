package com.arquisoft.fichas.infrastructure.revisionitem.command.primaryadapter.web.mapper;

import com.arquisoft.fichas.infrastructure.revisionitem.command.primaryadapter.web.dto.ModificarRevisionItemRequestDTO;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarRevisionItemRequestMapperTest {

    @Test
    void debeConstruirElCommand_cuandoDatosValidos() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID asesorFicha = UUID.randomUUID();
        var dto = new ModificarRevisionItemRequestDTO("VISUALIZADA");

        // Act
        var command = ModificarRevisionItemRequestMapper.toCommand(dto, item, asesorFicha);

        // Assert
        assertThat(command.item()).isEqualTo(item);
        assertThat(command.estadoRevision()).isEqualTo("VISUALIZADA");
        assertThat(command.asesorFicha()).isEqualTo(asesorFicha);
    }
}
