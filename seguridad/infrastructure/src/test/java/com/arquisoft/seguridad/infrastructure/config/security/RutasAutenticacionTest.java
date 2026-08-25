package com.arquisoft.seguridad.infrastructure.config.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RutasAutenticacionTest {

    private RutasAutenticacion rutas(String contextPath, String base) {
        return new RutasAutenticacion(contextPath, base, "/login", "/refresh", "/validate");
    }

    @Test
    void debeExponerLaRutaSinContextPath_paraSpringSecurity() {
        // Arrange / Act
        var rutas = rutas("/api", "/auth");

        // Assert
        assertThat(rutas.login()).isEqualTo("/auth/login");
        assertThat(rutas.refresh()).isEqualTo("/auth/refresh");
        assertThat(rutas.validate()).isEqualTo("/auth/validate");
    }

    @Test
    void debeReconocerLaRutaConContextPath_paraLosFiltros() {
        // Arrange / Act
        var rutas = rutas("/api", "/auth");

        // Assert
        assertThat(rutas.esPublica("/api/auth/login")).isTrue();
        assertThat(rutas.esPublica("/api/auth/refresh")).isTrue();
        assertThat(rutas.esPublica("/api/auth/validate")).isTrue();
        assertThat(rutas.esPublica("/api/auth/logout")).isFalse();
        assertThat(rutas.esPublica("/api/fichas-perfil")).isFalse();
    }

    @Test
    void debeSeguirElYml_cuandoSeRenombraLaRutaBase() {
        // Arrange / Act — esta es la deriva que la clase existe para impedir
        var rutas = rutas("/api", "/autenticacion");

        // Assert
        assertThat(rutas.login()).isEqualTo("/autenticacion/login");
        assertThat(rutas.esPublica("/api/autenticacion/login")).isTrue();
        assertThat(rutas.esPublica("/api/auth/login")).isFalse();
    }

    @Test
    void debeFuncionarSinContextPath_cuandoLaAplicacionSirveEnLaRaiz() {
        // Arrange / Act
        var rutas = rutas("", "/auth");

        // Assert
        assertThat(rutas.esPublica("/auth/login")).isTrue();
    }
}
