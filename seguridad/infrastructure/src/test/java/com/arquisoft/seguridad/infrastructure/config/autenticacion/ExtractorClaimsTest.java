package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ExtractorClaimsTest {

    private final Map<String, Object> claims = Map.of(
            "sub", "u-1",
            "realm_access", Map.of("roles", List.of("A", "B")),
            "numero", 5);

    @Test
    void debeLeerPorRutaConPuntos_cuandoElClaimExiste() {
        // Act / Assert
        assertThat(ExtractorClaims.texto(claims, "sub")).isEqualTo("u-1");
        assertThat(ExtractorClaims.lista(claims, "realm_access.roles")).containsExactly("A", "B");
    }

    @Test
    void debeRetornarNuloOVacio_cuandoLaRutaEstaVaciaOAusenteOElTipoNoCoincide() {
        // Act / Assert
        assertThat(ExtractorClaims.valor(null, "sub")).isNull();
        assertThat(ExtractorClaims.valor(claims, "")).isNull();
        assertThat(ExtractorClaims.valor(claims, "sub.interno")).isNull();
        assertThat(ExtractorClaims.texto(claims, "numero")).isNull();
        assertThat(ExtractorClaims.lista(claims, "sub")).isEmpty();
        assertThat(ExtractorClaims.lista(claims, "no.existe")).isEmpty();
    }
}
