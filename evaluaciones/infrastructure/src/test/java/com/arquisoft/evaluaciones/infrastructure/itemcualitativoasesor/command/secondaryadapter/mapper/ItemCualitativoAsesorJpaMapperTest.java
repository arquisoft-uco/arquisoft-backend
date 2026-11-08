package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.entity.ItemCualitativoAsesorEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemCualitativoAsesorJpaMapperTest {

    @Test
    void debeMapearEntityAJpaEntity_cuandoRecibeEntity() {
        // Arrange
        var entity = new ItemCualitativoAsesorEntity(UtilUUID.generarNuevoUUID(), "Claridad", "Descripción");

        // Act
        var jpaEntity = ItemCualitativoAsesorJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpaEntity.getId()).isEqualTo(entity.id());
        assertThat(jpaEntity.getNombre()).isEqualTo("Claridad");
        assertThat(jpaEntity.getDescripcion()).isEqualTo("Descripción");
    }
}
