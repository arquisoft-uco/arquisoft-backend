package com.arquisoft.entregables.infrastructure.estadoentregable.query.secondaryadapter.repository.mapper;

import com.arquisoft.entregables.infrastructure.estadoentregable.query.secondaryadapter.repository.EstadoEntregableJpaQueryEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoEntregableQueryMapperTest {

    @Test
    void debeMapearLasTresColumnas_cuandoRecibeLaEntidad() {
        // Arrange
        var entity = EstadoEntregableJpaQueryEntity.builder()
                .id("CERRADO")
                .nombre("Cerrado")
                .descripcion("Cerrado ok")
                .build();

        // Act
        var readModel = EstadoEntregableQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo("CERRADO");
        assertThat(readModel.nombre()).isEqualTo("Cerrado");
        assertThat(readModel.descripcion()).isEqualTo("Cerrado ok");
    }
}
