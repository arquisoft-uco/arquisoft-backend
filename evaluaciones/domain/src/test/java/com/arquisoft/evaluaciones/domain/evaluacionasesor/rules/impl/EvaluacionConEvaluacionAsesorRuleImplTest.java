package com.arquisoft.evaluaciones.domain.evaluacionasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacionasesor.exception.EvaluacionSinEvaluacionAsesorException;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.model.ExistenciaEvaluacionAsesorDeEvaluacion;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionConEvaluacionAsesorRuleImplTest {

    private final EvaluacionConEvaluacionAsesorRuleImpl regla = new EvaluacionConEvaluacionAsesorRuleImpl();

    @Test
    void noDebeLanzar_cuandoLaEvaluacionTieneEvaluacionAsesor() {
        // Arrange
        var existencia = new ExistenciaEvaluacionAsesorDeEvaluacion(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarEvaluacionSinEvaluacionAsesor_cuandoElIdEsElPorDefecto() {
        // Arrange
        var existencia = new ExistenciaEvaluacionAsesorDeEvaluacion(
                UtilUUID.generarNuevoUUID(), UtilUUID.obtenerUUIDPorDefecto());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOfSatisfying(EvaluacionSinEvaluacionAsesorException.class, exception ->
                        assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.EvaluacionAsesor.SIN_EVALUACION_ASESOR));
    }
}
