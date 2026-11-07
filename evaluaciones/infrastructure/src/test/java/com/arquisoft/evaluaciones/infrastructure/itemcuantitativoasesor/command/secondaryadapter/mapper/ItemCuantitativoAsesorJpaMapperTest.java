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
}
