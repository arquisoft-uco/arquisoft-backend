package com.arquisoft.shared.util;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class UtilFechaTest {

    @Test
    void debeGenerarElInstanteActual_cuandoSeInvocaElAccesor() {
        // Act
        Instant instante = UtilFecha.generarInstanteActual();

        // Assert
        assertThat(instante).isBetween(Instant.now().minusSeconds(1), Instant.now().plusSeconds(1));
    }

    @Test
    void debeExponerElCentinela_cuandoSeConsultaVacio() {
        // Act & Assert
        assertThat(UtilFecha.VACIO).isEqualTo(Instant.EPOCH);
    }

    @Test
    void debeValidarElFormatoDeFecha_cuandoElTextoCoincideConElPatron() {
        // Act & Assert
        assertThat(UtilFecha.fechaValida("2026-08-27")).isTrue();
        assertThat(UtilFecha.fechaValida("27/08/2026")).isFalse();
        assertThat(UtilFecha.fechaValida(null)).isFalse();
    }

    @Test
    void debeParsearLaFecha_cuandoElTextoEsValido() {
        // Act & Assert
        assertThat(UtilFecha.parsearFechaDesdeTexto("2026-08-27"))
                .isEqualTo(java.time.LocalDate.of(2026, 8, 27));
        assertThat(UtilFecha.parsearFechaDesdeTexto("no-fecha")).isNull();
    }
}
