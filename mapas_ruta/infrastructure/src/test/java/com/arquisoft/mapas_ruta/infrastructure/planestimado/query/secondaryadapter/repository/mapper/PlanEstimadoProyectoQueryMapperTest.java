package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository.mapper;

import com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository.PlanEstimadoProyectoJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlanEstimadoProyectoQueryMapperTest {

    @Test
    void debeMapearEntityAReadModel() {
        // Arrange
        var entity = PlanEstimadoProyectoJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .tareaId(UtilUUID.generarNuevoUUID())
                .descripcionTarea("Redactar capitulo")
                .mapaRutaId(UtilUUID.generarNuevoUUID())
                .proyectoGradoId(UtilUUID.generarNuevoUUID())
                .tiempoEstimadoDias(9)
                .descripcionEstimado("Revisar")
                .build();

        // Act
        var readModel = PlanEstimadoProyectoQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(entity.getId());
        assertThat(readModel.tarea()).isEqualTo(entity.getTareaId());
        assertThat(readModel.descripcionTarea()).isEqualTo("Redactar capitulo");
        assertThat(readModel.mapaRuta()).isEqualTo(entity.getMapaRutaId());
        assertThat(readModel.tiempoEstimadoDias()).isEqualTo(9);
        assertThat(readModel.descripcionEstimado()).isEqualTo("Revisar");
    }
}
