package com.arquisoft.evaluaciones.application.evaluacionasesor.query.validator.impl;

import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.exception.EvaluacionSinEvaluacionAsesorException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEvaluacionAsesorValidatorImplTest {

    private final ConsultarEvaluacionAsesorValidatorImpl validator = new ConsultarEvaluacionAsesorValidatorImpl();

    @Test
    void debeLanzarEvaluacionNoEncontrada_cuandoNoExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var sinEvaluacionAsesor = UtilUUID.obtenerUUIDPorDefecto();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(evaluacion, false, sinEvaluacionAsesor))
                .isInstanceOfSatisfying(EvaluacionNoEncontradaException.class, exception ->
                        assertThat(exception.getCodigoError()).isEqualTo(EvaluacionesCodes.Evaluacion.NO_ENCONTRADA));
    }

    @Test
    void debeLanzarSinEvaluacionAsesor_cuandoExisteSinEvaluacionAsesor() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var sinEvaluacionAsesor = UtilUUID.obtenerUUIDPorDefecto();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(evaluacion, true, sinEvaluacionAsesor))
                .isInstanceOfSatisfying(EvaluacionSinEvaluacionAsesorException.class, exception ->
                        assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.EvaluacionAsesor.SIN_EVALUACION_ASESOR));
    }

    @Test
    void noDebeLanzar_cuandoAmbasSeCumplen() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(evaluacion, true, evaluacionAsesor)).doesNotThrowAnyException();
    }
}
