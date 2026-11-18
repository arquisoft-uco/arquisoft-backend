package com.arquisoft.evaluaciones.application.observacionitemasesor.query.validator.impl;

import com.arquisoft.evaluaciones.domain.evaluacionasesor.exception.EvaluacionAsesorNoPerteneceEstudianteException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.AccesoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.EvaluacionCuantitativaAsesorNoEncontradaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarObservacionesItemAsesorEstudianteValidatorImplTest {

    private final ConsultarObservacionesItemAsesorEstudianteValidatorImpl validator =
            new ConsultarObservacionesItemAsesorEstudianteValidatorImpl();

    private static AccesoEvaluacionCuantitativaAsesorDomain acceso() {
        return AccesoEvaluacionCuantitativaAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
    }

    @Test
    void noDebeLanzar_cuandoLaEvaluacionExisteYElEstudianteEstaVinculado() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var vinculados = List.of(UtilUUID.generarNuevoUUID(), estudiante);

        // Act & Assert
        assertThatCode(() -> validator.validar(UtilUUID.generarNuevoUUID(), acceso(), vinculados, estudiante))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_antesQueLaPropiedad_cuandoLaEvaluacionNoExisteYNoHayVinculo() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UtilUUID.generarNuevoUUID(),
                AccesoEvaluacionCuantitativaAsesorDomain.VACIO, List.of(), UtilUUID.generarNuevoUUID()))
                .isInstanceOf(EvaluacionCuantitativaAsesorNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoPertenece_cuandoElEstudianteNoEstaEntreLosVinculados() {
        // Arrange
        var vinculados = List.of(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                UtilUUID.generarNuevoUUID(), acceso(), vinculados, UtilUUID.generarNuevoUUID()))
                .isInstanceOf(EvaluacionAsesorNoPerteneceEstudianteException.class);
        assertThatThrownBy(() -> validator.validar(
                UtilUUID.generarNuevoUUID(), acceso(), List.of(), UtilUUID.generarNuevoUUID()))
                .isInstanceOf(EvaluacionAsesorNoPerteneceEstudianteException.class);
    }
}
