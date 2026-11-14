package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model.ModificarItemCualitativoAsesorCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarItemCualitativoAsesorMapperTest {

    @Test
    void debeConstruirObjetoDeAccion_cuandoCommandEsValido() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var command = ModificarItemCualitativoAsesorCommand.crear(item, "Descripción ampliada");

        // Act
        var modificacion = ModificarItemCualitativoAsesorMapper.toDomain(command);

        // Assert
        assertThat(modificacion.getItemCualitativoAsesor()).isEqualTo(item);
        assertThat(modificacion.getDescripcion()).isEqualTo("Descripción ampliada");
    }
}
