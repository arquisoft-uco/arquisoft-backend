package com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator.impl.ModificarObservacionItemJuradoValidatorImpl;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ModificacionObservacionItemJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.DescripcionObservacionItemJuradoDuplicadaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.ObservacionItemJuradoNoEncontradaException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarObservacionItemJuradoValidatorImplTest {

    private final ModificarObservacionItemJuradoValidatorImpl validator =
            new ModificarObservacionItemJuradoValidatorImpl();

    @Test
    void debeValidarSinErrores_cuandoTodoCumple() {
        // Arrange
        var modificacion = modificacionValida();
        var observacion = observacionExistente(modificacion);

        // Act & Assert
        assertThatCode(() -> validator.validar(modificacion, observacion, false, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoLaObservacionEsVacia() {
        // Arrange
        var modificacion = modificacionValida();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, ObservacionItemJuradoDomain.VACIO, false, false))
                .isInstanceOfSatisfying(ObservacionItemJuradoNoEncontradaException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ObservacionItemJurado.NO_ENCONTRADA);
                    assertThat(exception.getMessage()).contains(modificacion.getObservacionItemJurado().toString());
                });
    }

    @Test
    void debeLanzarDescripcionDuplicada_cuandoOtraObservacionYaTieneLaDescripcion() {
        // Arrange
        var modificacion = modificacionValida();
        var observacion = observacionExistente(modificacion);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, observacion, false, true))
                .isInstanceOfSatisfying(DescripcionObservacionItemJuradoDuplicadaException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ObservacionItemJurado.DESCRIPCION_DUPLICADA);
                    assertThat(exception.getMessage())
                            .contains(observacion.getEvaluacionCuantitativaJurado().toString());
                });
    }

    @Test
    void debeLanzarEvaluacionFinalizada_cuandoLaEvaluacionJuradoEstaFinalizada() {
        // Arrange
        var modificacion = modificacionValida();
        var observacion = observacionExistente(modificacion);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, observacion, true, false))
                .isInstanceOfSatisfying(EvaluacionJuradoFinalizadaException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ObservacionItemJurado.EVALUACION_JURADO_FINALIZADA);
                    assertThat(exception.getMessage())
                            .contains(observacion.getEvaluacionCuantitativaJurado().toString());
                });
    }

    @Test
    void debeLanzarNoEncontrada_cuandoFallanExistenciaUnicidadYFinalizada() {
        // Arrange
        var modificacion = modificacionValida();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, ObservacionItemJuradoDomain.VACIO, true, true))
                .isInstanceOf(ObservacionItemJuradoNoEncontradaException.class);
    }

    @Test
    void debeLanzarDescripcionDuplicada_cuandoFallanUnicidadYFinalizada() {
        // Arrange
        var modificacion = modificacionValida();
        var observacion = observacionExistente(modificacion);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, observacion, true, true))
                .isInstanceOf(DescripcionObservacionItemJuradoDuplicadaException.class);
    }

    private static ModificacionObservacionItemJuradoDomain modificacionValida() {
        return ModificacionObservacionItemJuradoDomain.crear(UUID.randomUUID(), "Nueva descripción de la observación");
    }

    private static ObservacionItemJuradoDomain observacionExistente(ModificacionObservacionItemJuradoDomain modificacion) {
        return ObservacionItemJuradoDomain.reconstruir(
                modificacion.getObservacionItemJurado(), UUID.randomUUID(), "Descripción anterior");
    }
}
