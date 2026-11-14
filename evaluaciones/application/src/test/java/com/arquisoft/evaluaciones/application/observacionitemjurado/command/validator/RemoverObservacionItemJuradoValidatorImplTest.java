package com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator.impl.RemoverObservacionItemJuradoValidatorImpl;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.RemocionObservacionItemJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.ObservacionItemJuradoNoEncontradaException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverObservacionItemJuradoValidatorImplTest {

    private final RemoverObservacionItemJuradoValidatorImpl validator =
            new RemoverObservacionItemJuradoValidatorImpl();

    @Test
    void debeValidarSinErrores_cuandoExisteYNoEstaFinalizada() {
        // Arrange
        var remocion = RemocionObservacionItemJuradoDomain.crear(UtilUUID.generarNuevoUUID());
        var observacion = observacionExistente(remocion);

        // Act & Assert
        assertThatCode(() -> validator.validar(remocion, observacion, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoLaObservacionEsVacia() {
        // Arrange
        var remocion = RemocionObservacionItemJuradoDomain.crear(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(remocion, ObservacionItemJuradoDomain.VACIO, false))
                .isInstanceOfSatisfying(ObservacionItemJuradoNoEncontradaException.class, exception ->
                        assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ObservacionItemJurado.NO_ENCONTRADA));
    }

    @Test
    void debeLanzarEvaluacionFinalizada_cuandoLaEvaluacionJuradoEstaFinalizada() {
        // Arrange
        var remocion = RemocionObservacionItemJuradoDomain.crear(UtilUUID.generarNuevoUUID());
        var observacion = observacionExistente(remocion);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(remocion, observacion, true))
                .isInstanceOfSatisfying(EvaluacionJuradoFinalizadaException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ObservacionItemJurado.EVALUACION_JURADO_FINALIZADA);
                    assertThat(exception.getMessage())
                            .contains(observacion.getEvaluacionCuantitativaJurado().toString());
                });
    }

    @Test
    void debeLanzarNoEncontrada_cuandoFallanExistenciaYFinalizada() {
        // Arrange
        var remocion = RemocionObservacionItemJuradoDomain.crear(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(remocion, ObservacionItemJuradoDomain.VACIO, true))
                .isInstanceOf(ObservacionItemJuradoNoEncontradaException.class);
    }

    private static ObservacionItemJuradoDomain observacionExistente(RemocionObservacionItemJuradoDomain remocion) {
        return ObservacionItemJuradoDomain.reconstruir(
                remocion.getObservacionItemJurado(), UtilUUID.generarNuevoUUID(), "Sustenta el puntaje otorgado");
    }
}
