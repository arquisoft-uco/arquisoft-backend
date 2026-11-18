package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.OmisionEvaluacionAsesorFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.EstadoOmisionEvaluacionesCuantitativasAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionCuantitativaAsesorAdmiteOmisionRuleImplTest {

    private final EvaluacionCuantitativaAsesorAdmiteOmisionRuleImpl regla =
            new EvaluacionCuantitativaAsesorAdmiteOmisionRuleImpl();

    @Test
    void debeLanzar_cuandoEstadoFinalizada() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var entrada = new EstadoOmisionEvaluacionesCuantitativasAsesor(evaluacionAsesor, EstadoEvaluacion.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(OmisionEvaluacionAsesorFinalizadaException.class, exception -> {
                    assertThat(exception.getCodigoError()).isEqualTo(
                            EvaluacionesCodes.EvaluacionCuantitativaAsesor.OMISION_EVALUACION_ASESOR_FINALIZADA);
                    assertThat(exception.getError().getMessage()).doesNotContain(evaluacionAsesor.toString());
                });
    }

    @Test
    void noDebeLanzar_cuandoEstadoNoFinalizado() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var pendiente = new EstadoOmisionEvaluacionesCuantitativasAsesor(evaluacionAsesor, EstadoEvaluacion.PENDIENTE);
        var enProgreso = new EstadoOmisionEvaluacionesCuantitativasAsesor(
                evaluacionAsesor, EstadoEvaluacion.EN_PROGRESO);

        // Act & Assert
        assertThatCode(() -> regla.validar(pendiente)).doesNotThrowAnyException();
        assertThatCode(() -> regla.validar(enProgreso)).doesNotThrowAnyException();
    }
}
