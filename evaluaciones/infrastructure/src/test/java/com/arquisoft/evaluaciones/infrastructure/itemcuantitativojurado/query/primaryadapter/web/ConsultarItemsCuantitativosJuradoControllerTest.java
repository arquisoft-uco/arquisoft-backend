package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.primaryport.interactor.ConsultarItemsCuantitativosJuradoInteractor;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarItemsCuantitativosJuradoController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarItemsCuantitativosJuradoControllerTest.TestSecurityConfig.class
})
class ConsultarItemsCuantitativosJuradoControllerTest {

    private static final String RUTA = "/evaluaciones/items-cuantitativos-jurado";

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
    private ConsultarItemsCuantitativosJuradoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar200ConLaListaDeItems_cuandoElUsuarioTieneElRolDeVista() throws Exception {
        // Arrange
        List<ItemCuantitativoJuradoReadModel> items = List.of(
                new ItemCuantitativoJuradoReadModel(UUID.randomUUID(), "Puntualidad", "Evalúa la puntualidad",
                        UUID.randomUUID(), 50),
                new ItemCuantitativoJuradoReadModel(UUID.randomUUID(), "Vestimenta", "Evalúa la vestimenta",
                        UUID.randomUUID(), 20));
        when(interactor.ejecutar()).thenReturn(items);

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermisoView()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void debeRetornar200ConListaVacia_cuandoNoHayItemsRegistrados() throws Exception {
        // Arrange
        when(interactor.ejecutar()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermisoView()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debeRetornar401_cuandoNoHayTokenValido() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoElUsuarioNoTieneElRolDeVista() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeSerializarCategoriaIdYValorEnLaRespuesta_cuandoHayResultados() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        when(interactor.ejecutar()).thenReturn(List.of(
                new ItemCuantitativoJuradoReadModel(id, "Puntualidad", "Evalúa la puntualidad", categoriaId, 50)));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermisoView()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].nombre").value("Puntualidad"))
                .andExpect(jsonPath("$[0].descripcion").value("Evalúa la puntualidad"))
                .andExpect(jsonPath("$[0].categoriaId").value(categoriaId.toString()))
                .andExpect(jsonPath("$[0].valor").value(50));
    }

    @Test
    void debeDelegarUnaSolaVez_enElInteractor() throws Exception {
        // Arrange
        when(interactor.ejecutar()).thenReturn(List.of());

        // Act
        mockMvc.perform(get(RUTA).with(jwtConPermisoView()))
                .andExpect(status().isOk());

        // Assert
        verify(interactor, times(1)).ejecutar();
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermisoView() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(
                        EvaluacionesAuthorities.ITEM_CUANTITATIVO_JURADO_VIEW));
    }
}
