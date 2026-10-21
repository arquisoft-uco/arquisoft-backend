package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.validator.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoPerteneceAsesorException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEvaluacionesCualitativasAsesorValidatorImplTest {

    private final ConsultarEvaluacionesCualitativasAsesorValidatorImpl validator =
            new ConsultarEvaluacionesCualitativasAsesorValidatorImpl();

    @Test
    void noDebeLanzar_cuandoLaEvaluacionExisteYEsDelAsesor() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(UtilUUID.generarNuevoUUID(), asesor, asesor))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoPertenece_cuandoLaEvaluacionEsDeOtroAsesor() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()))
                .isInstanceOf(EvaluacionAsesorNoPerteneceAsesorException.class);
    }

    @Test
    void debePriorizarNoEncontrada_cuandoLaEvaluacionNoExisteAunqueElSolicitanteDifiera() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                UtilUUID.generarNuevoUUID(), UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.generarNuevoUUID()))
                .isInstanceOf(EvaluacionAsesorNoEncontradaException.class);
    }
}
