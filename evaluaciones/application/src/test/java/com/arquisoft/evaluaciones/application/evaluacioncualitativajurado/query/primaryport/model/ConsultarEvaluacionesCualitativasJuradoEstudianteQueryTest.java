package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarEvaluacionesCualitativasJuradoEstudianteQueryTest {

    @Test
    void debeCrearQuery_cuandoEvaluacionYSubjectSonValidos() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();

        // Act
        ConsultarEvaluacionesCualitativasJuradoEstudianteQuery query =
                ConsultarEvaluacionesCualitativasJuradoEstudianteQuery.crear(
                        evaluacionJurado, estudiante.toString());

        // Assert
        assertThat(query.evaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeAcumularErroresDeEntrada_cuandoEvaluacionEsNulaYSubjectNoEsUuid() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEvaluacionesCualitativasJuradoEstudianteQuery.crear(
                null, "no-es-un-uuid"))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.ESTUDIANTE,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.ESTUDIANTE_REQUERIDO)));
    }

    @Test
    void debeAcumularErrorDeEntrada_cuandoSubjectEstaEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEvaluacionesCualitativasJuradoEstudianteQuery.crear(
                UUID.randomUUID(), "   "))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(
                                        EvaluacionesCodes.EvaluacionCualitativaJurado.ESTUDIANTE_REQUERIDO));
    }
}
