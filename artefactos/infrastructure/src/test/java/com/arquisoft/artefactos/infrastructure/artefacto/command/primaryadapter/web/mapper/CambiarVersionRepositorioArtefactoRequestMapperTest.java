package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.dto.CambiarVersionRepositorioArtefactoRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarVersionRepositorioArtefactoRequestMapperTest {

    @Test
    void debeMapearConActorDeLaIdentidad() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var dto = new CambiarVersionRepositorioArtefactoRequestDTO(version.toString());

        // Act
        var command = CambiarVersionRepositorioArtefactoRequestMapper.toCommand(artefacto, asesor, dto);

        // Assert
        assertThat(command.artefacto()).isEqualTo(artefacto);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.versionRepositorioArtefacto()).isEqualTo(version);
    }
}
