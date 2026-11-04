package com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.CambiarVersionRepositorioArtefactoCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarVersionRepositorioArtefactoMapperTest {

    @Test
    void debeMapearADomain() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var command = new CambiarVersionRepositorioArtefactoCommand(artefacto, asesor, version);

        // Act
        var cambio = CambiarVersionRepositorioArtefactoMapper.toDomain(command);

        // Assert
        assertThat(cambio.getArtefacto()).isEqualTo(artefacto);
        assertThat(cambio.getAsesor()).isEqualTo(asesor);
        assertThat(cambio.getVersionRepositorioArtefacto()).isEqualTo(version);
    }
}
