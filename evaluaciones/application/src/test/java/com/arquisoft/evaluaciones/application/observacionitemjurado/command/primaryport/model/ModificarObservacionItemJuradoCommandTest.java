package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ModificarObservacionItemJuradoCommandTest {

    @Test
    void debeCrearYRecortarDescripcion_cuandoDatosValidos() {
        // Arrange
        var observacionItemJurado = UUID.randomUUID();

        // Act
        var command = ModificarObservacionItemJuradoCommand.crear(
                observacionItemJurado, "  Sustenta el puntaje otorgado  ");

        // Assert
        assertThat(command.observacionItemJurado()).isEqualTo(observacionItemJurado);
        assertThat(command.descripcion()).isEqualTo("Sustenta el puntaje otorgado");
    }

    @Test
    void debeAcumularErroresDeEntrada_cuandoIdEsNuloYDescripcionEnBlanco() {
        // Arrange
        UUID observacionItemJurado = null;

        // Act & Assert
        assertThatThrownBy(() -> ModificarObservacionItemJuradoCommand.crear(observacionItemJurado, "   "))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.ObservacionItemJurado.ID,
                                                EvaluacionesCodes.ObservacionItemJurado.ID_REQUERIDO),
                                        tuple(EvaluacionesFields.ObservacionItemJurado.DESCRIPCION,
                                                EvaluacionesCodes.ObservacionItemJurado.DESCRIPCION_REQUERIDA)));
    }

    @Test
    void debeRechazarDescripcion_cuandoSuperaQuinientosCaracteres() {
        // Arrange
        var observacionItemJurado = UUID.randomUUID();
        var descripcionLarga = "a".repeat(501);

        // Act & Assert
        assertThatThrownBy(() -> ModificarObservacionItemJuradoCommand.crear(observacionItemJurado, descripcionLarga))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(EvaluacionesFields.ObservacionItemJurado.DESCRIPCION,
                                        EvaluacionesCodes.ObservacionItemJurado.DESCRIPCION_DEMASIADO_LARGA)));
    }
}
