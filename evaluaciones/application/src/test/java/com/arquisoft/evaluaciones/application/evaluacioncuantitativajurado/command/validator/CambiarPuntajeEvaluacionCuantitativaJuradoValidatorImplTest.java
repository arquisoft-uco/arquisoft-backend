package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator.impl.CambiarPuntajeEvaluacionCuantitativaJuradoValidatorImpl;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.CambioPuntajeEvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionCuantitativaJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionCuantitativaJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.PuntajeEvaluacionCuantitativaJuradoExcedeValorItemException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarPuntajeEvaluacionCuantitativaJuradoValidatorImplTest {

    private final CambiarPuntajeEvaluacionCuantitativaJuradoValidatorImpl validator =
            new CambiarPuntajeEvaluacionCuantitativaJuradoValidatorImpl();

    @Test
    void debeValidarSinErrores_cuandoTodoCumple() {
        // Arrange
        var cambio = cambioValido(300);
        var evaluacion = evaluacionExistente();
        var estado = new EstadoEvaluacionJuradoEntity(true, false);

        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, evaluacion, estado, 500))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoEvaluacionEsVacia() {
        // Arrange
        var cambio = cambioValido(300);
        var estado = new EstadoEvaluacionJuradoEntity(true, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, EvaluacionCuantitativaJuradoDomain.VACIO, estado, 500))
                .isInstanceOf(EvaluacionCuantitativaJuradoNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoPerteneceJurado_cuandoNoPerteneceAunConEvaluacionExistente() {
        // Arrange
        var cambio = cambioValido(300);
        var evaluacion = evaluacionExistente();
        var estado = new EstadoEvaluacionJuradoEntity(false, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, evaluacion, estado, 500))
                .isInstanceOf(EvaluacionCuantitativaJuradoNoPerteneceJuradoException.class);
    }

    @Test
    void debeLanzarEvaluacionFinalizada_cuandoPerteneceYEvaluacionFinalizada() {
        // Arrange
        var cambio = cambioValido(300);
        var evaluacion = evaluacionExistente();
        var estado = new EstadoEvaluacionJuradoEntity(true, true);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, evaluacion, estado, 500))
                .isInstanceOf(EvaluacionJuradoFinalizadaException.class);
    }

    @Test
    void debeLanzarPuntajeExcedeValorItem_cuandoUltimaReglaFalla() {
        // Arrange
        var cambio = cambioValido(300);
        var evaluacion = evaluacionExistente();
        var estado = new EstadoEvaluacionJuradoEntity(true, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, evaluacion, estado, 200))
                .isInstanceOf(PuntajeEvaluacionCuantitativaJuradoExcedeValorItemException.class);
    }

    private static CambioPuntajeEvaluacionCuantitativaJuradoDomain cambioValido(int nuevoPuntaje) {
        return CambioPuntajeEvaluacionCuantitativaJuradoDomain.crear(
                UUID.randomUUID(), UUID.randomUUID(), nuevoPuntaje);
    }

    private static EvaluacionCuantitativaJuradoDomain evaluacionExistente() {
        return EvaluacionCuantitativaJuradoDomain.reconstruir(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 250);
    }
}
