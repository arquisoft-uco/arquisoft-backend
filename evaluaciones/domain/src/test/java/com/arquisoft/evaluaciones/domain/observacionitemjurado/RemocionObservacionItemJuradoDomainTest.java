package com.arquisoft.evaluaciones.domain.observacionitemjurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RemocionObservacionItemJuradoDomainTest {

    @Test
    void debeCrearRemocion_cuandoIdEsValido() {
        // Arrange
        var observacionItemJurado = UtilUUID.generarNuevoUUID();

        // Act
        var remocion = RemocionObservacionItemJuradoDomain.crear(observacionItemJurado);

        // Assert
        assertThat(remocion.getObservacionItemJurado()).isEqualTo(observacionItemJurado);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoIdEsNulo() {
        // Arrange
        UUID observacionItemJurado = null;

        // Act & Assert
        assertThatThrownBy(() -> RemocionObservacionItemJuradoDomain.crear(observacionItemJurado))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ObservacionItemJurado.ID,
                                        EvaluacionesCodes.ObservacionItemJurado.ID_REQUERIDO)));
    }
}
