package com.arquisoft.shared.tracing.domain.traza.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelacionEntranteTest {

    @Test
    void debeAceptarCorrelacion_cuandoEsAlfanumericaConGuiones() {
        // Assert
        assertThat(CorrelacionEntrante.validar("abc-123")).contains("abc-123");
        assertThat(CorrelacionEntrante.validar("a".repeat(64))).isPresent();
    }

    @Test
    void debeDevolverVacio_cuandoLaCorrelacionEstaAusente() {
        // Assert
        assertThat(CorrelacionEntrante.validar(null)).isEmpty();
        assertThat(CorrelacionEntrante.validar("")).isEmpty();
        assertThat(CorrelacionEntrante.validar("   ")).isEmpty();
    }

    @Test
    void debeRechazarCorrelacion_cuandoIntentaInyectarSaltosDeLinea() {
        // Assert
        assertThat(CorrelacionEntrante.validar("abc\r\nWARN falso")).isEmpty();
        assertThat(CorrelacionEntrante.validar("abc\ndef")).isEmpty();
        assertThat(CorrelacionEntrante.validar("<script>")).isEmpty();
    }

    @Test
    void debeRechazarCorrelacion_cuandoExcedeSesentaYCuatroCaracteres() {
        // Assert
        assertThat(CorrelacionEntrante.validar("a".repeat(65))).isEmpty();
    }
}
