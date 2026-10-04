package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.query.secondaryadapter.repository.ItemCuantitativoJuradoJpaQueryEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ItemCuantitativoJuradoQueryMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoConvierteJpaQueryEntityAReadModel() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        ItemCuantitativoJuradoJpaQueryEntity entity = ItemCuantitativoJuradoJpaQueryEntity.builder()
                .id(id)
                .nombre("Puntualidad")
                .descripcion("Evalúa la puntualidad en la sustentación")
                .categoriaId(categoriaId)
                .valor(50)
                .build();

        // Act
        ItemCuantitativoJuradoReadModel resultado = ItemCuantitativoJuradoQueryMapper.toReadModel(entity);

        // Assert
        assertThat(resultado.id()).isEqualTo(id);
        assertThat(resultado.nombre()).isEqualTo("Puntualidad");
        assertThat(resultado.descripcion()).isEqualTo("Evalúa la puntualidad en la sustentación");
        assertThat(resultado.categoriaId()).isEqualTo(categoriaId);
        assertThat(resultado.valor()).isEqualTo(50);
    }
}
