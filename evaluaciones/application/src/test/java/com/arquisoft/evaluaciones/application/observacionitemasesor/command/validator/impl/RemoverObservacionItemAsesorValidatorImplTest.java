package com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoPerteneceAsesorException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.RemocionObservacionItemAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.ObservacionItemAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.RemocionObservacionItemAsesorFinalizadaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverObservacionItemAsesorValidatorImplTest {

    private final RemoverObservacionItemAsesorValidatorImpl validator =
            new RemoverObservacionItemAsesorValidatorImpl();

    private final UUID asesor = UtilUUID.generarNuevoUUID();
    private final RemocionObservacionItemAsesorDomain remocion = RemocionObservacionItemAsesorDomain.crear(
            UtilUUID.generarNuevoUUID(), asesor);

    @Test
    void noDebeLanzar_cuandoObservacionDelAsesorYEvaluacionEnCurso() {
        // Arrange
        var contexto = contexto(asesor, EstadoEvaluacion.EN_PROGRESO);

        // Act & Assert
        assertThatCode(() -> validator.validar(remocion, contexto)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoContextoVacio() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(remocion, ContextoEvaluacionCuantitativaAsesorDomain.VACIO))
                .isInstanceOf(ObservacionItemAsesorNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoPerteneceAsesor_cuandoAsesorDistinto() {
        // Arrange
        var contexto = contexto(UtilUUID.generarNuevoUUID(), EstadoEvaluacion.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(remocion, contexto))
                .isInstanceOf(EvaluacionAsesorNoPerteneceAsesorException.class);
    }

    @Test
    void debeLanzarFinalizada_cuandoEvaluacionFinalizada() {
        // Arrange
        var contexto = contexto(asesor, EstadoEvaluacion.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(remocion, contexto))
                .isInstanceOf(RemocionObservacionItemAsesorFinalizadaException.class);
    }

    @Test
    void debePriorizarPropiedadSobreEstado_cuandoAsesorAjenoYFinalizada() {
        // Arrange
        var contexto = contexto(UtilUUID.generarNuevoUUID(), EstadoEvaluacion.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(remocion, contexto))
                .isInstanceOf(EvaluacionAsesorNoPerteneceAsesorException.class);
    }

    private ContextoEvaluacionCuantitativaAsesorDomain contexto(UUID asesorAsignado, EstadoEvaluacion estado) {
        return ContextoEvaluacionCuantitativaAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), asesorAsignado, estado);
    }
}
