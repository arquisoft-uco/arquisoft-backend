package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionDomainTest {

    @Test
    void debeCrearPendienteConIdGenerado_cuandoEntregableValido() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act
        var evaluacion = EvaluacionDomain.crear(entregable);

        // Assert
        assertThat(evaluacion.getId()).isNotNull();
        assertThat(evaluacion.getEntregable()).isEqualTo(entregable);
        assertThat(evaluacion.getEstado()).isEqualTo(EstadoEvaluacion.PENDIENTE);
    }

    @Test
    void debeAcumularError_cuandoEntregableNulo() {
        // Act & Assert
        assertThatThrownBy(() -> EvaluacionDomain.crear(null))
                .isInstanceOfSatisfying(DomainValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("codigoError")
                                .containsExactly(EvaluacionesCodes.Evaluacion.ENTREGABLE_REQUERIDO));
    }
}
