package com.arquisoft.shared.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UtilTextoTest {

    @Test
    void debeConservarLaInicialYElDominio_cuandoElCorreoEsValido() {
        assertThat(UtilTexto.enmascararCorreo("juan.perez@uco.edu.co"))
                .isEqualTo("j***@uco.edu.co");
    }

    @Test
    void debeOcultarTodo_cuandoElTextoNoTieneArroba() {
        assertThat(UtilTexto.enmascararCorreo("sin-arroba")).isEqualTo("***");
    }

    @Test
    void debeOcultarTodo_cuandoLaParteLocalEstaVacia() {
        assertThat(UtilTexto.enmascararCorreo("@uco.edu.co")).isEqualTo("***");
    }

    @Test
    void debeDevolverVacio_cuandoElCorreoEsNuloOEnBlanco() {
        assertThat(UtilTexto.enmascararCorreo(null)).isEqualTo(UtilTexto.VACIO);
        assertThat(UtilTexto.enmascararCorreo("   ")).isEqualTo(UtilTexto.VACIO);
    }

    @Test
    void debeEnmascarar_cuandoLaParteLocalEsUnSoloCaracter() {
        assertThat(UtilTexto.enmascararCorreo("a@uco.edu.co")).isEqualTo("a***@uco.edu.co");
    }
}
