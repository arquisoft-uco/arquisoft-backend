package com.arquisoft.evaluaciones.domain.observacionitemasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistroObservacionItemAsesorDomainTest {

    @Test
    void debeCrear_cuandoComponentesPresentes() {
        // Arrange
        var observacion = ObservacionItemAsesorDomain.crear(UtilUUID.generarNuevoUUID(), "Descripcion");
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var registro = RegistroObservacionItemAsesorDomain.crear(observacion, asesor);

        // Assert
        assertThat(registro.getObservacion()).isSameAs(observacion);
        assertThat(registro.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoComponentesNulos() {
        // Act & Assert
        assertThatThrownBy(() -> RegistroObservacionItemAsesorDomain.crear(null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.ObservacionItemAsesor.OBSERVACION_REQUERIDA,
                                        EvaluacionesCodes.ObservacionItemAsesor.ASESOR_REQUERIDO));
    }
}
