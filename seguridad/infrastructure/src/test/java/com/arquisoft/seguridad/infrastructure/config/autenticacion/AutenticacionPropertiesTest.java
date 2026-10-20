package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AutenticacionPropertiesTest {

    private static AutenticacionProperties.Emisor emisor(String id, boolean habilitado, boolean listaNegra) {
        return new AutenticacionProperties.Emisor(id, habilitado, "https://" + id, "", List.of("a", " "),
                ModoAutorizacion.PEP, listaNegra, new ClaimsIdentidad("sub", "email", "u", "n", "", ""));
    }

    @Test
    void debeBuscarSoloEntreHabilitados_cuandoSeConsultaPorIdOIssuer() {
        // Arrange
        var propiedades = new AutenticacionProperties(List.of(emisor("principal", true, false), emisor("legado", false, true)));

        // Act / Assert
        assertThat(propiedades.habilitados()).extracting(AutenticacionProperties.Emisor::id).containsExactly("principal");
        assertThat(propiedades.porId("principal")).isPresent();
        assertThat(propiedades.porId("legado")).isEmpty();
        assertThat(propiedades.porIssuer("https://principal")).isPresent();
        assertThat(propiedades.porIssuer("https://legado")).isEmpty();
    }

    @Test
    void debeConservarBlacklistPorEmisorYNormalizarAudiencias_cuandoSeConstruye() {
        // Arrange
        var propiedades = new AutenticacionProperties(List.of(emisor("principal", true, false), emisor("legado", true, true)));

        // Act / Assert
        assertThat(propiedades.porId("principal").orElseThrow().blacklist()).isFalse();
        assertThat(propiedades.porId("legado").orElseThrow().blacklist()).isTrue();
        assertThat(propiedades.porId("legado").orElseThrow().audiences()).containsExactly("a");
        assertThat(new AutenticacionProperties(null).issuers()).isEmpty();
    }
}
