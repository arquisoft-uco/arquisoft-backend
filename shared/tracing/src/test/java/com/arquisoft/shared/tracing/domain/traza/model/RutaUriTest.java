package com.arquisoft.shared.tracing.domain.traza.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RutaUriTest {

    @Test
    void debeDevolverLaRutaIntacta_cuandoNoTieneCaracteresDeControl() {
        // Assert
        assertThat(RutaUri.sanear("/api/fichas/perfil")).isEqualTo("/api/fichas/perfil");
    }

    @Test
    void debeReemplazarCaracteresDeControl_cuandoIntentanInyectarUnaLineaDeLog() {
        // Assert
        assertThat(RutaUri.sanear("/api\r\nERROR falso")).isEqualTo("/api__ERROR falso");
        assertThat(RutaUri.sanear("/api\ttab")).isEqualTo("/api_tab");
    }

    @Test
    void debeDevolverDesconocido_cuandoLaRutaEstaAusente() {
        // Assert
        assertThat(RutaUri.sanear(null)).isEqualTo(TrazaValores.DESCONOCIDO);
        assertThat(RutaUri.sanear("")).isEqualTo(TrazaValores.DESCONOCIDO);
    }
}
