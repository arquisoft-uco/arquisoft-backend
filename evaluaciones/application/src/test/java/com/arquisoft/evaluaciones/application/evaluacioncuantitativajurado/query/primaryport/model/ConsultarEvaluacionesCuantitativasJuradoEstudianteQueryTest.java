package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarEvaluacionesCuantitativasJuradoEstudianteQueryTest {

    @Test
    void debeCrearQuery_cuandoEvaluacionYSubjectSonValidos() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var estudiante = UUID.randomUUID();

        // Act
        var query = ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery.crear(
                evaluacionJurado, estudiante.toString());

        // Assert
        assertThat(query.evaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeAcumularErroresDeEntrada_cuandoEvaluacionEsNulaYSubjectNoEsUuid() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery.crear(
                null, "no-es-un-uuid"))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaJurado.EVALUACION_JURADO,
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACION_JURADO_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaJurado.ESTUDIANTE,
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.ESTUDIANTE_REQUERIDO)));
    }

    @Test
    void debeAcumularErrorDeEntrada_cuandoSubjectEstaEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery.crear(
                UUID.randomUUID(), "   "))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(
                                        EvaluacionesCodes.EvaluacionCuantitativaJurado.ESTUDIANTE_REQUERIDO));
    }
}
