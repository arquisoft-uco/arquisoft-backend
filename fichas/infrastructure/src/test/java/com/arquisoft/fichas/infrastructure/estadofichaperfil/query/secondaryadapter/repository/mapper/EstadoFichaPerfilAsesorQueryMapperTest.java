package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository.EstadoFichaPerfilAsesorJpaQueryEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoFichaPerfilAsesorQueryMapperTest {

    @Test
    void debeMapearEntityAReadModel_conTodosLosCampos() {
        // Arrange
        var id = UUID.randomUUID();
        var fichaPerfilId = UUID.randomUUID();
        var asesorFichaId = UUID.randomUUID();
        var fecha = Instant.now();
        var entity = EstadoFichaPerfilAsesorJpaQueryEntity.builder()
                .id(id)
                .fichaPerfilId(fichaPerfilId)
                .tituloProyecto("Sistema de gestion")
                .asesorFichaId(asesorFichaId)
                .estadoId("APROBADA")
                .estadoNombre("Aprobada")
                .fechaActualizacion(fecha)
                .build();

        // Act
        var readModel = EstadoFichaPerfilAsesorQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.fichaPerfil()).isEqualTo(fichaPerfilId);
        assertThat(readModel.tituloProyecto()).isEqualTo("Sistema de gestion");
        assertThat(readModel.estadoId()).isEqualTo("APROBADA");
        assertThat(readModel.estadoNombre()).isEqualTo("Aprobada");
        assertThat(readModel.fechaActualizacion()).isEqualTo(fecha);
    }
}
