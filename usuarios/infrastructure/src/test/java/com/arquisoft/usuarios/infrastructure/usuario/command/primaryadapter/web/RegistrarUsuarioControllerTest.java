package com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web;

import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.RegistrarUsuarioInteractor;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioEmailDuplicadoException;
import com.arquisoft.usuarios.infrastructure.security.UsuariosAuthorities;
import com.arquisoft.usuarios.infrastructure.usuario.exception.ProveedorIdentidadUsuarioNoDisponibleException;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrarUsuarioController.class)
@Import({com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        RegistrarUsuarioControllerTest.TestSecurityConfig.class})
class RegistrarUsuarioControllerTest {

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
                    .accessDeniedHandler((req, res, e) -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrarUsuarioInteractor registrarUsuarioInteractor;

    private static final String BODY_VALIDO = """
            {
              "identificador": "usr001",
              "nombres": "Ana",
              "apellidos": "Pérez",
              "email": "ana@uco.edu.co",
              "contacto": "573001112233"
            }
            """;

    @Test
    void debe201_cuandoPeticionValida() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        when(registrarUsuarioInteractor.ejecutar(any())).thenReturn(id);

        // Act & Assert
        mockMvc.perform(post("/usuarios")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.USUARIO_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void debe201_cuandoRolesAusenteEnElJson() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        when(registrarUsuarioInteractor.ejecutar(any())).thenReturn(id);

        // Act & Assert
        mockMvc.perform(post("/usuarios")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.USUARIO_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isCreated());
    }

    @Test
    void debe201_cuandoRolesConocidos() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        when(registrarUsuarioInteractor.ejecutar(any())).thenReturn(id);
        String body = """
                {
                  "identificador": "usr002",
                  "nombres": "Juan",
                  "apellidos": "Gómez",
                  "email": "juan@uco.edu.co",
                  "contacto": "573001112244",
                  "roles": ["estudiante", "asesor"]
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/usuarios")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.USUARIO_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void debe400_cuandoFaltaEmail() throws Exception {
        // Arrange
        String body = """
                {
                  "identificador": "usr003",
                  "nombres": "Juan",
                  "apellidos": "Gómez",
                  "contacto": "573001112244"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/usuarios")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.USUARIO_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe400_cuandoRolDesconocido() throws Exception {
        // Arrange
        String body = """
                {
                  "identificador": "usr004",
                  "nombres": "Juan",
                  "apellidos": "Gómez",
                  "email": "juan@uco.edu.co",
                  "contacto": "573001112255",
                  "roles": ["jefe"]
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/usuarios")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.USUARIO_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoRolInsuficiente() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/usuarios")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("estudiante:perfil:read")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe422_cuandoEmailDuplicado() throws Exception {
        // Arrange
        when(registrarUsuarioInteractor.ejecutar(any()))
                .thenThrow(new UsuarioEmailDuplicadoException("ana@uco.edu.co"));

        // Act & Assert
        mockMvc.perform(post("/usuarios")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.USUARIO_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe503_cuandoElProveedorDeIdentidadNoEstaDisponible() throws Exception {
        // Arrange
        when(registrarUsuarioInteractor.ejecutar(any()))
                .thenThrow(new ProveedorIdentidadUsuarioNoDisponibleException("no disponible"));

        // Act & Assert
        mockMvc.perform(post("/usuarios")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.USUARIO_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isServiceUnavailable());
    }
}
