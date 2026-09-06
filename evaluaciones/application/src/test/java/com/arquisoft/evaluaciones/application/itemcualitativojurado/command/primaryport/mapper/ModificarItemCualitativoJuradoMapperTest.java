package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model.ModificarItemCualitativoJuradoCommand;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.ModificacionItemCualitativoJuradoDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarItemCualitativoJuradoMapperTest {

    @Test
    void debeConstruirObjetoDeAccion_cuandoCommandEsValido() {
        // Arrange
        UUID itemCualitativoJurado = UUID.randomUUID();
        var command = ModificarItemCualitativoJuradoCommand.crear(
                itemCualitativoJurado, "Nueva descripción");

        // Act
        ModificacionItemCualitativoJuradoDomain modificacion =
                ModificarItemCualitativoJuradoMapper.toDomain(command);

        // Assert
        assertThat(modificacion.getItemCualitativoJurado()).isEqualTo(itemCualitativoJurado);
        assertThat(modificacion.getDescripcion()).isEqualTo(command.descripcion());
    }
}
