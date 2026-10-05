package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.interactor.ModificarItemCuantitativoJuradoInteractor;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model.ModificarItemCuantitativoJuradoCommand;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.ItemCuantitativoJuradoNoEncontradoException;
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

@WebMvcTest(ModificarItemCuantitativoJuradoController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ModificarItemCuantitativoJuradoControllerTest.TestSecurityConfig.class
})
class ModificarItemCuantitativoJuradoControllerTest {

    private static final String BASE = "/evaluaciones/items-cuantitativos-jurado";
    private static final String BODY_VALIDO = """
            {
              "descripcion": "Descripción técnica actualizada"
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
    private ModificarItemCuantitativoJuradoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar204YDelegarCommandCorrecto_cuandoPeticionEsValida() throws Exception {
        // Arrange
        var itemId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(patch(BASE + "/{itemId}", itemId)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isNoContent());

        verify(interactor).ejecutar(ModificarItemCuantitativoJuradoCommand.crear(
                itemId, "Descripción técnica actualizada"));
    }

    @Test
    void debeRetornar400YNoInvocarInteractor_cuandoDescripcionEsInvalida() throws Exception {
        // Arrange
        var itemId = UUID.randomUUID();
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
        var itemId = UUID.randomUUID();
        doThrow(new ItemCuantitativoJuradoNoEncontradoException(itemId))
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
        var itemId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(patch(BASE + "/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoNoTienePermiso() throws Exception {
        // Arrange
        var itemId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(patch(BASE + "/{itemId}", itemId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(
                                        "evaluaciones:item-cuantitativo-jurado:create")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isForbidden());
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermiso() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(
                        EvaluacionesAuthorities.ITEM_CUANTITATIVO_JURADO_UPDATE));
    }
}
