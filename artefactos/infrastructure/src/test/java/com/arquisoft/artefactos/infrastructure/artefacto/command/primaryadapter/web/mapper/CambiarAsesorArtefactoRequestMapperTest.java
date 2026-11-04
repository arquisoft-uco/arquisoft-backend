package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.dto.CambiarAsesorArtefactoRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarAsesorArtefactoRequestMapperTest {

    @Test
    void debeConstruirElCommand_cuandoElDtoEsValido() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var dto = new CambiarAsesorArtefactoRequestDTO(asesor.toString());

        // Act
        var command = CambiarAsesorArtefactoRequestMapper.toCommand(artefacto, dto);

        // Assert
        assertThat(command.artefacto()).isEqualTo(artefacto);
        assertThat(command.asesor()).isEqualTo(asesor);
    }
}
