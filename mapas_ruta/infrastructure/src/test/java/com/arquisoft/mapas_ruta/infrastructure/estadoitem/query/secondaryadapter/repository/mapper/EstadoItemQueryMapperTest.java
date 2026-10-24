package com.arquisoft.mapas_ruta.infrastructure.estadoitem.query.secondaryadapter.repository.mapper;

import com.arquisoft.mapas_ruta.infrastructure.estadoitem.query.secondaryadapter.repository.EstadoItemJpaQueryEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoItemQueryMapperTest {

    @Test
    void debeMapearLasTresColumnas_cuandoRecibeLaEntidad() {
        // Arrange
        var entity = EstadoItemJpaQueryEntity.builder()
                .id("FINALIZADA")
                .nombre("Finalizada")
                .descripcion("Terminada")
                .build();

        // Act
        var readModel = EstadoItemQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo("FINALIZADA");
        assertThat(readModel.nombre()).isEqualTo("Finalizada");
        assertThat(readModel.descripcion()).isEqualTo("Terminada");
    }
}
