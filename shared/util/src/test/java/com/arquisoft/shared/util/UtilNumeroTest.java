package com.arquisoft.shared.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

class UtilNumeroTest {

    @Test
    void debeReconocerCero_cuandoLlegaEnCualquierTipoNumerico() {
        assertThat(UtilNumero.esCero(0)).isTrue();
        assertThat(UtilNumero.esCero(0L)).isTrue();
        assertThat(UtilNumero.esCero((short) 0)).isTrue();
        assertThat(UtilNumero.esCero(0.0)).isTrue();
        assertThat(UtilNumero.esCero(-0.0)).isTrue();
        assertThat(UtilNumero.esCero(0.0f)).isTrue();
        assertThat(UtilNumero.esCero(new BigDecimal("0.00"))).isTrue();
        assertThat(UtilNumero.esCero(BigInteger.ZERO)).isTrue();
        assertThat(UtilNumero.esCero(new AtomicLong())).isTrue();
    }

    @Test
    void debeTratarComoCero_cuandoEsNulo() {
        assertThat(UtilNumero.esCero(null)).isTrue();
    }

    @Test
    void debeRechazarCero_cuandoElValorNoEsCero() {
        assertThat(UtilNumero.esCero(1)).isFalse();
        assertThat(UtilNumero.esCero(-1L)).isFalse();
        assertThat(UtilNumero.esCero(0.0001)).isFalse();
        assertThat(UtilNumero.esCero(new BigDecimal("1E-400"))).isFalse();
        assertThat(UtilNumero.esCero(Double.NaN)).isFalse();
        assertThat(UtilNumero.esCero(Double.POSITIVE_INFINITY)).isFalse();
    }

    @Test
    void debeDetectarParteDecimal_cuandoElValorTieneFraccion() {
        assertThat(UtilNumero.tieneParteDecimal(1.5)).isTrue();
        assertThat(UtilNumero.tieneParteDecimal(-1.5)).isTrue();
        assertThat(UtilNumero.tieneParteDecimal(0.1f)).isTrue();
        assertThat(UtilNumero.tieneParteDecimal(new BigDecimal("3.50"))).isTrue();
    }

    @Test
    void debeRechazarParteDecimal_cuandoElValorEsEnteroONoFinito() {
        assertThat(UtilNumero.tieneParteDecimal(3)).isFalse();
        assertThat(UtilNumero.tieneParteDecimal(3.0)).isFalse();
        assertThat(UtilNumero.tieneParteDecimal(1.0E10)).isFalse();
        assertThat(UtilNumero.tieneParteDecimal(new BigDecimal("3.00"))).isFalse();
        assertThat(UtilNumero.tieneParteDecimal(Double.NaN)).isFalse();
        assertThat(UtilNumero.tieneParteDecimal(null)).isFalse();
    }

    @Test
    void debeDevolverCeroDelTipo_cuandoElNumeroEsNulo() {
        assertThat(UtilNumero.obtenerPorDefecto((Integer) null)).isZero();
        assertThat(UtilNumero.obtenerPorDefecto((Long) null)).isZero();
        assertThat(UtilNumero.obtenerPorDefecto((Double) null)).isZero();
        assertThat(UtilNumero.obtenerPorDefecto((BigDecimal) null)).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void debeConservarNumero_cuandoNoEsNulo() {
        assertThat(UtilNumero.obtenerPorDefecto(7)).isEqualTo(7);
        assertThat(UtilNumero.obtenerPorDefecto(7L)).isEqualTo(7L);
        assertThat(UtilNumero.obtenerPorDefecto(7.5)).isEqualTo(7.5);
        assertThat(UtilNumero.obtenerPorDefecto(new BigDecimal("7.5"))).isEqualTo(new BigDecimal("7.5"));
    }

    @Test
    void debeAplicarValorPorDefecto_cuandoElNumeroEsNulo() {
        assertThat(UtilNumero.obtenerPorDefecto(null, 9L)).isEqualTo(9L);
        assertThat(UtilNumero.obtenerPorDefecto(3L, 9L)).isEqualTo(3L);
    }
}
