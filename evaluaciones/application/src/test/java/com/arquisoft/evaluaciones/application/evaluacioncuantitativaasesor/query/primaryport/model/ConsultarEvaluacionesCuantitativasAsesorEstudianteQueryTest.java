package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarEvaluacionesCuantitativasAsesorEstudianteQueryTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery.crear(evaluacionAsesor, estudiante);

        // Assert
        assertThat(query.evaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeAcumularErrores_cuandoAmbosSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery.crear(null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR,
                                                EvaluacionesCodes.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaAsesor.ESTUDIANTE,
                                                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ESTUDIANTE_REQUERIDO)));
    }
}
