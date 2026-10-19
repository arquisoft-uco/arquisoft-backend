package com.arquisoft.artefactos.infrastructure.estadorevisionasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.infrastructure.estadorevisionasesor.query.secondaryadapter.repository.EstadoRevisionAsesorJpaQueryEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoRevisionAsesorQueryMapperTest {

    @Test
    void debeMapearIdNombreYDescripcion_cuandoConvierteLaEntidad() {
        // Arrange
        var entity = EstadoRevisionAsesorJpaQueryEntity.builder()
                .id("PENDIENTE").nombre("Pendiente").descripcion("Nueva revisión").orden(1).build();

        // Act
        var resultado = EstadoRevisionAsesorQueryMapper.toReadModel(entity);

        // Assert
        assertThat(resultado.id()).isEqualTo("PENDIENTE");
        assertThat(resultado.nombre()).isEqualTo("Pendiente");
        assertThat(resultado.descripcion()).isEqualTo("Nueva revisión");
    }
}
