package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VersionRepositorioArtefactoMapperTest {

    @Test
    void debeConservarLosOchoCampos_cuandoMapeaAEntity() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        var version = VersionRepositorioArtefactoDomain.crear(id, "Plantilla", 4, repositorio, "Repositorio",
                new DatosArchivo("plantilla.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        5000L));

        // Act
        var entity = VersionRepositorioArtefactoMapper.toEntity(version);

        // Assert
        assertThat(entity.id()).isEqualTo(id);
        assertThat(entity.nombreDocumento()).isEqualTo("Plantilla");
        assertThat(entity.versionDocumento()).isEqualTo(4);
        assertThat(entity.repositorioArtefacto()).isEqualTo(repositorio);
        assertThat(entity.nombreRepositorio()).isEqualTo("Repositorio");
        assertThat(entity.nombreArchivo()).isEqualTo("plantilla.docx");
        assertThat(entity.tipoContenido())
                .isEqualTo("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        assertThat(entity.tamanoBytes()).isEqualTo(5000L);
    }
}
