package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository.EstadoFichaPerfilJpaQueryEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoFichaPerfilQueryMapperTest {

    @Test
    void debeMapearEntityAReadModel() {
        // Arrange
        var fechaActualizacion = Instant.now();
        var entity = EstadoFichaPerfilJpaQueryEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(UUID.randomUUID())
                .estudianteId(UUID.randomUUID())
                .estadoId("EN_CONSTRUCCION")
                .estadoNombre("En Construccion")
                .fechaActualizacion(fechaActualizacion)
                .build();

        // Act
        EstadoFichaPerfilReadModel readModel = EstadoFichaPerfilQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo("EN_CONSTRUCCION");
        assertThat(readModel.nombre()).isEqualTo("En Construccion");
        assertThat(readModel.fechaActualizacion()).isEqualTo(fechaActualizacion);
    }
}
