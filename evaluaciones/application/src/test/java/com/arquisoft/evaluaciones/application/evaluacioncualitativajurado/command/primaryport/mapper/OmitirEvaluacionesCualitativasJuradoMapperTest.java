package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.OmitirEvaluacionesCualitativasJuradoCommand;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OmitirEvaluacionesCualitativasJuradoMapperTest {

    @Test
    void debeConstruirObjetoDeAccion_cuandoMapeaElCommand() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var primera = UUID.randomUUID();
        var segunda = UUID.randomUUID();
        var command = new OmitirEvaluacionesCualitativasJuradoCommand(evaluacionJurado, List.of(primera, segunda));

        // Act
        var omision = OmitirEvaluacionesCualitativasJuradoMapper.toDomain(command);

        // Assert
        assertThat(omision.getEvaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(omision.getEvaluaciones()).containsExactlyInAnyOrder(primera, segunda);
    }
}
