package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.ParticipacionArtefactoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParticipacionArtefactoResponseMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoReadModelCompleto() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var readModel = new ParticipacionArtefactoReadModel(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "Proyecto Alfa", UtilUUID.generarNuevoUUID(), "Informe", 3, "Repo",
                asesor, "Ana Asesora", true);

        // Act
        var dto = ParticipacionArtefactoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.proyectoGrado()).isEqualTo(readModel.proyectoGrado());
        assertThat(dto.nombreProyecto()).isEqualTo("Proyecto Alfa");
        assertThat(dto.versionRepositorioArtefacto()).isEqualTo(readModel.versionRepositorioArtefacto());
        assertThat(dto.nombreDocumento()).isEqualTo("Informe");
        assertThat(dto.versionDocumento()).isEqualTo(3);
        assertThat(dto.nombreRepositorio()).isEqualTo("Repo");
        assertThat(dto.asesor()).isEqualTo(asesor);
        assertThat(dto.nombreAsesor()).isEqualTo("Ana Asesora");
        assertThat(dto.omitido()).isTrue();
    }
}
