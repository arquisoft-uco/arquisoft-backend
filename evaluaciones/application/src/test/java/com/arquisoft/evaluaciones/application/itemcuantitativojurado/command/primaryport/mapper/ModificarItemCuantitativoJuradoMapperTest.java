package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model.ModificarItemCuantitativoJuradoCommand;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ModificacionItemCuantitativoJuradoDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarItemCuantitativoJuradoMapperTest {

    @Test
    void debeConstruirObjetoDeAccion_cuandoCommandEsValido() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();
        var command = ModificarItemCuantitativoJuradoCommand.crear(
                itemCuantitativoJurado, "Nueva descripción");

        // Act
        ModificacionItemCuantitativoJuradoDomain modificacion =
                ModificarItemCuantitativoJuradoMapper.toDomain(command);

        // Assert
        assertThat(modificacion.getItemCuantitativoJurado()).isEqualTo(itemCuantitativoJurado);
        assertThat(modificacion.getDescripcion()).isEqualTo(command.descripcion());
    }
}
