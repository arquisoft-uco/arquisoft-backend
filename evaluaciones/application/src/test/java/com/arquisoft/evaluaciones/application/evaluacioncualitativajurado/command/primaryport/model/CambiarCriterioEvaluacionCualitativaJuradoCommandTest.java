package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class CambiarCriterioEvaluacionCualitativaJuradoCommandTest {

    @Test
    void debeCrearCommand_cuandoDatosValidos() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();

        // Act
        var command = CambiarCriterioEvaluacionCualitativaJuradoCommand.crear(
                evaluacion, criterio.toString(), jurado);

        // Assert
        assertThat(command.evaluacionCualitativaJurado()).isEqualTo(evaluacion);
        assertThat(command.criterio()).isEqualTo(criterio);
        assertThat(command.jurado()).isEqualTo(jurado);
    }

    @Test
    void debeFallarConCriterioRequerido_cuandoElCriterioEstaEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarCriterioEvaluacionCualitativaJuradoCommand.crear(
                UtilUUID.generarNuevoUUID(), "  ", UtilUUID.generarNuevoUUID()))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.CRITERIO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIO_REQUERIDO)));
    }

    @Test
    void debeFallarConCriterioInvalido_cuandoElCriterioNoEsUuid() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarCriterioEvaluacionCualitativaJuradoCommand.crear(
                UtilUUID.generarNuevoUUID(), "no-es-un-uuid", UtilUUID.generarNuevoUUID()))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.CRITERIO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIO_INVALIDO)));
    }

    @Test
    void debeAcumularErrores_cuandoTodoEsInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarCriterioEvaluacionCualitativaJuradoCommand.crear(null, null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.ID,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.ID_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.CRITERIO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIO_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.JURADO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.JURADO_REQUERIDO)));
    }
}
