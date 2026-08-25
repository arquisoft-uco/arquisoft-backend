package com.arquisoft.fichas.domain.estadoficha;

import com.arquisoft.fichas.domain.estadoficha.exception.EstadoFichaNoEncontradoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoFichaTest {

    @Test
    void debeRetornarElEstado_cuandoElIdCoincideConElCatalogo() {
        assertThat(EstadoFicha.desde(EstadoFicha.APROBADA.name())).isEqualTo(EstadoFicha.APROBADA);
    }

    @Test
    void debeLanzarExcepcion_cuandoElIdEsNulo() {
        assertThatThrownBy(() -> EstadoFicha.desde(null))
                .isInstanceOf(EstadoFichaNoEncontradoException.class);
    }

    @Test
    void debeLanzarExcepcion_cuandoElIdEstaEnBlanco() {
        assertThatThrownBy(() -> EstadoFicha.desde(""))
                .isInstanceOf(EstadoFichaNoEncontradoException.class);
    }

    @Test
    void debeLanzarExcepcion_cuandoElIdNoCoincideConElCatalogo() {
        assertThatThrownBy(() -> EstadoFicha.desde("ESTADO_INEXISTENTE"))
                .isInstanceOf(EstadoFichaNoEncontradoException.class);
    }

    @Test
    void debeRechazarVacio_cuandoLlegaComoTextoDesdeLaBaseDeDatos() {
        assertThatThrownBy(() -> EstadoFicha.desde("VACIO"))
                .isInstanceOf(EstadoFichaNoEncontradoException.class);
    }

    @Test
    void debeSerNoTerminal_cuandoEsElValorCero() {
        assertThat(EstadoFicha.VACIO.esTerminal()).isFalse();
        assertThat(EstadoFicha.VACIO.getNombre()).isEmpty();
    }

    @Test
    void debeReportarValidez_sinLanzar_cuandoSeConsultaConEsValido() {
        assertThat(EstadoFicha.esValido("APROBADA")).isTrue();
        assertThat(EstadoFicha.esValido("NO_EXISTE")).isFalse();
        assertThat(EstadoFicha.esValido("VACIO")).isFalse();
        assertThat(EstadoFicha.esValido(null)).isFalse();
    }
}
