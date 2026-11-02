package com.arquisoft.evaluaciones.application.evaluacionasesor.query.validator.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoPerteneceAsesorException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEvaluacionAsesorRealizadaValidatorImplTest {

    private final ConsultarEvaluacionAsesorRealizadaValidatorImpl validator =
            new ConsultarEvaluacionAsesorRealizadaValidatorImpl();

    @Test
    void noDebeLanzar_cuandoLaEvaluacionExisteYEsDelAsesor() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(evaluacionAsesor, asesor, asesor))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoPertenece_cuandoLaEvaluacionEsDeOtroAsesor() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asignado = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(evaluacionAsesor, asignado, solicitante))
                .isInstanceOf(EvaluacionAsesorNoPerteneceAsesorException.class);
    }

    @Test
    void debeLanzarNoEncontrada_cuandoNoExisteAunqueElSolicitanteDifiera() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var ausente = UtilUUID.obtenerUUIDPorDefecto();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(evaluacionAsesor, ausente, solicitante))
                .isInstanceOf(EvaluacionAsesorNoEncontradaException.class);
    }
}
