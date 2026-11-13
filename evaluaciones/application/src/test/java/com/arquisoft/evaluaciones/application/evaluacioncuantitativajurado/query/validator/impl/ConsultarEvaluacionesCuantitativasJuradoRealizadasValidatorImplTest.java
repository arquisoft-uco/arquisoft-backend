package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.validator.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoPerteneceJuradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEvaluacionesCuantitativasJuradoRealizadasValidatorImplTest {

    private final ConsultarEvaluacionesCuantitativasJuradoRealizadasValidatorImpl validator =
            new ConsultarEvaluacionesCuantitativasJuradoRealizadasValidatorImpl();

    @Test
    void debePasar_cuandoExisteYEsPropietario() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(UtilUUID.generarNuevoUUID(), true, jurado, jurado))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoEvaluacionNoExiste() {
        // Arrange
        var porDefecto = UtilUUID.obtenerUUIDPorDefecto();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UtilUUID.generarNuevoUUID(), false, porDefecto, porDefecto))
                .isInstanceOf(EvaluacionJuradoNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoPertenece_cuandoJuradoDiferente() {
        // Arrange
        var asignado = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UtilUUID.generarNuevoUUID(), true, asignado, solicitante))
                .isInstanceOf(EvaluacionJuradoNoPerteneceJuradoException.class);
    }

    @Test
    void debeDarPrioridadAExistencia_cuandoNoExisteYJuradoDiferente() {
        // Arrange
        var porDefecto = UtilUUID.obtenerUUIDPorDefecto();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UtilUUID.generarNuevoUUID(), false, porDefecto, solicitante))
                .isInstanceOf(EvaluacionJuradoNoEncontradaException.class);
    }
}
