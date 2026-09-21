package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.ModificarObservacionItemJuradoCommand;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarObservacionItemJuradoMapperTest {

    @Test
    void debeConstruirModificacion_cuandoCommandEsValido() {
        // Arrange
        var observacionItemJurado = UUID.randomUUID();
        var command = ModificarObservacionItemJuradoCommand.crear(
                observacionItemJurado, "Sustenta el puntaje otorgado");

        // Act
        var modificacion = ModificarObservacionItemJuradoMapper.toDomain(command);

        // Assert
        assertThat(modificacion.getObservacionItemJurado()).isEqualTo(observacionItemJurado);
        assertThat(modificacion.getDescripcion()).isEqualTo("Sustenta el puntaje otorgado");
    }
}
