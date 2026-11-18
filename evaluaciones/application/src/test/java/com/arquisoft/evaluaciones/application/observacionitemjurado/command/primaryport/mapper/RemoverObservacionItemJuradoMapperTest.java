package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.RemoverObservacionItemJuradoCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverObservacionItemJuradoMapperTest {

    @Test
    void debeMapearId_cuandoCommandEsValido() {
        // Arrange
        var observacionItemJurado = UtilUUID.generarNuevoUUID();
        var command = RemoverObservacionItemJuradoCommand.crear(observacionItemJurado);

        // Act
        var remocion = RemoverObservacionItemJuradoMapper.toDomain(command);

        // Assert
        assertThat(remocion.getObservacionItemJurado()).isEqualTo(observacionItemJurado);
    }
}
