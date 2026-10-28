package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.ModificarInformacionTareaCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarInformacionTareaMapperTest {

    @Test
    void debeMapearADominio_cuandoCommandValido() {
        // Arrange
        var command = new ModificarInformacionTareaCommand(
                UtilUUID.generarNuevoUUID(), "Redactar capitulo", UtilUUID.generarNuevoUUID());

        // Act
        var modificacion = ModificarInformacionTareaMapper.toDomain(command);

        // Assert
        assertThat(modificacion.getTarea()).isEqualTo(command.tarea());
        assertThat(modificacion.getDescripcion()).isEqualTo(command.descripcion());
        assertThat(modificacion.getAsesor()).isEqualTo(command.asesor());
    }
}
