package com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarObservacionRequestMapperTest {

    @Test
    void debeConstruirElCommand_cuandoPathEIdentidadSonValidos() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = EliminarObservacionRequestMapper.toCommand(observacion, asesor);

        // Assert
        assertThat(command.observacion()).isEqualTo(observacion);
        assertThat(command.asesor()).isEqualTo(asesor);
    }
}
