package com.arquisoft.evaluaciones.application.evaluacionjurado.command.validator;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.validator.impl.AsignarJuradosEvaluacionValidatorImpl;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoRegistroEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosYaAsignadosException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.DisponibilidadJuradosEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.ExistenciaJurados;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarJuradosEvaluacionValidatorTest {

    private final AsignarJuradosEvaluacionValidator validator = new AsignarJuradosEvaluacionValidatorImpl();

    private final UUID evaluacion = UtilUUID.generarNuevoUUID();
    private final UUID jurado = UtilUUID.generarNuevoUUID();

    @Test
    void debePermitirFlujo_cuandoTodoEsValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(
                new ExistenciaEvaluacion(evaluacion, true),
                new EstadoRegistroEvaluacion(EstadoEvaluacion.PENDIENTE),
                new ExistenciaJurados(Set.of(jurado), Set.of(jurado)),
                new DisponibilidadJuradosEvaluacion(evaluacion, Set.of(jurado), Set.of())))
                .doesNotThrowAnyException();
    }

    @Test
    void debeGanarEvaluacionInexistente_cuandoTambienFallanLasDemasReglas() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacion(evaluacion, false),
                new EstadoRegistroEvaluacion(EstadoEvaluacion.FINALIZADA),
                new ExistenciaJurados(Set.of(jurado), Set.of()),
                new DisponibilidadJuradosEvaluacion(evaluacion, Set.of(jurado), Set.of(jurado))))
                .isInstanceOf(EvaluacionNoEncontradaException.class);
    }

    @Test
    void debeGanarEvaluacionFinalizada_cuandoTambienFallanLasReglasDeJurados() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacion(evaluacion, true),
                new EstadoRegistroEvaluacion(EstadoEvaluacion.FINALIZADA),
                new ExistenciaJurados(Set.of(jurado), Set.of()),
                new DisponibilidadJuradosEvaluacion(evaluacion, Set.of(jurado), Set.of(jurado))))
                .isInstanceOf(EvaluacionFinalizadaException.class);
    }

    @Test
    void debeGanarJuradosFaltantes_cuandoTambienEstanYaAsignados() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacion(evaluacion, true),
                new EstadoRegistroEvaluacion(EstadoEvaluacion.EN_PROGRESO),
                new ExistenciaJurados(Set.of(jurado), Set.of()),
                new DisponibilidadJuradosEvaluacion(evaluacion, Set.of(jurado), Set.of(jurado))))
                .isInstanceOf(JuradosNoEncontradosException.class);
    }

    @Test
    void debeLanzarJuradosYaAsignados_cuandoSoloFallaLaUnicidad() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacion(evaluacion, true),
                new EstadoRegistroEvaluacion(EstadoEvaluacion.EN_PROGRESO),
                new ExistenciaJurados(Set.of(jurado), Set.of(jurado)),
                new DisponibilidadJuradosEvaluacion(evaluacion, Set.of(jurado), Set.of(jurado))))
                .isInstanceOf(JuradosYaAsignadosException.class);
    }
}
