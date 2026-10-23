package com.arquisoft.seguridad.infrastructure.config.security;

import org.junit.jupiter.api.Test;

import org.springframework.mock.env.MockEnvironment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RutasPublicasTest {

    private static final List<String> PUBLICAS = List.of("/actuator/health/**", "/swagger-ui/**");
    private static final List<String> AUTENTICADAS = List.of("/auth/logout");

    private RutasPublicas rutas(String contextPath, String base, String proveedor) {
        var entorno = new MockEnvironment().withProperty("rutas.seguridad.auth.base", base);
        return new RutasPublicas(contextPath, PUBLICAS, AUTENTICADAS, proveedor, entorno);
    }

    @Test
    void debeIncluirLasRutasDeLoginDirecto_cuandoElProveedorEsKeycloak() {
        // Arrange / Act
        var rutas = rutas("/api", "/auth", "keycloak");

        // Assert
        assertThat(rutas.patronesPublicos()).contains("/auth/login", "/auth/refresh", "/auth/validate");
        assertThat(rutas.esPublica("/api/auth/login")).isTrue();
        assertThat(rutas.esPublica("/api/auth/logout")).isFalse();
        assertThat(rutas.esPublica("/api/fichas-perfil")).isFalse();
    }

    @Test
    void debeOmitirLasRutasDeLoginDirecto_cuandoElProveedorEsNone() {
        // Arrange / Act
        var rutas = rutas("/api", "/auth", "none");

        // Assert
        assertThat(rutas.esPublica("/api/auth/login")).isFalse();
        assertThat(rutas.esPublica("/api/actuator/health/liveness")).isTrue();
    }

    @Test
    void debeSeguirElYml_cuandoSeRenombraLaRutaBase() {
        // Arrange / Act
        var rutas = rutas("/api", "/autenticacion", "keycloak");

        // Assert
        assertThat(rutas.esPublica("/api/autenticacion/login")).isTrue();
        assertThat(rutas.esPublica("/api/auth/login")).isFalse();
    }

    @Test
    void debeReconocerLasRutasExentasDePep_conYSinContextPath() {
        // Arrange / Act
        var conContexto = rutas("/api", "/auth", "keycloak");
        var sinContexto = rutas("", "/auth", "keycloak");

        // Assert
        assertThat(conContexto.esExentaDePep("/api/auth/logout")).isTrue();
        assertThat(sinContexto.esExentaDePep("/auth/logout")).isTrue();
        assertThat(conContexto.esExentaDePep("/api/fichas-perfil")).isFalse();
    }
}
