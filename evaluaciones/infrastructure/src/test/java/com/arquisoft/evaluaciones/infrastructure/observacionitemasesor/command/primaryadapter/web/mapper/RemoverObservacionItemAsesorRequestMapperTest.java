package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverObservacionItemAsesorRequestMapperTest {

    @Test
    void debeConstruirElCommand_cuandoRecibeIds() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = RemoverObservacionItemAsesorRequestMapper.toCommand(observacion, asesor);

        // Assert
        assertThat(command.observacion()).isEqualTo(observacion);
        assertThat(command.asesor()).isEqualTo(asesor);
    }
}
