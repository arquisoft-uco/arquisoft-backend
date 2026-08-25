package com.arquisoft.shared.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UtilEnumTest {

    private enum Ejemplo { UNO, DOS }

    @Test
    void debeConvertir_cuandoElIdCoincideConUnaConstante() {
        assertThat(UtilEnum.desde(Ejemplo.class, "UNO")).contains(Ejemplo.UNO);
    }

    @Test
    void debeConvertir_cuandoElIdTraeEspaciosAlrededor() {
        assertThat(UtilEnum.desde(Ejemplo.class, "  DOS  ")).contains(Ejemplo.DOS);
    }

    @Test
    void debeRetornarVacio_cuandoElIdEsNuloOEnBlanco() {
        assertThat(UtilEnum.desde(Ejemplo.class, null)).isEmpty();
        assertThat(UtilEnum.desde(Ejemplo.class, "   ")).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoElIdNoCoincideConNingunaConstante() {
        assertThat(UtilEnum.desde(Ejemplo.class, "TRES")).isEmpty();
    }

    @Test
    void debeDistinguirMayusculas_cuandoElIdCambiaDeCaja() {
        assertThat(UtilEnum.desde(Ejemplo.class, "uno")).isEmpty();
    }

    @Test
    void debeReportarValidez_cuandoSeConsultaConEsValido() {
        assertThat(UtilEnum.esValido(Ejemplo.class, "UNO")).isTrue();
        assertThat(UtilEnum.esValido(Ejemplo.class, "TRES")).isFalse();
    }
}
