package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model.RemoverItemCuantitativoJuradoCommand;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverItemCuantitativoJuradoMapperTest {

    @Test
    void debeMapearItem_cuandoCommandEsValido() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();
        var command = RemoverItemCuantitativoJuradoCommand.crear(itemCuantitativoJurado);

        // Act
        var remocion = RemoverItemCuantitativoJuradoMapper.toDomain(command);

        // Assert
        assertThat(remocion.getItemCuantitativoJurado()).isEqualTo(itemCuantitativoJurado);
    }
}
