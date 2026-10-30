package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.secondaryadapter.repository.ItemCualitativoAsesorJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemCualitativoAsesorQueryMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoConvierteAReadModel() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entity = ItemCualitativoAsesorJpaQueryEntity.builder()
                .id(id)
                .nombre("Actitud")
                .descripcion("Evalúa la actitud")
                .build();

        // Act
        var resultado = ItemCualitativoAsesorQueryMapper.toReadModel(entity);

        // Assert
        assertThat(resultado.id()).isEqualTo(id);
        assertThat(resultado.nombre()).isEqualTo("Actitud");
        assertThat(resultado.descripcion()).isEqualTo("Evalúa la actitud");
    }
}
