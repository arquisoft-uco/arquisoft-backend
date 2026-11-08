package com.arquisoft.mapas_ruta.infrastructure.planreal.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealProyectoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlanRealProyectoResponseMapperTest {

    @Test
    void debeMapearReadModelADto_cuandoEsValido() {
        // Arrange
        var readModel = new PlanRealProyectoReadModel(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "Redactar capitulo", UtilUUID.generarNuevoUUID(), 7, "Revisar");

        // Act
        var dto = PlanRealProyectoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.tarea()).isEqualTo(readModel.tarea());
        assertThat(dto.descripcionTarea()).isEqualTo("Redactar capitulo");
        assertThat(dto.mapaRuta()).isEqualTo(readModel.mapaRuta());
        assertThat(dto.tiempoRealDias()).isEqualTo(7);
        assertThat(dto.descripcionReal()).isEqualTo("Revisar");
    }
}
