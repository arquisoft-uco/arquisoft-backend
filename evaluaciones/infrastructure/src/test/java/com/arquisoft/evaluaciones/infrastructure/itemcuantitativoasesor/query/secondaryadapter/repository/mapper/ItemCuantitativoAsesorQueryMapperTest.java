package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.query.secondaryadapter.repository.ItemCuantitativoAsesorJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemCuantitativoAsesorQueryMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoConvierteAReadModel() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var categoriaId = UtilUUID.generarNuevoUUID();
        var entity = ItemCuantitativoAsesorJpaQueryEntity.builder()
                .id(id)
                .nombre("Puntualidad")
                .descripcion("Evalúa la puntualidad")
                .categoriaId(categoriaId)
                .valor(50)
                .build();

        // Act
        var resultado = ItemCuantitativoAsesorQueryMapper.toReadModel(entity);

        // Assert
        assertThat(resultado.id()).isEqualTo(id);
        assertThat(resultado.nombre()).isEqualTo("Puntualidad");
        assertThat(resultado.descripcion()).isEqualTo("Evalúa la puntualidad");
        assertThat(resultado.categoriaId()).isEqualTo(categoriaId);
        assertThat(resultado.valor()).isEqualTo(50);
    }
}
