package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository.mapper;

import com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository.PlanEstimadoJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlanEstimadoQueryMapperTest {

    @Test
    void debeMapearCamposAlReadModel() {
        // Arrange
        var entity = PlanEstimadoJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .tareaId(UtilUUID.generarNuevoUUID())
                .tiempoEstimadoDias(9)
                .descripcionEstimado("Revisar")
                .build();

        // Act
        var readModel = PlanEstimadoQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(entity.getId());
        assertThat(readModel.tarea()).isEqualTo(entity.getTareaId());
        assertThat(readModel.tiempoEstimadoDias()).isEqualTo(9);
        assertThat(readModel.descripcionEstimado()).isEqualTo("Revisar");
    }
}
