package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository.EstudianteArtefactoJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteArtefactoQueryMapperTest {

    @Test
    void debeCopiarLosSieteCampos_cuandoMapea() {
        // Arrange
        var entity = EstudianteArtefactoJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .artefactoId(UtilUUID.generarNuevoUUID())
                .estudianteId(UtilUUID.generarNuevoUUID())
                .identificador("E-1")
                .nombre("Ana")
                .email("ana@uco.edu.co")
                .vigente(true)
                .build();

        // Act
        var readModel = EstudianteArtefactoQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(entity.getId());
        assertThat(readModel.artefactoId()).isEqualTo(entity.getArtefactoId());
        assertThat(readModel.estudianteId()).isEqualTo(entity.getEstudianteId());
        assertThat(readModel.identificador()).isEqualTo("E-1");
        assertThat(readModel.nombre()).isEqualTo("Ana");
        assertThat(readModel.email()).isEqualTo("ana@uco.edu.co");
        assertThat(readModel.vigente()).isTrue();
    }
}
