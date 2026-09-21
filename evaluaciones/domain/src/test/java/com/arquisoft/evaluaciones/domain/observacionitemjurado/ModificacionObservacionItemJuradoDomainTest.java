package com.arquisoft.evaluaciones.domain.observacionitemjurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ModificacionObservacionItemJuradoDomainTest {

    @Test
    void debeCrearYRecortarDescripcion_cuandoDatosValidos() {
        // Arrange
        var observacionItemJurado = UUID.randomUUID();
        var descripcion = "   Sustenta bien el puntaje otorgado   ";

        // Act
        var modificacion = ModificacionObservacionItemJuradoDomain.crear(observacionItemJurado, descripcion);

        // Assert
        assertThat(modificacion.getObservacionItemJurado()).isEqualTo(observacionItemJurado);
        assertThat(modificacion.getDescripcion()).isEqualTo("Sustenta bien el puntaje otorgado");
    }

    @Test
    void debeAcumularErrores_cuandoIdEsNuloYDescripcionEnBlanco() {
        // Arrange
        UUID observacionItemJurado = null;
        var descripcion = "   ";

        // Act & Assert
        assertThatThrownBy(() -> ModificacionObservacionItemJuradoDomain.crear(observacionItemJurado, descripcion))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
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
        assertThatThrownBy(() -> ModificacionObservacionItemJuradoDomain.crear(observacionItemJurado, descripcionLarga))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(EvaluacionesFields.ObservacionItemJurado.DESCRIPCION,
                                        EvaluacionesCodes.ObservacionItemJurado.DESCRIPCION_DEMASIADO_LARGA)));
    }
}
