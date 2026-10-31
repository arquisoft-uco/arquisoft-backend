package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.primaryport.interactor.ConsultarItemsCualitativosAsesorInteractor;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.readmodel.ItemCualitativoAsesorReadModel;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarItemsCualitativosAsesorController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarItemsCualitativosAsesorControllerTest.TestSecurityConfig.class
})
class ConsultarItemsCualitativosAsesorControllerTest {

    private static final String RUTA = "/evaluaciones/items-cualitativos-asesor";

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
    private ConsultarItemsCualitativosAsesorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeResponder200ConLista_cuandoExistenItems() throws Exception {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar()).thenReturn(List.of(
                new ItemCualitativoAsesorReadModel(id, "Puntualidad", "Evalúa la puntualidad"),
                new ItemCualitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), "Vestimenta",
                        "Evalúa la vestimenta")));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].nombre").value("Puntualidad"))
                .andExpect(jsonPath("$[0].descripcion").value("Evalúa la puntualidad"));
    }

    @Test
    void debeResponder200ConListaVacia_cuandoNoHayItems() throws Exception {
        // Arrange
        when(interactor.ejecutar()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
    }
}
