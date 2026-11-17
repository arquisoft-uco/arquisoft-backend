package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.OmisionEvaluacionCualitativaAsesorFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.EstadoOmisionEvaluacionesCualitativasAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionCualitativaAsesorAdmiteOmisionRuleImplTest {

    private final EvaluacionCualitativaAsesorAdmiteOmisionRuleImpl regla =
            new EvaluacionCualitativaAsesorAdmiteOmisionRuleImpl();

    @Test
    void debeLanzar_cuandoEstadoFinalizada() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var entrada = new EstadoOmisionEvaluacionesCualitativasAsesor(evaluacionAsesor, EstadoEvaluacion.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(OmisionEvaluacionCualitativaAsesorFinalizadaException.class, exception -> {
                    assertThat(exception.getCodigoError()).isEqualTo(
                            EvaluacionesCodes.EvaluacionCualitativaAsesor.OMISION_EVALUACION_ASESOR_FINALIZADA);
                    assertThat(exception.getError().getMessage()).doesNotContain(evaluacionAsesor.toString());
                });
    }

    @Test
    void noDebeLanzar_cuandoEstadoNoFinalizado() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var pendiente = new EstadoOmisionEvaluacionesCualitativasAsesor(evaluacionAsesor, EstadoEvaluacion.PENDIENTE);
        var enProgreso = new EstadoOmisionEvaluacionesCualitativasAsesor(
                evaluacionAsesor, EstadoEvaluacion.EN_PROGRESO);

        // Act & Assert
        assertThatCode(() -> regla.validar(pendiente)).doesNotThrowAnyException();
        assertThatCode(() -> regla.validar(enProgreso)).doesNotThrowAnyException();
    }
}
