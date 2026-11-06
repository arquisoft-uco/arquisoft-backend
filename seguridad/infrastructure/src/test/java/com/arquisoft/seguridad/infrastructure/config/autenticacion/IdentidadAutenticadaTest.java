package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

import static org.assertj.core.api.Assertions.assertThat;

class IdentidadAutenticadaTest {

    @Test
    void debeDevolverVacio_cuandoNoHayAutenticacionOEsAnonima() {
        // Arrange
        var anonima = new AnonymousAuthenticationToken("k", "anon", AuthorityUtils.createAuthorityList("ROLE_ANON"));

        // Act / Assert
        assertThat(IdentidadAutenticada.desde(null)).isEmpty();
        assertThat(IdentidadAutenticada.desde(anonima)).isEmpty();
    }

    @Test
    void debeDevolverLaIdentidad_cuandoElTokenLaImplementa() {
        // Arrange
        var token = new SesionAuthenticationToken("sesion-pdp", "sub-1", "Ana", "a@uco.edu.co", "t");

        // Act / Assert
        assertThat(IdentidadAutenticada.desde(token)).containsSame(token);
    }
}
