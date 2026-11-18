package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverObservacionItemJuradoRequestMapperTest {

    @Test
    void debeConstruirCommand_cuandoIdEsValido() {
        // Arrange
        var observacionId = UtilUUID.generarNuevoUUID();

        // Act
        var command = RemoverObservacionItemJuradoRequestMapper.toCommand(observacionId);

        // Assert
        assertThat(command.observacionItemJurado()).isEqualTo(observacionId);
    }
}
