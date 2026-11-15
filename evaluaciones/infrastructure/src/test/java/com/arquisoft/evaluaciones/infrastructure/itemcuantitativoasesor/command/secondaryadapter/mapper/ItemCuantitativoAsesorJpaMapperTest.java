package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.entity.ItemCuantitativoAsesorEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemCuantitativoAsesorJpaMapperTest {

    @Test
    void debeMapearEntityAJpaEntity_cuandoEntityEsValida() {
        // Arrange
        var entity = new ItemCuantitativoAsesorEntity(
                UtilUUID.generarNuevoUUID(), "Avance", "Porcentaje", UtilUUID.generarNuevoUUID(), 80);

        // Act
        var jpa = ItemCuantitativoAsesorJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpa.getId()).isEqualTo(entity.id());
        assertThat(jpa.getNombre()).isEqualTo("Avance");
        assertThat(jpa.getDescripcion()).isEqualTo("Porcentaje");
        assertThat(jpa.getCategoria()).isEqualTo(entity.categoriaId());
        assertThat(jpa.getValor()).isEqualTo(80);
    }

    @Test
    void debeMapearJpaEntityAEntity_cuandoJpaEntityEsValida() {
        // Arrange
        var jpa = com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.entity
                .ItemCuantitativoAsesorJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .nombre("Avance")
                .descripcion("Porcentaje")
                .categoria(UtilUUID.generarNuevoUUID())
                .valor(80)
                .build();

        // Act
        var entity = ItemCuantitativoAsesorJpaMapper.toEntity(jpa);

        // Assert
        assertThat(entity.id()).isEqualTo(jpa.getId());
        assertThat(entity.categoriaId()).isEqualTo(jpa.getCategoria());
        assertThat(entity.valor()).isEqualTo(80);
    }
}
