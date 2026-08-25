package com.arquisoft.usuarios.infrastructure.representantecomitecurriculum.command.primaryadapter.web;

import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.exception.GlobalAppExceptionHandler;
import com.arquisoft.usuarios.application.representantecomitecurriculum.command.primaryport.interactor.AgregarRepresentanteComiteCurriculumInteractor;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.exception.UsuarioNoEncontradoException;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.exception.UsuarioYaEsRepresentanteException;
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

@WebMvcTest(AgregarRepresentanteComiteCurriculumController.class)
@Import({GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        AgregarRepresentanteComiteCurriculumControllerTest.TestSecurityConfig.class})
class AgregarRepresentanteComiteCurriculumControllerTest {

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
    private AgregarRepresentanteComiteCurriculumInteractor interactor;

    // Se usa user() y no jwt(): el módulo usuarios no tiene oauth2-resource-server en el
    // classpath, y el adapter solo exige la authority vía @PreAuthorize, no el Jwt principal.
    private static SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor usuarioAdmin() {
        return SecurityMockMvcRequestPostProcessors.user("admin")
                .authorities(new SimpleGrantedAuthority("usuarios:representante-comite-curriculum:create"));
    }

    @Test
    void debe201_cuandoPeticionValida() throws Exception {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        String body = String.format("""
                {
                  "usuario": "%s"
                }
                """, usuarioId);

        when(interactor.ejecutar(any())).thenReturn(usuarioId);

        // Act & Assert
        mockMvc.perform(post("/representantes-comite-curriculum")
                        .with(usuarioAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(usuarioId.toString()));
    }

    @Test
    void debe422_cuandoRequestInvalido() throws Exception {
        // Arrange
        String bodyInvalido = """
                {
                  "usuario": ""
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/representantes-comite-curriculum")
                        .with(usuarioAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe422_cuandoUsuarioNoEncontrado() throws Exception {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        String body = String.format("""
                {
                  "usuario": "%s"
                }
                """, usuarioId);

        when(interactor.ejecutar(any())).thenThrow(new UsuarioNoEncontradoException(usuarioId));

        // Act & Assert
        mockMvc.perform(post("/representantes-comite-curriculum")
                        .with(usuarioAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe422_cuandoUsuarioYaEsRepresentante() throws Exception {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        String body = String.format("""
                {
                  "usuario": "%s"
                }
                """, usuarioId);

        when(interactor.ejecutar(any())).thenThrow(new UsuarioYaEsRepresentanteException(usuarioId));

        // Act & Assert
        mockMvc.perform(post("/representantes-comite-curriculum")
                        .with(usuarioAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe403_cuandoRolIncorrecto() throws Exception {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        String body = String.format("""
                {
                  "usuario": "%s"
                }
                """, usuarioId);

        // Act & Assert
        mockMvc.perform(post("/representantes-comite-curriculum")
                        .with(SecurityMockMvcRequestPostProcessors.user("otro")
                                .authorities(new SimpleGrantedAuthority("otro-rol")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe401_cuandoSinToken() throws Exception {
        // Arrange
        UUID usuarioId = UUID.randomUUID();
        String body = String.format("""
                {
                  "usuario": "%s"
                }
                """, usuarioId);

        // Act & Assert
        mockMvc.perform(post("/representantes-comite-curriculum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
