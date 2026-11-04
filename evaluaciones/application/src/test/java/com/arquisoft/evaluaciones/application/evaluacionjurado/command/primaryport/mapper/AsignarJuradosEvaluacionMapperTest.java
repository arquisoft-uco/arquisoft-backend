package com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.model.AsignarJuradosEvaluacionCommand;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.EvaluacionJuradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class AsignarJuradosEvaluacionMapperTest {

    @Test
    void debeArmarCompuestoConUnaEvaluacionJuradoPorJurado_cuandoSeMapeaElCommand() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var juradoUno = UtilUUID.generarNuevoUUID();
        var juradoDos = UtilUUID.generarNuevoUUID();
        var command = new AsignarJuradosEvaluacionCommand(evaluacion, List.of(juradoUno, juradoDos));

        // Act
        var asignacion = AsignarJuradosEvaluacionMapper.toDomain(command);

        // Assert
        assertThat(asignacion.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(asignacion.getJuradosEvaluacion())
                .extracting(EvaluacionJuradoDomain::getEvaluacion, EvaluacionJuradoDomain::getJurado)
                .containsExactly(
                        tuple(evaluacion, juradoUno),
                        tuple(evaluacion, juradoDos));
        assertThat(asignacion.getJuradosEvaluacion())
                .extracting(EvaluacionJuradoDomain::getId)
                .doesNotContainNull()
                .doesNotHaveDuplicates();
    }
}
