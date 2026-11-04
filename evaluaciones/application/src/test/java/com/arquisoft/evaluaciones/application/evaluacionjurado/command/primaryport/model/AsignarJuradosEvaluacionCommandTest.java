package com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class AsignarJuradosEvaluacionCommandTest {

    @Test
    void debeCrearCommand_cuandoDatosValidos() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var juradoUno = UtilUUID.generarNuevoUUID();
        var juradoDos = UtilUUID.generarNuevoUUID();

        // Act
        var command = AsignarJuradosEvaluacionCommand.crear(
                evaluacion.toString(), List.of(juradoUno.toString(), juradoDos.toString()));

        // Assert
        assertThat(command.evaluacion()).isEqualTo(evaluacion);
        assertThat(command.jurados()).containsExactly(juradoUno, juradoDos);
    }

    @Test
    void debeAcumularErrores_cuandoEvaluacionEnBlancoYJuradosInvalidos() {
        // Arrange
        var juradoValido = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        assertThatThrownBy(() -> AsignarJuradosEvaluacionCommand.crear(
                " ", List.of(juradoValido, "no-es-un-uuid", "")))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .contains(
                                        tuple(EvaluacionesFields.EvaluacionJurado.EVALUACION,
                                                EvaluacionesCodes.EvaluacionJurado.EVALUACION_REQUERIDO),
                                        tuple("jurados[1]", EvaluacionesCodes.EvaluacionJurado.JURADO_INVALIDO),
                                        tuple("jurados[2]", EvaluacionesCodes.EvaluacionJurado.JURADO_REQUERIDO)));
    }

    @Test
    void debeAcumularErrorLoteVacio_cuandoNoHayJurados() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        assertThatThrownBy(() -> AsignarJuradosEvaluacionCommand.crear(evaluacion, List.of()))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.AsignacionJuradosEvaluacion.JURADOS,
                                                EvaluacionesCodes.AsignacionJuradosEvaluacion.LOTE_VACIO)));
    }
}
