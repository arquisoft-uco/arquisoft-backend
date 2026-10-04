package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.entity.ItemCuantitativoJuradoEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ItemCuantitativoJuradoJpaMapperTest {

    @Test
    void debeMapearEntityAJpaEntityYRegresar() {
        // Arrange
        var entity = new ItemCuantitativoJuradoEntity(
                UUID.randomUUID(), "Calidad", "Descripción", UUID.randomUUID(), 100);

        // Act
        var jpaEntity = ItemCuantitativoJuradoJpaMapper.toJpaEntity(entity);
        var reconstruida = ItemCuantitativoJuradoJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(reconstruida).isEqualTo(entity);
    }
}
