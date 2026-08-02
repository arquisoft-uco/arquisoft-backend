package com.arquisoft.usuarios.infrastructure.usuario.command.adapter.in.web;

import com.arquisoft.usuarios.application.usuario.command.model.CrearUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.usecase.CrearUsuarioUseCase;
import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;
import com.arquisoft.shared.web.exception.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioCommandInputAdapter.class)
@Import({GlobalAppExceptionHandler.class,
        UsuarioCommandInputAdapterWebTest.TestSecurityConfig.class})
class UsuarioCommandInputAdapterWebTest {

    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity(prePostEnabled = true)
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((req, res, e) -> res.sendError(401, "Unauthorized"))
                    .accessDeniedHandler((req, res, e)    -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CrearUsuarioUseCase crearUsuarioUseCase;

    private static final String BODY_VALIDO = """
            {
              "email": "test@example.com",
              "rol": "ESTUDIANTE"
            }
            """;

    // Se usa user() y no jwt(): el módulo usuarios no tiene oauth2-resource-server en el
    // classpath, y el adapter solo exige la authority vía @PreAuthorize, no el Jwt principal.
    private static SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor usuarioAdmin() {
        return SecurityMockMvcRequestPostProcessors.user("admin")
                .authorities(new SimpleGrantedAuthority("usuarios:usuario:create"));
    }

    @Test
    void debeRetornar201_cuandoBodyEsValido() throws Exception {
        // Arrange
        UUID idEsperado = UUID.randomUUID();
        when(crearUsuarioUseCase.ejecutar(any(CrearUsuarioCommand.class))).thenReturn(idEsperado);

        // Act & Assert
        mockMvc.perform(post("/usuarios")
                        .with(usuarioAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(idEsperado.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.rol").value("estudiante"));
    }

    @Test
    void debeEnlazarTodosLosCampos_cuandoBodyEsValido() throws Exception {
        // Arrange
        when(crearUsuarioUseCase.ejecutar(any(CrearUsuarioCommand.class))).thenReturn(UUID.randomUUID());
        ArgumentCaptor<CrearUsuarioCommand> captor = ArgumentCaptor.forClass(CrearUsuarioCommand.class);

        // Act
        mockMvc.perform(post("/usuarios")
                        .with(usuarioAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@example.com",
                                  "rol": "ADMINISTRADOR"
                                }
                                """))
                .andExpect(status().isCreated());

        // Assert — el DTO recibió los valores del JSON, no los defaults
        verify(crearUsuarioUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().email()).isEqualTo("admin@example.com");
        assertThat(captor.getValue().rol()).isEqualTo(UsuarioRole.ADMINISTRADOR);
    }

    @Test
    void debeRetornar400_cuandoEmailTieneFormatoInvalido() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .with(usuarioAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "no-es-un-email",
                                  "rol": "ESTUDIANTE"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar400_cuandoRolEsNulo() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .with(usuarioAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@example.com"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoNoEstaAutenticado() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }
}
