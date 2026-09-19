package com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator.impl.RegistrarObservacionItemJuradoValidatorImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.DescripcionObservacionItemJuradoDuplicadaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionCuantitativaJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionJuradoFinalizadaException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarObservacionItemJuradoValidatorImplTest {

    private final RegistrarObservacionItemJuradoValidatorImpl validator =
            new RegistrarObservacionItemJuradoValidatorImpl();

    @Test
    void debeValidarSinErrores_cuandoTodoCumple() {
        // Arrange
        var observacion = observacionValida();
        var evaluacion = evaluacionExistente(observacion.getEvaluacionCuantitativaJurado());

        // Act & Assert
        assertThatCode(() -> validator.validar(observacion, evaluacion, false, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoEvaluacionEsVacia() {
        // Arrange
        var observacion = observacionValida();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                observacion, EvaluacionCuantitativaJuradoDomain.VACIO, false, false))
                .isInstanceOf(EvaluacionCuantitativaJuradoNoEncontradaException.class);
    }

    @Test
    void debeLanzarEvaluacionFinalizada_cuandoLaEvaluacionExisteYEstaFinalizada() {
        // Arrange
        var observacion = observacionValida();
        var evaluacion = evaluacionExistente(observacion.getEvaluacionCuantitativaJurado());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(observacion, evaluacion, true, false))
                .isInstanceOf(EvaluacionJuradoFinalizadaException.class);
    }

    @Test
    void debeLanzarDescripcionDuplicada_cuandoUltimaReglaFalla() {
        // Arrange
        var observacion = observacionValida();
        var evaluacion = evaluacionExistente(observacion.getEvaluacionCuantitativaJurado());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(observacion, evaluacion, false, true))
                .isInstanceOf(DescripcionObservacionItemJuradoDuplicadaException.class);
    }

    private static ObservacionItemJuradoDomain observacionValida() {
        return ObservacionItemJuradoDomain.crear(UUID.randomUUID(), "Sustenta el puntaje otorgado");
    }

    private static EvaluacionCuantitativaJuradoDomain evaluacionExistente(UUID evaluacionCuantitativaJurado) {
        return EvaluacionCuantitativaJuradoDomain.reconstruir(
                evaluacionCuantitativaJurado, UUID.randomUUID(), UUID.randomUUID(), 300);
    }
}
