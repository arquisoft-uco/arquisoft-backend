package com.arquisoft.fichas.domain.estadoevaluacion;

import com.arquisoft.fichas.domain.estadoevaluacion.exception.EstadoEvaluacionNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @ParameterizedTest
    @CsvSource({
            "EN_EVALUACION,              false, false, false",
            "APROBADA,                   true,  true,  false",
            "APROBADA_CON_OBSERVACIONES, true,  true,  false",
            "NO_APROBADA,                true,  false, false",
            "DESCARTADA,                 false, false, true",
            "VACIO,                      false, false, false"
    })
    void debeClasificarElEstado_cuandoSeConsultaSiEsFinalizadaAprobatoriaODescartada(
            EstadoEvaluacion estado, boolean finalizada, boolean aprobatoria, boolean descartada) {
        // Act
        var esFinalizada = estado.esFinalizada();
        var esAprobatoria = estado.esAprobatoria();
        var esDescartada = estado.esDescartada();

        // Assert
        assertThat(esFinalizada).isEqualTo(finalizada);
        assertThat(esAprobatoria).isEqualTo(aprobatoria);
        assertThat(esDescartada).isEqualTo(descartada);
    }
}
