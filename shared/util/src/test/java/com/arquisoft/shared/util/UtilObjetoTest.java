package com.arquisoft.shared.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UtilObjetoTest {

    @Test
    void debeIndicarNulo_cuandoElValorEsNulo() {
        assertThat(UtilObjeto.esNulo(null)).isTrue();
        assertThat(UtilObjeto.esNulo("x")).isFalse();
    }

    @Test
    void debeIndicarNoNulo_cuandoElValorNoEsNulo() {
        assertThat(UtilObjeto.noEsNulo("x")).isTrue();
        assertThat(UtilObjeto.noEsNulo(null)).isFalse();
    }

    @Test
    void debeDevolverElValor_cuandoNoEsNulo() {
        assertThat(UtilObjeto.aplicarPorDefecto("valor", "defecto")).isEqualTo("valor");
    }

    @Test
    void debeDevolverElPorDefecto_cuandoElValorEsNulo() {
        assertThat(UtilObjeto.aplicarPorDefecto(null, "defecto")).isEqualTo("defecto");
    }

    @Test
    void debeConservarLaIdentidad_cuandoElValorNoEsNulo() {
        Object valor = new Object();
        Object porDefecto = new Object();

        assertThat(UtilObjeto.aplicarPorDefecto(valor, porDefecto)).isSameAs(valor);
    }
}
