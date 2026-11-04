package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.CambiarEstadoItemTareaCommand;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarEstadoItemTareaMapperTest {

    @Test
    void debeMapearCommandADomain() {
        // Arrange
        var command = new CambiarEstadoItemTareaCommand(UtilUUID.generarNuevoUUID(), "FINALIZADA");

        // Act
        var cambio = CambiarEstadoItemTareaMapper.toDomain(command);

        // Assert
        assertThat(cambio.getTarea()).isEqualTo(command.tarea());
        assertThat(cambio.getEstadoItem()).isEqualTo(EstadoItem.FINALIZADA);
    }
}
