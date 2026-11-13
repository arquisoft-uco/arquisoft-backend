package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.RegistrarEvaluacionesCuantitativasJuradoCommand.ParEntrada;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RegistrarEvaluacionesCuantitativasJuradoCommandTest {

    @Test
    void debeCrearCommand_cuandoDatosValidos() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();

        // Act
        var command = RegistrarEvaluacionesCuantitativasJuradoCommand.crear(
                evaluacionJurado, jurado, List.of(new ParEntrada(item.toString(), 250)));

        // Assert
        assertThat(command.evaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(command.jurado()).isEqualTo(jurado);
        assertThat(command.evaluaciones()).containsExactly(
                new RegistrarEvaluacionesCuantitativasJuradoCommand.Par(item, 250));
    }

    @Test
    void debeAcumularErrores_cuandoEvaluacionJuradoJuradoItemYPuntajeInvalidos() {
        // Arrange
        var pares = List.of(
                new ParEntrada(" ", null),
                new ParEntrada("no-es-un-uuid", 501),
                new ParEntrada(UtilUUID.generarNuevoUUID().toString(), -1));

        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCuantitativasJuradoCommand.crear(null, null, pares))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple("evaluacionJurado",
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACION_JURADO_REQUERIDO),
                                        tuple("jurado",
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.JURADO_REQUERIDO),
                                        tuple("evaluaciones[0].item",
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.ITEM_REQUERIDO),
                                        tuple("evaluaciones[0].puntaje",
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_REQUERIDO),
                                        tuple("evaluaciones[1].item",
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.ITEM_INVALIDO),
                                        tuple("evaluaciones[1].puntaje",
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_FUERA_DE_RANGO),
                                        tuple("evaluaciones[2].puntaje",
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_FUERA_DE_RANGO)));
    }

    @Test
    void debeAcumularErrorLoteVacio_cuandoLaListaDeParesEsVacia() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCuantitativasJuradoCommand.crear(
                evaluacionJurado, jurado, List.of()))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.RegistroEvaluacionesCuantitativasJurado.LOTE_VACIO));
    }
}
