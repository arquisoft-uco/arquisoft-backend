package com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoPerteneceAsesorException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.ModificacionObservacionItemAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.DescripcionObservacionItemAsesorDuplicadaException;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.ModificacionObservacionItemAsesorFinalizadaException;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.ObservacionItemAsesorNoEncontradaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarObservacionItemAsesorValidatorImplTest {

    private final ModificarObservacionItemAsesorValidatorImpl validator =
            new ModificarObservacionItemAsesorValidatorImpl();

    private final UUID asesor = UtilUUID.generarNuevoUUID();
    private final ModificacionObservacionItemAsesorDomain modificacion = ModificacionObservacionItemAsesorDomain.crear(
            UtilUUID.generarNuevoUUID(), asesor, "Nuevo sustento");

    @Test
    void noDebeLanzar_cuandoTodoValido() {
        // Arrange
        var contexto = contexto(asesor, EstadoEvaluacion.EN_PROGRESO);

        // Act & Assert
        assertThatCode(() -> validator.validar(modificacion, contexto, false)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoObservacionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                modificacion, ContextoEvaluacionCuantitativaAsesorDomain.VACIO, false))
                .isInstanceOf(ObservacionItemAsesorNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoEvaluacionDeOtroAsesor() {
        // Arrange
        var contexto = contexto(UtilUUID.generarNuevoUUID(), EstadoEvaluacion.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, contexto, false))
                .isInstanceOf(EvaluacionAsesorNoPerteneceAsesorException.class);
    }

    @Test
    void debeLanzar_cuandoEvaluacionFinalizada() {
        // Arrange
        var contexto = contexto(asesor, EstadoEvaluacion.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, contexto, false))
                .isInstanceOf(ModificacionObservacionItemAsesorFinalizadaException.class);
    }

    @Test
    void debeLanzar_cuandoOtraObservacionTieneLaDescripcion() {
        // Arrange
        var contexto = contexto(asesor, EstadoEvaluacion.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, contexto, true))
                .isInstanceOf(DescripcionObservacionItemAsesorDuplicadaException.class);
    }

    @Test
    void debeDarPrioridadAExistenciaSobrePropiedad_cuandoAmbasFallan() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                modificacion, ContextoEvaluacionCuantitativaAsesorDomain.VACIO, true))
                .isInstanceOf(ObservacionItemAsesorNoEncontradaException.class);
    }

    @Test
    void debeDarPrioridadAPropiedadSobreFinalizada_cuandoAmbasFallan() {
        // Arrange
        var contexto = contexto(UtilUUID.generarNuevoUUID(), EstadoEvaluacion.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, contexto, true))
                .isInstanceOf(EvaluacionAsesorNoPerteneceAsesorException.class);
    }

    @Test
    void debeDarPrioridadAFinalizadaSobreUnicidad_cuandoAmbasFallan() {
        // Arrange
        var contexto = contexto(asesor, EstadoEvaluacion.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, contexto, true))
                .isInstanceOf(ModificacionObservacionItemAsesorFinalizadaException.class);
    }

    private ContextoEvaluacionCuantitativaAsesorDomain contexto(UUID asesorAsignado, EstadoEvaluacion estado) {
        return ContextoEvaluacionCuantitativaAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), asesorAsignado, estado);
    }
}
