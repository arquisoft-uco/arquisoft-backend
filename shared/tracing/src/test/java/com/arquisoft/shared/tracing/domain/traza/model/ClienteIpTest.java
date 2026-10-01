package com.arquisoft.shared.tracing.domain.traza.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteIpTest {

    @Test
    void debeDevolverLaIpIntacta_cuandoLaAnonimizacionEstaDesactivada() {
        // Assert
        assertThat(ClienteIp.paraTraza("203.0.113.25", false)).isEqualTo("203.0.113.25");
    }

    @Test
    void debeAnularElUltimoOcteto_cuandoSeAnonimizaUnaIpv4() {
        // Assert
        assertThat(ClienteIp.paraTraza("203.0.113.25", true)).isEqualTo("203.0.113.0");
    }

    @Test
    void debeTruncarA48Bits_cuandoSeAnonimizaUnaIpv6() {
        // Assert
        assertThat(ClienteIp.paraTraza("2001:db8:85a3:8d3:1319:8a2e:370:7348", true))
                .isEqualTo("2001:db8:85a3::");
    }

    @Test
    void debeExpandirLaCompresion_cuandoLaIpv6VieneAbreviada() {
        // Assert
        assertThat(ClienteIp.paraTraza("fe80::1", true)).isEqualTo("fe80:0:0::");
        assertThat(ClienteIp.paraTraza("::1", true)).isEqualTo("0:0:0::");
        assertThat(ClienteIp.paraTraza("2001:db8::", true)).isEqualTo("2001:db8:0::");
        assertThat(ClienteIp.paraTraza("0:0:0:0:0:0:0:1", true)).isEqualTo("0:0:0::");
    }

    @Test
    void debeNoConservarNingunGrupoIdentificador_cuandoSeAnonimizaUnaIpv6() {
        // Assert
        assertThat(ClienteIp.paraTraza("fe80::dead:beef", true)).doesNotContain("dead", "beef");
    }

    @Test
    void debeDevolverInvalido_cuandoLaIpNoTieneFormaDeIp() {
        // Assert
        assertThat(ClienteIp.paraTraza("no-es-una-ip", false)).isEqualTo(TrazaValores.INVALIDO);
        assertThat(ClienteIp.paraTraza(null, false)).isEqualTo(TrazaValores.INVALIDO);
        assertThat(ClienteIp.paraTraza("", true)).isEqualTo(TrazaValores.INVALIDO);
    }
}
