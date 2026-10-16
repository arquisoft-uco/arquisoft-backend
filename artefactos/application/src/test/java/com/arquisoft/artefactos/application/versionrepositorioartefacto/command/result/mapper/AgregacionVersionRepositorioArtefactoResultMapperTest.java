package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.result.mapper;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AgregacionVersionRepositorioArtefactoResultMapperTest {

    private static VersionRepositorioArtefactoDomain version() {
        return VersionRepositorioArtefactoDomain.crear(UtilUUID.generarNuevoUUID(), "Plantilla", 1,
                UtilUUID.generarNuevoUUID(), "Repositorio", new DatosArchivo("plantilla.pdf", "application/pdf", 10L));
    }

    @Test
    void debeMapearAgregada_conElIdDeLaVersion() {
        // Arrange
        var version = version();

        // Act
        var resultado = AgregacionVersionRepositorioArtefactoResultMapper.toResultAgregada(version);

        // Assert
        assertThat(resultado.versionRepositorioArtefacto()).isEqualTo(version.getId());
    }

    @Test
    void debeMapearDuplicada_conElIdDeLaVersion() {
        // Arrange
        var version = version();

        // Act
        var resultado = AgregacionVersionRepositorioArtefactoResultMapper.toResultDuplicada(version);

        // Assert
        assertThat(resultado.versionRepositorioArtefacto()).isEqualTo(version.getId());
    }
}
