package com.arquisoft.fichas.domain.estadoevaluacion;

import com.arquisoft.fichas.domain.estadoevaluacion.exception.EstadoEvaluacionNoEncontradoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoEvaluacionTest {

    @Test
    void debeRetornarElEstado_cuandoElIdCoincideConElCatalogo() {
        assertThat(EstadoEvaluacion.desde("APROBADA")).isEqualTo(EstadoEvaluacion.APROBADA);
    }

    @Test
    void debeLanzarExcepcion_cuandoElIdEsNuloEnBlancoODesconocido() {
        assertThatThrownBy(() -> EstadoEvaluacion.desde(null))
                .isInstanceOf(EstadoEvaluacionNoEncontradoException.class);
        assertThatThrownBy(() -> EstadoEvaluacion.desde(""))
                .isInstanceOf(EstadoEvaluacionNoEncontradoException.class);
        assertThatThrownBy(() -> EstadoEvaluacion.desde("NO_EXISTE"))
                .isInstanceOf(EstadoEvaluacionNoEncontradoException.class);
    }

    @Test
    void debeReportarValidez_sinLanzar_cuandoSeConsultaConEsValido() {
        assertThat(EstadoEvaluacion.esValido("DESCARTADA")).isTrue();
        assertThat(EstadoEvaluacion.esValido("NO_EXISTE")).isFalse();
        assertThat(EstadoEvaluacion.esValido(null)).isFalse();
    }
}
