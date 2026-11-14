package com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.ModificacionObservacionItemAsesorFinalizadaException;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.EstadoModificacionObservacionItemAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionItemAsesorAdmiteModificacionRuleImplTest {

    private final ObservacionItemAsesorAdmiteModificacionRuleImpl regla =
            new ObservacionItemAsesorAdmiteModificacionRuleImpl();

    @Test
    void debeLanzar_cuandoEstadoFinalizada() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var entrada = new EstadoModificacionObservacionItemAsesor(evaluacionAsesor, EstadoEvaluacion.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(ModificacionObservacionItemAsesorFinalizadaException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ObservacionItemAsesor.MODIFICACION_EVALUACION_FINALIZADA);
                    assertThat(exception.getError().getMessage()).doesNotContain(evaluacionAsesor.toString());
                });
    }

    @Test
    void noDebeLanzar_cuandoEstadoNoFinalizado() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var pendiente = new EstadoModificacionObservacionItemAsesor(evaluacionAsesor, EstadoEvaluacion.PENDIENTE);
        var enProgreso = new EstadoModificacionObservacionItemAsesor(evaluacionAsesor, EstadoEvaluacion.EN_PROGRESO);

        // Act & Assert
        assertThatCode(() -> regla.validar(pendiente)).doesNotThrowAnyException();
        assertThatCode(() -> regla.validar(enProgreso)).doesNotThrowAnyException();
    }
}
