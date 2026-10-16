package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorProyectoGradoResponseMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoReadModelCompleto() {
        // Arrange
        var readModel = new AsesorProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "1001", "Ana", "ana@soyuco.edu.co", "FINALIZADO", "Finalizado",
                Instant.parse("2026-10-05T10:00:00Z"), false, true);

        // Act
        var dto = AsesorProyectoGradoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.proyectoGradoId()).isEqualTo(readModel.proyectoGradoId());
        assertThat(dto.asesorId()).isEqualTo(readModel.asesorId());
        assertThat(dto.identificador()).isEqualTo("1001");
        assertThat(dto.nombre()).isEqualTo("Ana");
        assertThat(dto.email()).isEqualTo("ana@soyuco.edu.co");
        assertThat(dto.estadoAsesor()).isEqualTo("FINALIZADO");
        assertThat(dto.estadoAsesorNombre()).isEqualTo("Finalizado");
        assertThat(dto.fechaAsignado()).isEqualTo(Instant.parse("2026-10-05T10:00:00Z"));
        assertThat(dto.vigente()).isFalse();
        assertThat(dto.actual()).isTrue();
    }
}
