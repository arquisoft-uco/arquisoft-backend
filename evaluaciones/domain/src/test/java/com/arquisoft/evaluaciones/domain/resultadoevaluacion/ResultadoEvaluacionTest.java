package com.arquisoft.evaluaciones.domain.resultadoevaluacion;

import com.arquisoft.evaluaciones.domain.resultadoevaluacion.exception.ResultadoEvaluacionNoEncontradoException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResultadoEvaluacionTest {

    @Test
    void debeResolverLasTresConstantesDelCatalogo_cuandoElIdEsValido() {
        // Act & Assert
        assertThat(ResultadoEvaluacion.desde("APROBADA")).isEqualTo(ResultadoEvaluacion.APROBADA);
        assertThat(ResultadoEvaluacion.desde("APROBADA_CON_OBSERVACIONES"))
                .isEqualTo(ResultadoEvaluacion.APROBADA_CON_OBSERVACIONES);
        assertThat(ResultadoEvaluacion.desde("RECHAZADA")).isEqualTo(ResultadoEvaluacion.RECHAZADA);
        assertThat(ResultadoEvaluacion.APROBADA.getNombre()).isEqualTo("Aprobada");
        assertThat(ResultadoEvaluacion.APROBADA_CON_OBSERVACIONES.getNombre()).isEqualTo("Aprobada con observaciones");
        assertThat(ResultadoEvaluacion.RECHAZADA.getId()).isEqualTo("RECHAZADA");
    }

    @Test
    void debeSerValidoSoloElCatalogo_cuandoSePreguntaPorElId() {
        // Act & Assert
        assertThat(ResultadoEvaluacion.esValido("RECHAZADA")).isTrue();
        assertThat(ResultadoEvaluacion.esValido("INEXISTENTE")).isFalse();
        assertThat(ResultadoEvaluacion.esValido("VACIO")).isFalse();
    }

    @Test
    void debeLanzarExcepcion_cuandoElIdNoPerteneceAlCatalogo() {
        // Act & Assert
        assertThatThrownBy(() -> ResultadoEvaluacion.desde("INEXISTENTE"))
                .isInstanceOfSatisfying(ResultadoEvaluacionNoEncontradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ResultadoEvaluacion.NO_ENCONTRADO));
    }
}
