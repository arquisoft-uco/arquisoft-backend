package com.arquisoft.proyectos.infrastructure.estadoproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoProyectoGradoResponseMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoReadModelCompleto() {
        // Arrange
        var readModel = new EstadoProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "EN_PROCESO", "En proceso",
                Instant.parse("2026-10-05T10:00:00Z"), null);

        // Act
        var dto = EstadoProyectoGradoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.estadoProyecto()).isEqualTo("EN_PROCESO");
        assertThat(dto.estadoProyectoNombre()).isEqualTo("En proceso");
        assertThat(dto.fechaActualizacion()).isEqualTo(Instant.parse("2026-10-05T10:00:00Z"));
        assertThat(dto.motivo()).isNull();
    }

    @Test
    void debeMapearMotivo_cuandoElReadModelLoTrae() {
        // Arrange
        var readModel = new EstadoProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), "CANCELADO", "Cancelado",
                Instant.parse("2026-10-05T10:00:00Z"), "Cancelado por inactividad");

        // Act
        var dto = EstadoProyectoGradoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.motivo()).isEqualTo("Cancelado por inactividad");
    }
}
