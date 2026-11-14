package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model.ModificarItemCuantitativoAsesorCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarItemCuantitativoAsesorMapperTest {

    @Test
    void debeConstruirObjetoDeAccion_cuandoCommandEsValido() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var command = ModificarItemCuantitativoAsesorCommand.crear(item, "Descripción ampliada");

        // Act
        var modificacion = ModificarItemCuantitativoAsesorMapper.toDomain(command);

        // Assert
        assertThat(modificacion.getItemCuantitativoAsesor()).isEqualTo(item);
        assertThat(modificacion.getDescripcion()).isEqualTo("Descripción ampliada");
    }
}
