package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProveedorBearerTest {

    private static final ClaimsIdentidad CLAIMS = new ClaimsIdentidad("sub", "email", "preferred_username", "name", "");

    private final AuthenticationManager gestor = mock(AuthenticationManager.class);

    private ProveedorBearer proveedor(boolean emisorHabilitado) {
        var emisor = new AutenticacionProperties.Emisor("principal", emisorHabilitado, "https://idp.test", "", List.of(),
                ModoAutorizacion.ROLE_TABLE, false, CLAIMS);
        return new ProveedorBearer(new AutenticacionProperties(List.of(emisor)), gestor);
    }

    @Test
    void debeExtraerElToken_cuandoHayCabeceraBearer() {
        // Arrange
        var request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer tok-1");

        // Act / Assert
        assertThat(proveedor(true).extraerCredencial(request)).contains("tok-1");
    }

    @Test
    void debeDevolverVacio_cuandoNoHayCabecera() {
        // Arrange
        var basic = new MockHttpServletRequest();
        basic.addHeader(HttpHeaders.AUTHORIZATION, "Basic abc");

        // Act / Assert
        assertThat(proveedor(true).extraerCredencial(new MockHttpServletRequest())).isEmpty();
        assertThat(proveedor(true).extraerCredencial(basic)).isEmpty();
    }

    @Test
    void debeLanzarAutenticacion_cuandoLaCabeceraEstaMalformada() {
        // Arrange
        var request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer ");
        var proveedor = proveedor(true);

        // Act / Assert
        assertThatThrownBy(() -> proveedor.extraerCredencial(request))
                .isInstanceOf(OAuth2AuthenticationException.class);
    }

    @Test
    void debeDelegarEnElGestor_cuandoAutentica() {
        // Arrange
        var identidad = new BearerAuthenticationToken("principal", "sub-1", null, null, null, null, null);
        when(gestor.authenticate(any(BearerTokenAuthenticationToken.class))).thenReturn(identidad);

        // Act
        var resultado = proveedor(true).autenticar("tok-1");

        // Assert
        assertThat(resultado).isSameAs(identidad);
        verify(gestor).authenticate(any(BearerTokenAuthenticationToken.class));
    }

    @Test
    void debeAdjuntarAuthorizationBearer_alAdjuntarCredencial() {
        // Arrange
        var cabeceras = new HttpHeaders();

        // Act
        proveedor(true).adjuntarCredencial(cabeceras, "tok-1");

        // Assert
        assertThat(cabeceras.getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer tok-1");
    }

    @Test
    void debeAtenderSoloLosEmisoresConfigurados_alConsultarAutorizacion() {
        // Arrange
        var proveedor = proveedor(true);

        // Act / Assert
        assertThat(proveedor.id()).isEqualTo("bearer");
        assertThat(proveedor.autorizacion("principal")).contains(ModoAutorizacion.ROLE_TABLE);
        assertThat(proveedor.autorizacion("otro")).isEmpty();
        assertThat(proveedor.habilitado()).isTrue();
        assertThat(proveedor(false).habilitado()).isFalse();
        assertThat(proveedor.autenticaPrimero()).isTrue();
        assertThat(proveedor.actuaEnRutasNoDeNegocio()).isTrue();
        assertThat(proveedor.exigeProteccionCsrf()).isFalse();
    }
}
