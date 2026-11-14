package com.arquisoft.evaluaciones.domain.observacionitemasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificacionObservacionItemAsesorDomainTest {

    @Test
    void debeCrearYRecortarDescripcion_cuandoDatosValidos() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var modificacion = ModificacionObservacionItemAsesorDomain.crear(observacion, asesor, "  Nuevo sustento  ");

        // Assert
        assertThat(modificacion.getObservacion()).isEqualTo(observacion);
        assertThat(modificacion.getAsesor()).isEqualTo(asesor);
        assertThat(modificacion.getDescripcion()).isEqualTo("Nuevo sustento");
    }

    @Test
    void debeAcumularErrores_cuandoTodoInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> ModificacionObservacionItemAsesorDomain.crear(null, null, "   "))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.ObservacionItemAsesor.ID_REQUERIDO,
                                        EvaluacionesCodes.ObservacionItemAsesor.ASESOR_REQUERIDO,
                                        EvaluacionesCodes.ObservacionItemAsesor.DESCRIPCION_REQUERIDA));
    }

    @Test
    void debeRechazar_cuandoDescripcionExcede500() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var descripcionLarga = "a".repeat(501);

        // Act & Assert
        assertThatThrownBy(() -> ModificacionObservacionItemAsesorDomain.crear(observacion, asesor, descripcionLarga))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.ObservacionItemAsesor.DESCRIPCION_DEMASIADO_LARGA));
    }
}
