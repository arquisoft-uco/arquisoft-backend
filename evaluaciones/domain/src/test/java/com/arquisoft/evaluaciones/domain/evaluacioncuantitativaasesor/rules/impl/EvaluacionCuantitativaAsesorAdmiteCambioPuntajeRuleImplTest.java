package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.CambioPuntajeEvaluacionAsesorFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.EstadoCambioPuntajeEvaluacionCuantitativaAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionCuantitativaAsesorAdmiteCambioPuntajeRuleImplTest {

    private final EvaluacionCuantitativaAsesorAdmiteCambioPuntajeRuleImpl regla =
            new EvaluacionCuantitativaAsesorAdmiteCambioPuntajeRuleImpl();

    @Test
    void debeLanzar_cuandoFinalizada() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var entrada = new EstadoCambioPuntajeEvaluacionCuantitativaAsesor(
                evaluacionAsesor, EstadoEvaluacion.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(CambioPuntajeEvaluacionAsesorFinalizadaException.class, exception -> {
                    assertThat(exception.getCodigoError()).isEqualTo(
                            EvaluacionesCodes.EvaluacionCuantitativaAsesor.CAMBIO_PUNTAJE_EVALUACION_FINALIZADA);
                    assertThat(exception.getError().getMessage()).doesNotContain(evaluacionAsesor.toString());
                });
    }

    @Test
    void debePasar_cuandoNoFinalizada() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var pendiente = new EstadoCambioPuntajeEvaluacionCuantitativaAsesor(
                evaluacionAsesor, EstadoEvaluacion.PENDIENTE);
        var enProgreso = new EstadoCambioPuntajeEvaluacionCuantitativaAsesor(
                evaluacionAsesor, EstadoEvaluacion.EN_PROGRESO);

        // Act & Assert
        assertThatCode(() -> regla.validar(pendiente)).doesNotThrowAnyException();
        assertThatCode(() -> regla.validar(enProgreso)).doesNotThrowAnyException();
    }
}
