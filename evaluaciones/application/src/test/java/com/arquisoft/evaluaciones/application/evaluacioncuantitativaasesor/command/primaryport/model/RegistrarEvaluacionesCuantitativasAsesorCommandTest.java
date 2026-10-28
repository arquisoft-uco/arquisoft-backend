package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.RegistrarEvaluacionesCuantitativasAsesorCommand.ParEntrada;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RegistrarEvaluacionesCuantitativasAsesorCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();

        // Act
        var command = RegistrarEvaluacionesCuantitativasAsesorCommand.crear(
                evaluacionAsesor, asesor, List.of(new ParEntrada(item.toString(), 25)));

        // Assert
        assertThat(command.evaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.evaluaciones()).hasSize(1);
        assertThat(command.evaluaciones().get(0).item()).isEqualTo(item);
        assertThat(command.evaluaciones().get(0).puntaje()).isEqualTo(25);
    }

    @Test
    void debeRechazar_cuandoLoteVacio() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var pares = List.<ParEntrada>of();

        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCuantitativasAsesorCommand.crear(evaluacionAsesor, asesor, pares))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.RegistroEvaluacionesCuantitativasAsesor.LOTE_VACIO));
    }

    @Test
    void debeAcumularErroresIndexados_cuandoItemYPuntajeSonInvalidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var pares = List.of(
                new ParEntrada("", null),
                new ParEntrada("no-es-un-uuid", EvaluacionesLimits.EvaluacionCuantitativaAsesor.PUNTAJE_MAX + 1),
                new ParEntrada(UtilUUID.generarNuevoUUID().toString(), -1));

        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCuantitativasAsesorCommand.crear(evaluacionAsesor, asesor, pares))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple("evaluaciones[0].item",
                                                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ITEM_REQUERIDO),
                                        tuple("evaluaciones[0].puntaje",
                                                EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_REQUERIDO),
                                        tuple("evaluaciones[1].item",
                                                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ITEM_INVALIDO),
                                        tuple("evaluaciones[1].puntaje",
                                                EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_FUERA_DE_RANGO),
                                        tuple("evaluaciones[2].puntaje",
                                                EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_FUERA_DE_RANGO)));
    }

    @Test
    void debeAcumularErrores_cuandoEvaluacionAsesorYAsesorSonNulos() {
        // Arrange
        var pares = List.of(new ParEntrada(UtilUUID.generarNuevoUUID().toString(), 10));

        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCuantitativasAsesorCommand.crear(null, null, pares))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR,
                                                EvaluacionesCodes.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaAsesor.ASESOR,
                                                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ASESOR_REQUERIDO)));
    }
}
