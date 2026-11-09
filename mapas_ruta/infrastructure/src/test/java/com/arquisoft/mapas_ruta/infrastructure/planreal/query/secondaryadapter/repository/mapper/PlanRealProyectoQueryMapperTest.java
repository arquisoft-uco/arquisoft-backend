package com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository.mapper;

import com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository.PlanRealProyectoJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlanRealProyectoQueryMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoEntidadCompleta() {
        // Arrange
        var entity = PlanRealProyectoJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .tareaId(UtilUUID.generarNuevoUUID())
                .descripcionTarea("Redactar capitulo")
                .mapaRutaId(UtilUUID.generarNuevoUUID())
                .proyectoGradoId(UtilUUID.generarNuevoUUID())
                .tiempoRealDias(9)
                .descripcionReal("Revisar")
                .build();

        // Act
        var readModel = PlanRealProyectoQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(entity.getId());
        assertThat(readModel.tarea()).isEqualTo(entity.getTareaId());
        assertThat(readModel.descripcionTarea()).isEqualTo("Redactar capitulo");
        assertThat(readModel.mapaRuta()).isEqualTo(entity.getMapaRutaId());
        assertThat(readModel.tiempoRealDias()).isEqualTo(9);
        assertThat(readModel.descripcionReal()).isEqualTo("Revisar");
    }
}
