package com.arquisoft.evaluaciones.domain.evaluacionjurado;

import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionJuradoDomainTest {

    @Test
    void debeCrearEvaluacionJurado_cuandoDatosValidos() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        var jurado = UUID.randomUUID();

        // Act
        var evaluacionJurado = EvaluacionJuradoDomain.crear(evaluacion, jurado);

        // Assert
        assertThat(evaluacionJurado.getId()).isNotNull();
        assertThat(evaluacionJurado.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(evaluacionJurado.getJurado()).isEqualTo(jurado);
    }

    @Test
    void debeAcumularAmbosFieldErrors_cuandoEvaluacionYJuradoSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> EvaluacionJuradoDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores)
                            .extracting("campo")
                            .containsExactlyInAnyOrder(
                                    EvaluacionesFields.EvaluacionJurado.EVALUACION,
                                    EvaluacionesFields.EvaluacionJurado.JURADO);
                });
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();

        // Act
        var evaluacionJurado = EvaluacionJuradoDomain.reconstruir(id, null, null);

        // Assert
        assertThat(evaluacionJurado.getId()).isEqualTo(id);
        assertThat(evaluacionJurado.getEvaluacion()).isNull();
        assertThat(evaluacionJurado.getJurado()).isNull();
    }
}
