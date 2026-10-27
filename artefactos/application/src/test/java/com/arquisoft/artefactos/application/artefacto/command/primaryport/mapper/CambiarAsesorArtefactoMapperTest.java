package com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.CambiarAsesorArtefactoCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarAsesorArtefactoMapperTest {

    @Test
    void debeMapearElCommandAlObjetoDeAccion_cuandoLosDatosSonValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = new CambiarAsesorArtefactoCommand(artefacto, asesor);

        // Act
        var cambio = CambiarAsesorArtefactoMapper.toDomain(command);

        // Assert
        assertThat(cambio.getArtefacto()).isEqualTo(artefacto);
        assertThat(cambio.getAsesor()).isEqualTo(asesor);
    }
}
