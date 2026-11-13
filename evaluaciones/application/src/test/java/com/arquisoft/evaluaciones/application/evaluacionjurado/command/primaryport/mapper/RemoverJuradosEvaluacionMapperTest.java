package com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.model.RemoverJuradosEvaluacionCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverJuradosEvaluacionMapperTest {

    @Test
    void debeArmarObjetoDeAccionConIdsPlanos_cuandoSeMapeaElCommand() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var juradoUno = UtilUUID.generarNuevoUUID();
        var juradoDos = UtilUUID.generarNuevoUUID();
        var command = new RemoverJuradosEvaluacionCommand(evaluacion, List.of(juradoUno, juradoDos));

        // Act
        var remocion = RemoverJuradosEvaluacionMapper.toDomain(command);

        // Assert
        assertThat(remocion.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(remocion.getJurados()).containsExactlyInAnyOrder(juradoUno, juradoDos);
    }
}
