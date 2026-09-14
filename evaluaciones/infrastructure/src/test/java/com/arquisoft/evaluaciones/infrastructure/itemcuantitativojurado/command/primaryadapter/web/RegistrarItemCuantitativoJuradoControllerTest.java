package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.interactor.RegistrarItemCuantitativoJuradoInteractor;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.CategoriaItemCuantitativoJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.web.config.JacksonConfig;
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

@WebMvcTest(RegistrarItemCuantitativoJuradoController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        JacksonConfig.class,
        RegistrarItemCuantitativoJuradoControllerTest.TestSecurityConfig.class
})
class RegistrarItemCuantitativoJuradoControllerTest {

    private static final String RUTA = "/evaluaciones/items-cuantitativos-jurado";
    private static final UUID CATEGORIA =
            UUID.fromString("8d943e12-ea2d-4d67-a036-d8808e640eb4");
    private static final String BODY_VALIDO = """
            {
              "nombre": "Calidad técnica",
              "descripcion": "Evalúa la calidad técnica",
              "categoria": "8d943e12-ea2d-4d67-a036-d8808e640eb4",
              "valor": 100
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
    private RegistrarItemCuantitativoJuradoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar201_cuandoPeticionEsValida() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        when(interactor.ejecutar(any())).thenReturn(id);

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void debeRetornar400_cuandoPeticionEsInvalida() throws Exception {
        // Arrange
        String body = """
                {
                  "nombre": " ",
                  "descripcion": "",
                  "categoria": null,
                  "valor": 501
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(4));
    }

    @Test
    void debeRetornar400_cuandoValorEsDecimal() throws Exception {
        // Arrange
        String body = """
                {
                  "nombre": "Calidad técnica",
                  "descripcion": "Evalúa la calidad técnica",
                  "categoria": "8d943e12-ea2d-4d67-a036-d8808e640eb4",
                  "valor": 100.5
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoNoEstaAutenticado() throws Exception {
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoNoTienePermiso() throws Exception {
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(
                                        "evaluaciones:item-cuantitativo-jurado:view")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeRetornar422_cuandoCategoriaNoExiste() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenThrow(new CategoriaItemCuantitativoJuradoNoEncontradaException(CATEGORIA));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity());
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermiso() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(
                        EvaluacionesAuthorities.ITEM_CUANTITATIVO_JURADO_CREATE));
    }
}
