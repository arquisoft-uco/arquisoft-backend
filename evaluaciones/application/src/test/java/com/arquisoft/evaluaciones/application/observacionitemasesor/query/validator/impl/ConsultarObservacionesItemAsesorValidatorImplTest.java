package com.arquisoft.evaluaciones.application.observacionitemasesor.query.validator.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoPerteneceAsesorException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.AccesoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.EvaluacionCuantitativaAsesorNoEncontradaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarObservacionesItemAsesorValidatorImplTest {

    private final ConsultarObservacionesItemAsesorValidatorImpl validator =
            new ConsultarObservacionesItemAsesorValidatorImpl();

    @Test
    void noDebeLanzar_cuandoLaEvaluacionExisteYElSolicitanteEsElAsesorAsignado() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var acceso = AccesoEvaluacionCuantitativaAsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(), asesor);

        // Act & Assert
        assertThatCode(() -> validator.validar(UtilUUID.generarNuevoUUID(), acceso, asesor))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_antesQueLaPropiedad_cuandoLaEvaluacionNoExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                evaluacion, AccesoEvaluacionCuantitativaAsesorDomain.VACIO, solicitante))
                .isInstanceOf(EvaluacionCuantitativaAsesorNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoPertenece_cuandoElSolicitanteNoEsElAsesorAsignado() {
        // Arrange
        var acceso = AccesoEvaluacionCuantitativaAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UtilUUID.generarNuevoUUID(), acceso, solicitante))
                .isInstanceOf(EvaluacionAsesorNoPerteneceAsesorException.class);
    }
}
