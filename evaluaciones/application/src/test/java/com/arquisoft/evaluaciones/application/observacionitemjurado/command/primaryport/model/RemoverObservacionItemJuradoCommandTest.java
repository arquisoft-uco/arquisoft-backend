package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RemoverObservacionItemJuradoCommandTest {

    @Test
    void debeCrearCommand_cuandoIdEsValido() {
        // Arrange
        var observacionItemJurado = UtilUUID.generarNuevoUUID();

        // Act
        var command = RemoverObservacionItemJuradoCommand.crear(observacionItemJurado);

        // Assert
        assertThat(command.observacionItemJurado()).isEqualTo(observacionItemJurado);
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoIdEsNulo() {
        // Arrange
        UUID observacionItemJurado = null;

        // Act & Assert
        assertThatThrownBy(() -> RemoverObservacionItemJuradoCommand.crear(observacionItemJurado))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ObservacionItemJurado.ID,
                                        EvaluacionesCodes.ObservacionItemJurado.ID_REQUERIDO)));
    }
}
