package com.arquisoft.evaluaciones.domain.observacionitemasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemocionObservacionItemAsesorDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var remocion = RemocionObservacionItemAsesorDomain.crear(observacion, asesor);

        // Assert
        assertThat(remocion.getObservacion()).isEqualTo(observacion);
        assertThat(remocion.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoObservacionYAsesorSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> RemocionObservacionItemAsesorDomain.crear(null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.ObservacionItemAsesor.ID_REQUERIDO,
                                        EvaluacionesCodes.ObservacionItemAsesor.ASESOR_REQUERIDO));
    }
}
