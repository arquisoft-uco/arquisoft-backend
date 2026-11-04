package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.OmitirEvaluacionesCuantitativasJuradoCommand;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OmitirEvaluacionesCuantitativasJuradoMapperTest {

    @Test
    void debeConstruirObjetoDeAccion_cuandoMapeaElCommand() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var primera = UUID.randomUUID();
        var segunda = UUID.randomUUID();
        var command = new OmitirEvaluacionesCuantitativasJuradoCommand(evaluacionJurado, List.of(primera, segunda));

        // Act
        var omision = OmitirEvaluacionesCuantitativasJuradoMapper.toDomain(command);

        // Assert
        assertThat(omision.getEvaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(omision.getEvaluaciones()).containsExactlyInAnyOrder(primera, segunda);
    }
}
