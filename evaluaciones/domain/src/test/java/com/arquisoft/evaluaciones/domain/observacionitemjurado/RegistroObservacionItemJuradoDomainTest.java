package com.arquisoft.evaluaciones.domain.observacionitemjurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistroObservacionItemJuradoDomainTest {

    @Test
    void debeCrearRegistro_cuandoDatosValidos() {
        // Arrange
        var observacion = ObservacionItemJuradoDomain.crear(UUID.randomUUID(), "Descripción válida");
        var jurado = UUID.randomUUID();

        // Act
        var registro = RegistroObservacionItemJuradoDomain.crear(observacion, jurado);

        // Assert
        assertThat(registro.getObservacion()).isEqualTo(observacion);
        assertThat(registro.getJurado()).isEqualTo(jurado);
    }

    @Test
    void debeAcumularErrores_cuandoObservacionYJuradoSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> RegistroObservacionItemJuradoDomain.crear(null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.ObservacionItemJurado
                                                .EVALUACION_CUANTITATIVA_JURADO_REQUERIDA,
                                        EvaluacionesCodes.ObservacionItemJurado.JURADO_REQUERIDO));
    }
}
