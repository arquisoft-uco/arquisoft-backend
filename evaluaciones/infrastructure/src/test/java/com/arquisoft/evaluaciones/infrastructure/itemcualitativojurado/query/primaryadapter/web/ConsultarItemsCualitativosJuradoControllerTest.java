package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.primaryport.interactor.ConsultarItemsCualitativosJuradoInteractor;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel.ItemCualitativoJuradoReadModel;
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

@WebMvcTest(ConsultarItemsCualitativosJuradoController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarItemsCualitativosJuradoControllerTest.TestSecurityConfig.class
})
class ConsultarItemsCualitativosJuradoControllerTest {

    private static final String RUTA = "/evaluaciones/items-cualitativos-jurado";

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
    private ConsultarItemsCualitativosJuradoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar200ConArregloDeDtos_cuandoTieneElClientRoleView() throws Exception {
        // Arrange
        List<ItemCualitativoJuradoReadModel> items = List.of(
                new ItemCualitativoJuradoReadModel(UUID.randomUUID(), "Claridad", "Evalúa la claridad conceptual"),
                new ItemCualitativoJuradoReadModel(UUID.randomUUID(), "Rigor", "Evalúa el rigor metodológico")
        );
        when(interactor.ejecutar()).thenReturn(items);

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermisoView()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void debeVerificarLosTresCamposYElOrden_deLosElementosSerializados() throws Exception {
        // Arrange
        UUID idClaridad = UUID.randomUUID();
        UUID idRigor = UUID.randomUUID();
        List<ItemCualitativoJuradoReadModel> items = List.of(
                new ItemCualitativoJuradoReadModel(idClaridad, "Claridad", "Evalúa la claridad conceptual"),
                new ItemCualitativoJuradoReadModel(idRigor, "Rigor", "Evalúa el rigor metodológico")
        );
        when(interactor.ejecutar()).thenReturn(items);

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermisoView()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(idClaridad.toString()))
                .andExpect(jsonPath("$[0].nombre").value("Claridad"))
                .andExpect(jsonPath("$[0].descripcion").value("Evalúa la claridad conceptual"))
                .andExpect(jsonPath("$[1].id").value(idRigor.toString()))
                .andExpect(jsonPath("$[1].nombre").value("Rigor"))
                .andExpect(jsonPath("$[1].descripcion").value("Evalúa el rigor metodológico"));
    }

    @Test
    void debeRetornar200ConListaVacia_cuandoElInteractorRetornaVacio() throws Exception {
        // Arrange
        when(interactor.ejecutar()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermisoView()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debeRetornar401_cuandoNoHayJwt() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoJwtSinAuthorities() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeRetornar403_cuandoTieneAuthorityDeCreacionPeroNoDeView() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(
                                        EvaluacionesAuthorities.ITEM_CUALITATIVO_JURADO_CREATE))))
                .andExpect(status().isForbidden());
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
                        EvaluacionesAuthorities.ITEM_CUALITATIVO_JURADO_VIEW));
    }
}
