package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoProyectoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlanEstimadoProyectoResponseMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoConvierteElReadModel() {
        // Arrange
        var readModel = new PlanEstimadoProyectoReadModel(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "Redactar capitulo", UtilUUID.generarNuevoUUID(), 7, "Revisar");

        // Act
        var dto = PlanEstimadoProyectoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.tarea()).isEqualTo(readModel.tarea());
        assertThat(dto.descripcionTarea()).isEqualTo("Redactar capitulo");
        assertThat(dto.mapaRuta()).isEqualTo(readModel.mapaRuta());
        assertThat(dto.tiempoEstimadoDias()).isEqualTo(7);
        assertThat(dto.descripcionEstimado()).isEqualTo("Revisar");
    }
}
