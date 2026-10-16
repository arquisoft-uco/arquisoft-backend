package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model.RemoverItemCualitativoJuradoCommand;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverItemCualitativoJuradoMapperTest {

    @Test
    void debeMapearItem_cuandoCommandEsValido() {
        // Arrange
        var itemCualitativoJurado = UUID.randomUUID();
        var command = RemoverItemCualitativoJuradoCommand.crear(itemCualitativoJurado);

        // Act
        var remocion = RemoverItemCualitativoJuradoMapper.toDomain(command);

        // Assert
        assertThat(remocion.getItemCualitativoJurado()).isEqualTo(itemCualitativoJurado);
    }
}
