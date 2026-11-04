package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.RemoverInformacionTareaCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverInformacionTareaMapperTest {

    @Test
    void debeMapearTareaAlObjetoDeAccion() {
        // Arrange
        var command = new RemoverInformacionTareaCommand(UtilUUID.generarNuevoUUID());

        // Act
        var remocion = RemoverInformacionTareaMapper.toDomain(command);

        // Assert
        assertThat(remocion.getTarea()).isEqualTo(command.tarea());
    }
}
