package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OmitirArtefactoRequestMapperTest {

    @Test
    void debeConstruirElCommand_cuandoArtefactoYAsesorSonValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = OmitirArtefactoRequestMapper.toCommand(artefacto, asesor);

        // Assert
        assertThat(command.artefacto()).isEqualTo(artefacto);
        assertThat(command.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoElAsesorEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> OmitirArtefactoRequestMapper.toCommand(UtilUUID.generarNuevoUUID(), null))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
