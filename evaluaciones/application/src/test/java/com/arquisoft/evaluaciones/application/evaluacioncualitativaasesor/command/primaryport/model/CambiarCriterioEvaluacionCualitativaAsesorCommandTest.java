package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarCriterioEvaluacionCualitativaAsesorCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var evaluacionCualitativa = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();

        // Act
        var command = CambiarCriterioEvaluacionCualitativaAsesorCommand.crear(
                evaluacionCualitativa, criterio.toString(), asesor);

        // Assert
        assertThat(command.evaluacionCualitativaAsesor()).isEqualTo(evaluacionCualitativa);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.criterio()).isEqualTo(criterio);
    }

    @Test
    void debeLanzar400ConFieldErrors_cuandoFaltanDatos() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarCriterioEvaluacionCualitativaAsesorCommand.crear(null, null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_CUALITATIVA_REQUERIDA,
                                        EvaluacionesCodes.EvaluacionCualitativaAsesor.ASESOR_REQUERIDO,
                                        EvaluacionesCodes.EvaluacionCualitativaAsesor.CRITERIO_REQUERIDO));
    }

    @Test
    void debeLanzar400_cuandoCriterioNoEsUuid() {
        // Arrange
        var evaluacionCualitativa = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambiarCriterioEvaluacionCualitativaAsesorCommand.crear(
                evaluacionCualitativa, "no-es-uuid", asesor))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.EvaluacionCualitativaAsesor.CRITERIO_INVALIDO));
    }

    @Test
    void debeLanzar400_cuandoCriterioEnBlanco() {
        // Arrange
        var evaluacionCualitativa = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambiarCriterioEvaluacionCualitativaAsesorCommand.crear(
                evaluacionCualitativa, "   ", asesor))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.EvaluacionCualitativaAsesor.CRITERIO_REQUERIDO));
    }
}
