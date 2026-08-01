package com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web;

import com.arquisoft.seguridad.application.auth.command.model.AuthenticateUserCommand;
import com.arquisoft.seguridad.application.auth.command.port.in.AuthenticateUserInputPort;
import com.arquisoft.seguridad.application.auth.command.port.in.LogoutInputPort;
import com.arquisoft.seguridad.application.auth.command.port.in.RefreshTokenInputPort;
import com.arquisoft.seguridad.application.auth.command.port.in.ValidateTokenInputPort;
import com.arquisoft.seguridad.infrastructure.filter.JwtBlacklistFilter;
import com.arquisoft.seguridad.infrastructure.filter.RateLimitingFilter;
import com.arquisoft.shared.web.exception.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Se excluyen los filtros @Component del módulo: @WebMvcTest los registra por defecto y
// dependen de puertos de dominio (TokenBlacklistOutputPort, BucketResolver) que este slice
// no levanta. No participan en el enlace del body, que es lo que aquí se verifica.
@WebMvcTest(controllers = AuthCommandInputAdapter.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtBlacklistFilter.class, RateLimitingFilter.class}))
@Import({GlobalAppExceptionHandler.class,
        AuthCommandInputAdapterWebTest.TestSecurityConfig.class})
class AuthCommandInputAdapterWebTest {

    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticateUserInputPort authenticateUserInputPort;

    @MockitoBean
    private RefreshTokenInputPort refreshTokenInputPort;

    @MockitoBean
    private LogoutInputPort logoutInputPort;

    @MockitoBean
    private ValidateTokenInputPort validateTokenInputPort;

    // ── login ──

    @Test
    void debeRetornar200YEnlazarCredenciales_cuandoBodyDeLoginEsValido() throws Exception {
        // Arrange
        when(authenticateUserInputPort.ejecutar(any())).thenReturn(
                new AuthenticateUserInputPort.AuthResult(
                        "access...", "refresh...", 3600L, "Bearer", "openid"));
        ArgumentCaptor<AuthenticateUserCommand> captor =
                ArgumentCaptor.forClass(AuthenticateUserCommand.class);

        // Act
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "estudiante@uco.edu.co",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access..."));

        // Assert — el DTO recibió los valores del JSON
        verify(authenticateUserInputPort).ejecutar(captor.capture());
        assertThat(captor.getValue().email()).isEqualTo("estudiante@uco.edu.co");
        assertThat(captor.getValue().password()).isEqualTo("password123");
    }

    @Test
    void debeRetornar400_cuandoEmailDeLoginTieneFormatoInvalido() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "no-es-un-email",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar400_cuandoPasswordDeLoginEsMuyCorta() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "estudiante@uco.edu.co",
                                  "password": "123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // ── refresh ──

    @Test
    void debeRetornar200YEnlazarRefreshToken_cuandoBodyDeRefreshEsValido() throws Exception {
        // Arrange
        when(refreshTokenInputPort.ejecutar(anyString())).thenReturn(
                new RefreshTokenInputPort.RefreshResult(
                        "access-new...", "refresh-new...", 3600L, "Bearer", "openid"));

        // Act & Assert
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "refresh-old..."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-new..."));

        // el DTO recibió el valor del JSON, no null
        verify(refreshTokenInputPort).ejecutar("refresh-old...");
    }

    @Test
    void debeRetornar400_cuandoRefreshTokenEstaVacio() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
