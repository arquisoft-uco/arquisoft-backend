package com.arquisoft.evaluaciones.domain.observacionitemjurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ObservacionItemJuradoDomainTest {

    @Test
    void debeCrearObservacion_cuandoDatosValidos() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();

        // Act
        var observacion = ObservacionItemJuradoDomain.crear(
                evaluacionCuantitativaJurado, "Sustenta bien el puntaje otorgado");

        // Assert
        assertThat(observacion.getId()).isNotNull();
        assertThat(observacion.getEvaluacionCuantitativaJurado()).isEqualTo(evaluacionCuantitativaJurado);
        assertThat(observacion.getDescripcion()).isEqualTo("Sustenta bien el puntaje otorgado");
    }

    @Test
    void debeAcumularErrores_cuandoEvaluacionCuantitativaJuradoYDescripcionSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> ObservacionItemJuradoDomain.crear(null, "   "))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.ObservacionItemJurado.EVALUACION_CUANTITATIVA_JURADO,
                                                EvaluacionesCodes.ObservacionItemJurado
                                                        .EVALUACION_CUANTITATIVA_JURADO_REQUERIDA),
                                        tuple(EvaluacionesFields.ObservacionItemJurado.DESCRIPCION,
                                                EvaluacionesCodes.ObservacionItemJurado.DESCRIPCION_REQUERIDA)));
    }

    @Test
    void debeAcumularErrorDescripcionDemasiadoLarga_cuandoExcedeQuinientosCaracteres() {
        // Arrange
        var descripcionLarga = "a".repeat(501);

        // Act & Assert
        assertThatThrownBy(() -> ObservacionItemJuradoDomain.crear(UUID.randomUUID(), descripcionLarga))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.ObservacionItemJurado.DESCRIPCION_DEMASIADO_LARGA));
    }

    @Test
    void debeRecortarDescripcion_cuandoTieneEspaciosAlInicioYFin() {
        // Act
        var observacion = ObservacionItemJuradoDomain.crear(UUID.randomUUID(), "   Buen sustento   ");

        // Assert
        assertThat(observacion.getDescripcion()).isEqualTo("Buen sustento");
    }

    @Test
    void debeReconstruirSinValidar_cuandoSeCargaDesdePersistencia() {
        // Arrange
        var id = UUID.randomUUID();

        // Act
        var observacion = ObservacionItemJuradoDomain.reconstruir(id, null, null);

        // Assert
        assertThat(observacion.getId()).isEqualTo(id);
        assertThat(observacion.getEvaluacionCuantitativaJurado()).isNull();
        assertThat(observacion.getDescripcion()).isNull();
    }
}
