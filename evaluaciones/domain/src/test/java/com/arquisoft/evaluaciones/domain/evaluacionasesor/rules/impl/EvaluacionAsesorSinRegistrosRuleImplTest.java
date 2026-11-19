package com.arquisoft.evaluaciones.domain.evaluacionasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacionasesor.exception.EvaluacionAsesorConEvaluacionesException;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.model.CambioAsesorEvaluacion;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionAsesorSinRegistrosRuleImplTest {

    private final EvaluacionAsesorSinRegistrosRuleImpl regla = new EvaluacionAsesorSinRegistrosRuleImpl();

    @Test
    void noDebeLanzar_cuandoNoHayAsesorActual() {
        // Arrange
        var cambio = new CambioAsesorEvaluacion(
                UtilUUID.generarNuevoUUID(), UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(cambio)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoElAsesorEsElMismo() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var cambio = new CambioAsesorEvaluacion(UtilUUID.generarNuevoUUID(), asesor, asesor, true);

        // Act & Assert
        assertThatCode(() -> regla.validar(cambio)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoElAsesorActualNoTieneRegistros() {
        // Arrange
        var cambio = new CambioAsesorEvaluacion(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatCode(() -> regla.validar(cambio)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarConEvaluaciones_cuandoCambiaDeAsesorConRegistros() {
        // Arrange
        var actual = UtilUUID.generarNuevoUUID();
        var cambio = new CambioAsesorEvaluacion(
                UtilUUID.generarNuevoUUID(), actual, UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(cambio))
                .isInstanceOfSatisfying(EvaluacionAsesorConEvaluacionesException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionAsesor.CON_EVALUACIONES);
                    assertThat(exception.getMessage()).contains(actual.toString());
                });
    }
}
