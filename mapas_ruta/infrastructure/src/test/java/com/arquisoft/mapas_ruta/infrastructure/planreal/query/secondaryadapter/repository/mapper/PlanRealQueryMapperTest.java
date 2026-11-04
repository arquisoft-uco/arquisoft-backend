package com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository.mapper;

import com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository.PlanRealJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlanRealQueryMapperTest {

    @Test
    void debeMapearCamposAlReadModel() {
        // Arrange
        var entity = PlanRealJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .tareaId(UtilUUID.generarNuevoUUID())
                .tiempoRealDias(9)
                .descripcionReal("Revisar")
                .build();

        // Act
        var readModel = PlanRealQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(entity.getId());
        assertThat(readModel.tarea()).isEqualTo(entity.getTareaId());
        assertThat(readModel.tiempoRealDias()).isEqualTo(9);
        assertThat(readModel.descripcionReal()).isEqualTo("Revisar");
    }
}
