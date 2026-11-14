package com.arquisoft.evaluaciones.application.evaluacionjurado.command.validator;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.validator.impl.RemoverJuradosEvaluacionValidatorImpl;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionFinalizadaAsignacionException;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoAsignacionEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosConEvaluacionesException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosNoAsignadosException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.EvaluacionesRegistradasJurados;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.JuradosAsignadosEvaluacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverJuradosEvaluacionValidatorTest {

    private final RemoverJuradosEvaluacionValidator validator = new RemoverJuradosEvaluacionValidatorImpl();

    private final UUID evaluacion = UtilUUID.generarNuevoUUID();
    private final UUID jurado = UtilUUID.generarNuevoUUID();

    @Test
    void debePermitirFlujo_cuandoTodoEsValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(
                new ExistenciaEvaluacion(evaluacion, true),
                new EstadoAsignacionEvaluacion(EstadoEvaluacion.EN_PROGRESO),
                new JuradosAsignadosEvaluacion(Set.of(jurado), Set.of(jurado)),
                new EvaluacionesRegistradasJurados(Set.of())))
                .doesNotThrowAnyException();
    }

    @Test
    void debeGanarEvaluacionInexistente_cuandoTambienFallanLasDemasReglas() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacion(evaluacion, false),
                new EstadoAsignacionEvaluacion(EstadoEvaluacion.FINALIZADA),
                new JuradosAsignadosEvaluacion(Set.of(jurado), Set.of()),
                new EvaluacionesRegistradasJurados(Set.of(jurado))))
                .isInstanceOf(EvaluacionNoEncontradaException.class);
    }

    @Test
    void debeGanarEvaluacionFinalizada_cuandoTambienFallanLasReglasDeJurados() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacion(evaluacion, true),
                new EstadoAsignacionEvaluacion(EstadoEvaluacion.FINALIZADA),
                new JuradosAsignadosEvaluacion(Set.of(jurado), Set.of()),
                new EvaluacionesRegistradasJurados(Set.of(jurado))))
                .isInstanceOf(EvaluacionFinalizadaAsignacionException.class);
    }

    @Test
    void debeGanarJuradosNoAsignados_cuandoTambienTienenEvaluaciones() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacion(evaluacion, true),
                new EstadoAsignacionEvaluacion(EstadoEvaluacion.PENDIENTE),
                new JuradosAsignadosEvaluacion(Set.of(jurado), Set.of()),
                new EvaluacionesRegistradasJurados(Set.of(jurado))))
                .isInstanceOf(JuradosNoAsignadosException.class);
    }

    @Test
    void debeLanzarJuradosConEvaluaciones_cuandoSoloFallaEsaRegla() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacion(evaluacion, true),
                new EstadoAsignacionEvaluacion(EstadoEvaluacion.PENDIENTE),
                new JuradosAsignadosEvaluacion(Set.of(jurado), Set.of(jurado)),
                new EvaluacionesRegistradasJurados(Set.of(jurado))))
                .isInstanceOf(JuradosConEvaluacionesException.class);
    }
}
