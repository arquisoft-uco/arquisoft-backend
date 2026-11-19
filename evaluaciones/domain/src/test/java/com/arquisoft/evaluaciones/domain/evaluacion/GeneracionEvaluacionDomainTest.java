package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.evaluaciones.domain.entregable.EntregableDomain;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.EvaluacionAsesorDomain;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeneracionEvaluacionDomainTest {

    @Test
    void debeCrear_cuandoPartesPresentes() {
        // Arrange
        var entregable = EntregableDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Sistema de riego", 1);
        var evaluacion = EvaluacionDomain.crear(entregable.getId());
        var evaluacionAsesor = EvaluacionAsesorDomain.crear(evaluacion.getId(), UtilUUID.generarNuevoUUID());

        // Act
        var generacion = GeneracionEvaluacionDomain.crear(entregable, evaluacion, evaluacionAsesor);

        // Assert
        assertThat(generacion.getEntregable()).isSameAs(entregable);
        assertThat(generacion.getEvaluacion()).isSameAs(evaluacion);
        assertThat(generacion.getEvaluacionAsesor()).isSameAs(evaluacionAsesor);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanEntregableYEvaluacion() {
        // Act & Assert
        assertThatThrownBy(() -> GeneracionEvaluacionDomain.crear(null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("codigoError")
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.GeneracionEvaluacion.ENTREGABLE_REQUERIDO,
                                        EvaluacionesCodes.GeneracionEvaluacion.EVALUACION_REQUERIDA,
                                        EvaluacionesCodes.GeneracionEvaluacion.EVALUACION_ASESOR_REQUERIDA));
    }
}
