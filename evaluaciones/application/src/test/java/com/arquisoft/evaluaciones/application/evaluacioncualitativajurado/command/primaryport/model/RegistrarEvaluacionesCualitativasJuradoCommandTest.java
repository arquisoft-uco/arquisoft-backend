package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.RegistrarEvaluacionesCualitativasJuradoCommand.Par;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RegistrarEvaluacionesCualitativasJuradoCommandTest {

    @Test
    void debeCrearCommand_cuandoDatosValidos() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID().toString();
        var item = UUID.randomUUID().toString();
        var criterio = UUID.randomUUID().toString();

        // Act
        var command = RegistrarEvaluacionesCualitativasJuradoCommand.crear(
                evaluacionJurado, List.of(new Par<>(item, criterio)));

        // Assert
        assertThat(command.evaluacionJurado()).isEqualTo(UUID.fromString(evaluacionJurado));
        assertThat(command.evaluaciones()).hasSize(1);
        assertThat(command.evaluaciones().get(0).item()).isEqualTo(UUID.fromString(item));
        assertThat(command.evaluaciones().get(0).criterio()).isEqualTo(UUID.fromString(criterio));
    }

    @Test
    void debeAcumularError_cuandoEvaluacionJuradoEstaEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCualitativasJuradoCommand.crear(
                " ", List.of(new Par<>(UUID.randomUUID().toString(), UUID.randomUUID().toString()))))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .contains(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO)));
    }

    @Test
    void debeAcumularErrorLoteVacio_cuandoLaListaDeParesEsVacia() {
        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCualitativasJuradoCommand.crear(
                UUID.randomUUID().toString(), List.of()))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.RegistroEvaluacionesCualitativasJurado.LOTE_VACIO));
    }

    @Test
    void debeAcumularErroresIndexados_cuandoHayParVacioYParIncompletoOInvalido() {
        // Arrange
        var pares = List.of(
                new Par<>("", ""),
                new Par<>(" ", "no-es-un-uuid"));

        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCualitativasJuradoCommand.crear(
                UUID.randomUUID().toString(), pares))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple("evaluaciones[0].item",
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.ITEM_REQUERIDO),
                                        tuple("evaluaciones[0].criterio",
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIO_REQUERIDO),
                                        tuple("evaluaciones[1].item",
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.ITEM_REQUERIDO),
                                        tuple("evaluaciones[1].criterio",
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIO_INVALIDO)));
    }
}
