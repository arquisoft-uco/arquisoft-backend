package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.validator.impl;

import com.arquisoft.evaluaciones.domain.evaluacionasesor.exception.EvaluacionAsesorNoPerteneceEstudianteException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoEncontradaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEvaluacionesCualitativasAsesorEstudianteValidatorImplTest {

    private final ConsultarEvaluacionesCualitativasAsesorEstudianteValidatorImpl validator =
            new ConsultarEvaluacionesCualitativasAsesorEstudianteValidatorImpl();

    @Test
    void debePasar_cuandoExisteYEstaVinculado() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var vinculados = List.of(UtilUUID.generarNuevoUUID(), estudiante);

        // Act & Assert
        assertThatCode(() -> validator.validar(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), vinculados, estudiante))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoNoExiste() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                UtilUUID.generarNuevoUUID(), UtilUUID.obtenerUUIDPorDefecto(), List.of(estudiante), estudiante))
                .isInstanceOf(EvaluacionAsesorNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoPertenece_cuandoNoVinculado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                List.of(UtilUUID.generarNuevoUUID()), UtilUUID.generarNuevoUUID()))
                .isInstanceOf(EvaluacionAsesorNoPerteneceEstudianteException.class);
    }

    @Test
    void debeLanzarNoEncontrada_cuandoNoExisteYNoVinculado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                UtilUUID.generarNuevoUUID(), UtilUUID.obtenerUUIDPorDefecto(), List.of(),
                UtilUUID.generarNuevoUUID()))
                .isInstanceOf(EvaluacionAsesorNoEncontradaException.class);
    }
}
