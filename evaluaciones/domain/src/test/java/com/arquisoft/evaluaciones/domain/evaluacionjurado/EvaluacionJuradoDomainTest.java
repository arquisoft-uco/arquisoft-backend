package com.arquisoft.evaluaciones.domain.evaluacionjurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class EvaluacionJuradoDomainTest {

    @Test
    void debeCrearEvaluacionJuradoConIdGenerado_cuandoDatosValidos() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();

        // Act
        var evaluacionJurado = EvaluacionJuradoDomain.crear(evaluacion, jurado);

        // Assert
        assertThat(evaluacionJurado.getId()).isNotNull();
        assertThat(evaluacionJurado.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(evaluacionJurado.getJurado()).isEqualTo(jurado);
    }

    @Test
    void debeAcumularErrores_cuandoEvaluacionYJuradoSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> EvaluacionJuradoDomain.crear(null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionJurado.EVALUACION,
                                                EvaluacionesCodes.EvaluacionJurado.EVALUACION_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionJurado.JURADO,
                                                EvaluacionesCodes.EvaluacionJurado.JURADO_REQUERIDO)));
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var evaluacionJurado = EvaluacionJuradoDomain.reconstruir(id, null, null);

        // Assert
        assertThat(evaluacionJurado.getId()).isEqualTo(id);
        assertThat(evaluacionJurado.getEvaluacion()).isNull();
        assertThat(evaluacionJurado.getJurado()).isNull();
    }
}
