package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.interactor.ModificarItemCualitativoJuradoInteractor;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model.ModificarItemCualitativoJuradoCommand;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception.ItemCualitativoJuradoNoEncontradoException;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModificarItemCualitativoJuradoController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ModificarItemCualitativoJuradoControllerTest.TestSecurityConfig.class
})
class ModificarItemCualitativoJuradoControllerTest {

    private static final String BASE = "/evaluaciones/items-cualitativos-jurado";
    private static final String BODY_VALIDO = """
            {
              "descripcion": "Descripción institucional actualizada"
            }
            """;

    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity(prePostEnabled = true)
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
    private ModificarItemCualitativoJuradoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar204YDelegarCommandCorrecto_cuandoPeticionEsValida() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(patch(BASE + "/{itemId}", itemId)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isNoContent());

        verify(interactor).ejecutar(ModificarItemCualitativoJuradoCommand.crear(
                itemId, "Descripción institucional actualizada"));
    }

    @Test
    void debeRetornar400YNoInvocarInteractor_cuandoDescripcionEsInvalida() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();
        String bodyInvalido = """
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
        UUID itemId = UUID.randomUUID();
        doThrow(new ItemCualitativoJuradoNoEncontradoException(itemId))
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
        UUID itemId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(patch(BASE + "/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoNoTienePermiso() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(patch(BASE + "/{itemId}", itemId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(
                                        "evaluaciones:item-cualitativo-jurado:view")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isForbidden());
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermiso() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(
                        EvaluacionesAuthorities.ITEM_CUALITATIVO_JURADO_UPDATE));
    }
}
