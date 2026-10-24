package com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.dto.CambiarEstadoItemTareaRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarEstadoItemTareaRequestMapperTest {

    @Test
    void debeMapearDtoACommand() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var dto = new CambiarEstadoItemTareaRequestDTO("EN_PROGRESO");

        // Act
        var command = CambiarEstadoItemTareaRequestMapper.toCommand(dto, tarea);

        // Assert
        assertThat(command.tarea()).isEqualTo(tarea);
        assertThat(command.estadoItem()).isEqualTo("EN_PROGRESO");
    }
}
