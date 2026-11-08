package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.interactor.RegistrarItemCualitativoAsesorInteractor;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.NombreItemCualitativoAsesorDuplicadoException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrarItemCualitativoAsesorController.class)
@Import({
        AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        RegistrarItemCualitativoAsesorControllerTest.TestSecurityConfig.class
})
class RegistrarItemCualitativoAsesorControllerTest {

    private static final String RUTA = "/evaluaciones/items-cualitativos-asesor";
    private static final String BODY_VALIDO = """
            {
              "nombre": "Claridad",
              "descripcion": "Evalúa la claridad conceptual"
            }
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
    private RegistrarItemCualitativoAsesorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar201ConId_cuandoPeticionEsValida() throws Exception {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(id);

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void debeRetornar400ConFieldErrors_cuandoPeticionEsInvalida() throws Exception {
        // Arrange
        var body = """
                {
                  "nombre": " ",
                  "descripcion": ""
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(2));
    }

    @Test
    void debeRetornar401_cuandoNoEstaAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar422_cuandoNombreYaExiste() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenThrow(new NombreItemCualitativoAsesorDuplicadoException("Claridad"));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity());
    }
}
