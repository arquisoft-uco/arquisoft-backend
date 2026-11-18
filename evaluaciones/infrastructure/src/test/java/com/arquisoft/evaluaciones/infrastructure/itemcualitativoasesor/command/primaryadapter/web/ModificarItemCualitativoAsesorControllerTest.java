package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.interactor.ModificarItemCualitativoAsesorInteractor;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model.ModificarItemCualitativoAsesorCommand;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.ItemCualitativoAsesorNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModificarItemCualitativoAsesorController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ModificarItemCualitativoAsesorControllerTest.TestSecurityConfig.class
})
class ModificarItemCualitativoAsesorControllerTest {

    private static final String BASE = "/evaluaciones/items-cualitativos-asesor";
    private static final String BODY_VALIDO = """
            {
              "descripcion": "Descripción técnica actualizada"
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
    private ModificarItemCualitativoAsesorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar204YDelegarCommandCorrecto_cuandoPeticionEsValida() throws Exception {
        // Arrange
        var itemId = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(patch(BASE + "/{itemId}", itemId)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isNoContent());

        verify(interactor).ejecutar(ModificarItemCualitativoAsesorCommand.crear(
                itemId, "Descripción técnica actualizada"));
    }

    @Test
    void debeRetornar400YNoInvocarInteractor_cuandoDescripcionEsInvalida() throws Exception {
        // Arrange
        var itemId = UtilUUID.generarNuevoUUID();
        var bodyInvalido = """
                {
                  "descripcion": ""
                }
                """;

        // Act & Assert
        mockMvc.perform(patch(BASE + "/{itemId}", itemId)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1));
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoItemNoExiste() throws Exception {
        // Arrange
        var itemId = UtilUUID.generarNuevoUUID();
        doThrow(new ItemCualitativoAsesorNoEncontradoException(itemId))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(BASE + "/{itemId}", itemId)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debeRetornar401_cuandoNoEstaAutenticado() throws Exception {
        // Arrange
        var itemId = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(patch(BASE + "/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }


    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermiso() {
        return SecurityMockMvcRequestPostProcessors.jwt();
    }
}
