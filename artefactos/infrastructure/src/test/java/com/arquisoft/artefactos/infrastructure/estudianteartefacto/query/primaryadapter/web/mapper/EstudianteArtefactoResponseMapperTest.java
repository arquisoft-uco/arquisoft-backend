package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.EstudianteArtefactoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteArtefactoResponseMapperTest {

    @Test
    void debeCopiarLosSieteCampos_cuandoMapea() {
        // Arrange
        var readModel = new EstudianteArtefactoReadModel(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "E-1", "Ana", "ana@uco.edu.co", false);

        // Act
        var dto = EstudianteArtefactoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.artefactoId()).isEqualTo(readModel.artefactoId());
        assertThat(dto.estudianteId()).isEqualTo(readModel.estudianteId());
        assertThat(dto.identificador()).isEqualTo("E-1");
        assertThat(dto.nombre()).isEqualTo("Ana");
        assertThat(dto.email()).isEqualTo("ana@uco.edu.co");
        assertThat(dto.vigente()).isFalse();
    }
}
