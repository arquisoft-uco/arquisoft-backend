package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator.impl.CambiarPuntajeEvaluacionCuantitativaJuradoValidatorImpl;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.CambioPuntajeEvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionCuantitativaJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionCuantitativaJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.PuntajeEvaluacionCuantitativaJuradoExcedeValorItemException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.EstadoEvaluacionJuradoDomain;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarPuntajeEvaluacionCuantitativaJuradoValidatorImplTest {

    private static final UUID JURADO = UUID.randomUUID();

    private final CambiarPuntajeEvaluacionCuantitativaJuradoValidatorImpl validator =
            new CambiarPuntajeEvaluacionCuantitativaJuradoValidatorImpl();

    @Test
    void debeValidarSinErrores_cuandoTodoCumple() {
        // Arrange
        var cambio = cambioValido(300);
        var estado = estado(JURADO, EstadoEvaluacion.PENDIENTE);

        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, evaluacionExistente(), estado, itemConValor(500)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoEvaluacionEsVacia() {
        // Arrange
        var cambio = cambioValido(300);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio,
                EvaluacionCuantitativaJuradoDomain.VACIO,
                EstadoEvaluacionJuradoDomain.VACIO,
                ItemCuantitativoJuradoDomain.VACIO))
                .isInstanceOf(EvaluacionCuantitativaJuradoNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoPerteneceJurado_cuandoElJuradoAsignadoEsOtro() {
        // Arrange
        var cambio = cambioValido(300);
        var estado = estado(UUID.randomUUID(), EstadoEvaluacion.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, evaluacionExistente(), estado, itemConValor(500)))
                .isInstanceOf(EvaluacionCuantitativaJuradoNoPerteneceJuradoException.class);
    }

    @Test
    void debeLanzarEvaluacionFinalizada_cuandoPerteneceYEvaluacionFinalizada() {
        // Arrange
        var cambio = cambioValido(300);
        var estado = estado(JURADO, EstadoEvaluacion.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, evaluacionExistente(), estado, itemConValor(500)))
                .isInstanceOf(EvaluacionJuradoFinalizadaException.class);
    }

    @Test
    void debeLanzarPuntajeExcedeValorItem_cuandoUltimaReglaFalla() {
        // Arrange
        var cambio = cambioValido(300);
        var estado = estado(JURADO, EstadoEvaluacion.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, evaluacionExistente(), estado, itemConValor(200)))
                .isInstanceOf(PuntajeEvaluacionCuantitativaJuradoExcedeValorItemException.class);
    }

    private static CambioPuntajeEvaluacionCuantitativaJuradoDomain cambioValido(int nuevoPuntaje) {
        return CambioPuntajeEvaluacionCuantitativaJuradoDomain.crear(UUID.randomUUID(), JURADO, nuevoPuntaje);
    }

    private static EvaluacionCuantitativaJuradoDomain evaluacionExistente() {
        return EvaluacionCuantitativaJuradoDomain.reconstruir(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 250);
    }

    private static EstadoEvaluacionJuradoDomain estado(UUID juradoAsignado, EstadoEvaluacion estado) {
        return EstadoEvaluacionJuradoDomain.reconstruir(UUID.randomUUID(), juradoAsignado, estado);
    }

    private static ItemCuantitativoJuradoDomain itemConValor(int valor) {
        return ItemCuantitativoJuradoDomain.reconstruir(
                UUID.randomUUID(), "Rigor", "Descripción", UUID.randomUUID(), valor);
    }
}
