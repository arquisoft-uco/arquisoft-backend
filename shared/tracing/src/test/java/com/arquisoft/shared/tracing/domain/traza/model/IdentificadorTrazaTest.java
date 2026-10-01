package com.arquisoft.shared.tracing.domain.traza.model;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class IdentificadorTrazaTest {

    @Test
    void debeGenerarCorrelacionDe32Hex_cuandoSeSolicitaUnaNueva() {
        // Act
        String correlacion = IdentificadorTraza.nuevaCorrelacion();

        // Assert
        assertThat(correlacion).hasSize(32).matches("[0-9a-f]{32}");
    }

    @Test
    void debeGenerarTransaccionDe16Hex_cuandoSeSolicitaUnaNueva() {
        // Act
        String transaccion = IdentificadorTraza.nuevaTransaccion();

        // Assert
        assertThat(transaccion).hasSize(16).matches("[0-9a-f]{16}");
    }

    @Test
    void debeGenerarIdentificadoresDistintos_cuandoSeInvocaRepetidamente() {
        // Act
        var correlaciones = IntStream.range(0, 500)
                .mapToObj(i -> IdentificadorTraza.nuevaCorrelacion())
                .distinct()
                .count();

        // Assert
        assertThat(correlaciones).isEqualTo(500);
    }

    @Test
    void debeAceptarFormaW3C_cuandoSonTreintaYDosHexNoNulos() {
        // Assert
        assertThat(IdentificadorTraza.esFormaW3C("4bf92f3577b34da6a3ce929d0e0e4736")).isTrue();
    }

    @Test
    void debeRechazarFormaW3C_cuandoElValorNoCumple() {
        // Assert
        assertThat(IdentificadorTraza.esFormaW3C(null)).isFalse();
        assertThat(IdentificadorTraza.esFormaW3C("abc-123")).isFalse();
        assertThat(IdentificadorTraza.esFormaW3C("4BF92F3577B34DA6A3CE929D0E0E4736")).isFalse();
        assertThat(IdentificadorTraza.esFormaW3C("4bf92f3577b34da6a3ce929d0e0e47")).isFalse();
        assertThat(IdentificadorTraza.esFormaW3C("0".repeat(32))).isFalse();
    }
}
