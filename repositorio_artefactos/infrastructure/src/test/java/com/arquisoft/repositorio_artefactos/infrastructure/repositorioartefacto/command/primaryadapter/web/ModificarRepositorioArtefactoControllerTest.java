package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.interactor.ModificarRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.model.ModificarRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.exception.RepositorioArtefactoDuplicadoException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.shared.web.identidad.IdentidadActualTestConfig;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModificarRepositorioArtefactoController.class)
@Import({IdentidadActualTestConfig.class,
        AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ModificarRepositorioArtefactoControllerTest.TestSecurityConfig.class
})
class ModificarRepositorioArtefactoControllerTest {

    private static final String RUTA = "/repositorio-artefactos/{id}";
    private static final String BODY_VALIDO = """
            { "nombreRepositorio": "Repo", "descripcion": "Descripcion" }
            """;

    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .exceptionHandling(exception -> exception
                            .authenticationEntryPoint((request, response, error) ->
                                    response.sendError(401, "Unauthorized"))
                            .accessDeniedHandler((request, response, error) ->
                                    response.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModificarRepositorioArtefactoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermiso(String subject) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(subject));
    }

    @Test
    void debeResponder204_cuandoValido() throws Exception {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(patch(RUTA, repositorio)
                        .with(jwtConPermiso(administrador.toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isNoContent());
        var captor = ArgumentCaptor.forClass(ModificarRepositorioArtefactoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().repositorio()).isEqualTo(repositorio);
        assertThat(captor.getValue().administrador()).isEqualTo(administrador);
        assertThat(captor.getValue().nombreRepositorio()).isEqualTo("Repo");
    }

    @Test
    void debeResponder400ConFieldErrors_cuandoCuerpoInvalido() throws Exception {
        // Arrange
        var body = """
                { "nombreRepositorio": " ", "descripcion": "%s" }
                """.formatted("d".repeat(301));

        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .with(jwtConPermiso(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(2));
        verifyNoInteractions(interactor);
    }

    @Test
    void debeResponder401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(interactor);
    }

    @Test
    void debeResponder422_cuandoDuplicado() throws Exception {
        // Arrange
        doThrow(new RepositorioArtefactoDuplicadoException("Repo")).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .with(jwtConPermiso(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(RepositorioArtefactosCodes.RepositorioArtefacto.DUPLICADO));
    }
}
