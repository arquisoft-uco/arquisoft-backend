package com.arquisoft.evaluaciones.infrastructure.estadoevaluacion.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.estadoevaluacion.query.primaryport.interactor.ConsultarEstadosEvaluacionEvaluacionesInteractor;
import com.arquisoft.evaluaciones.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEstadosEvaluacionEvaluacionesController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarEstadosEvaluacionEvaluacionesControllerTest.TestSecurityConfig.class
})
class ConsultarEstadosEvaluacionEvaluacionesControllerTest {

    private static final String RUTA = "/evaluaciones/estados-evaluacion";

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
    private ConsultarEstadosEvaluacionEvaluacionesInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    // Los 4 roles autorizados por el plan (estudiante, coordinador, asesor, jurado) se mapean en
    // Keycloak al MISMO client role `evaluaciones:estado-evaluacion:view`: Spring Security solo ve
    // esa autoridad, nunca el rol de realm. Un test por "rol" con el mismo JWT simulado (misma
    // autoridad) era el mismo Act y el mismo Assert repetido 3 veces (anti-patrón de duplicación) y
    // no hubiera detectado una regresión distinta a esta; se consolida en un solo test que además
    // verifica el orden de la lista a través del controller, y se agrega abajo el caso negativo real
    // que sí distingue algo — un rol autenticado pero DISTINTO al requerido.
    @Test
    void debeRetornar200ConLaListaEnOrden_cuandoElUsuarioTieneElClientRoleRequerido() throws Exception {
        // Arrange
        when(interactor.ejecutar()).thenReturn(estadosDeCatalogo());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermisoView()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].nombre").value("Pendiente"))
                .andExpect(jsonPath("$[1].id").value("EN_PROGRESO"))
                .andExpect(jsonPath("$[2].id").value("FINALIZADA"));
    }

    @Test
    void debeRetornar403_cuandoElUsuarioNoTieneElClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeRetornar403_cuandoElUsuarioTieneUnClientRoleDistintoAlRequerido() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt()
                        .authorities(new SimpleGrantedAuthority(
                                EvaluacionesAuthorities.ITEM_CUALITATIVO_JURADO_VIEW))))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeRetornar401_cuandoNoHayTokenJwt() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornarCuerpoVacio_cuandoElInteractorNoDevuelveEstados() throws Exception {
        // Arrange
        when(interactor.ejecutar()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermisoView()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
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

    private static List<EstadoEvaluacionReadModel> estadosDeCatalogo() {
        return List.of(
                new EstadoEvaluacionReadModel("PENDIENTE", "Pendiente", "Indica que una evaluación está pendiente por realizar"),
                new EstadoEvaluacionReadModel("EN_PROGRESO", "En progreso", "Indica que una evaluación está en curso"),
                new EstadoEvaluacionReadModel("FINALIZADA", "Finalizada", "Indica que una evaluación ha sido finalizada")
        );
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermisoView() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(
                        EvaluacionesAuthorities.ESTADO_EVALUACION_VIEW));
    }
}
