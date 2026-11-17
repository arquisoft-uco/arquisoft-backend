package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.CambioCriterioEvaluacionCualitativaAsesorFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.EstadoCambioCriterioEvaluacionCualitativaAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionCualitativaAsesorAdmiteCambioCriterioRuleImplTest {

    private final EvaluacionCualitativaAsesorAdmiteCambioCriterioRuleImpl regla =
            new EvaluacionCualitativaAsesorAdmiteCambioCriterioRuleImpl();

    @Test
    void debeLanzar_cuandoFinalizada() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var entrada = new EstadoCambioCriterioEvaluacionCualitativaAsesor(evaluacionAsesor, EstadoEvaluacion.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(CambioCriterioEvaluacionCualitativaAsesorFinalizadaException.class,
                        exception -> {
                            assertThat(exception.getCodigoError()).isEqualTo(EvaluacionesCodes
                                    .EvaluacionCualitativaAsesor.CAMBIO_CRITERIO_EVALUACION_ASESOR_FINALIZADA);
                            assertThat(exception.getError().getMessage())
                                    .doesNotContain(evaluacionAsesor.toString());
                        });
    }

    @Test
    void noDebeLanzar_cuandoPendienteOEnProgreso() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var pendiente = new EstadoCambioCriterioEvaluacionCualitativaAsesor(evaluacionAsesor, EstadoEvaluacion.PENDIENTE);
        var enProgreso = new EstadoCambioCriterioEvaluacionCualitativaAsesor(
                evaluacionAsesor, EstadoEvaluacion.EN_PROGRESO);

        // Act & Assert
        assertThatCode(() -> regla.validar(pendiente)).doesNotThrowAnyException();
        assertThatCode(() -> regla.validar(enProgreso)).doesNotThrowAnyException();
    }
}
