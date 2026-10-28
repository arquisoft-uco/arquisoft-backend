package com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverInformacionTareaRequestMapperTest {

    @Test
    void debeConstruirCommand_conElTareaId() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();

        // Act
        var command = RemoverInformacionTareaRequestMapper.toCommand(tarea);

        // Assert
        assertThat(command.tarea()).isEqualTo(tarea);
    }
}
