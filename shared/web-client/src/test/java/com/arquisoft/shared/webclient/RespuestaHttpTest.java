package com.arquisoft.shared.webclient;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RespuestaHttpTest {

    @Test
    void debeSerExitosa_soloEnElRango2xx() {
        // Act
        var antes = new RespuestaHttp<String>(199, null).esExitosa();
        var inicio = new RespuestaHttp<String>(200, null).esExitosa();
        var fin = new RespuestaHttp<String>(299, null).esExitosa();
        var despues = new RespuestaHttp<String>(300, null).esExitosa();

        // Assert
        assertThat(antes).isFalse();
        assertThat(inicio).isTrue();
        assertThat(fin).isTrue();
        assertThat(despues).isFalse();
    }
}
