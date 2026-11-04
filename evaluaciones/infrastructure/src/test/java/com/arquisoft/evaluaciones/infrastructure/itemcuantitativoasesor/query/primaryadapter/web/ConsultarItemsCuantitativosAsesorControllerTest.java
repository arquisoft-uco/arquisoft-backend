package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.primaryport.interactor.ConsultarItemsCuantitativosAsesorInteractor;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.readmodel.ItemCuantitativoAsesorReadModel;
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

@WebMvcTest(ConsultarItemsCuantitativosAsesorController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarItemsCuantitativosAsesorControllerTest.TestSecurityConfig.class
})
class ConsultarItemsCuantitativosAsesorControllerTest {

    private static final String RUTA = "/evaluaciones/items-cuantitativos-asesor";

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
    private ConsultarItemsCuantitativosAsesorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar200ConLista_cuandoExistenItems() throws Exception {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var categoriaId = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar()).thenReturn(List.of(
                new ItemCuantitativoAsesorReadModel(id, "Puntualidad", "Evalúa la puntualidad", categoriaId, 50),
                new ItemCuantitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), "Vestimenta",
                        "Evalúa la vestimenta", categoriaId, 20)));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].nombre").value("Puntualidad"))
                .andExpect(jsonPath("$[0].descripcion").value("Evalúa la puntualidad"))
                .andExpect(jsonPath("$[0].categoriaId").value(categoriaId.toString()))
                .andExpect(jsonPath("$[0].valor").value(50));
    }

    @Test
    void debeRetornar200ListaVacia_cuandoNoHayItems() throws Exception {
        // Arrange
        when(interactor.ejecutar()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debeRetornar401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
    }
}
