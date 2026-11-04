package com.arquisoft.entregables.infrastructure.estadoentregable.query.primaryadapter.web;

import com.arquisoft.entregables.application.estadoentregable.query.primaryport.interactor.ConsultarEstadosEntregableInteractor;
import com.arquisoft.entregables.application.estadoentregable.query.readmodel.EstadoEntregableReadModel;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.shared.web.identidad.IdentidadActualTestConfig;
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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEstadosEntregableController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEstadosEntregableControllerTest.TestSecurityConfig.class})
class ConsultarEstadosEntregableControllerTest {

    private static final String RUTA = "/estados-entregable";

    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .exceptionHandling(ex -> ex
                            .authenticationEntryPoint((req, res, e) -> res.sendError(401, "Unauthorized"))
                            .accessDeniedHandler((req, res, e) -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarEstadosEntregableInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtAsesor() {
        return SecurityMockMvcRequestPostProcessors.jwt();
    }

    @Test
    void debeRetornar200ConLosEstados_cuandoEstaAutenticado() throws Exception {
        // Arrange
        when(interactor.ejecutar()).thenReturn(List.of(
                new EstadoEntregableReadModel("DISPONIBLE_EVALUACION", "Disponible Evaluacion", "Listo"),
                new EstadoEntregableReadModel("EN_EVALUACION", "En Evaluacion", "Evaluando"),
                new EstadoEntregableReadModel("CERRADO", "Cerrado", "Cerrado ok")));

        // Act
        mockMvc.perform(get(RUTA).with(jwtAsesor()))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value("DISPONIBLE_EVALUACION"))
                .andExpect(jsonPath("$[0].nombre").value("Disponible Evaluacion"))
                .andExpect(jsonPath("$[0].descripcion").value("Listo"))
                .andExpect(jsonPath("$[2].id").value("CERRADO"));
    }

    @Test
    void debeRetornar200ConListaVacia_cuandoNoHayEstados() throws Exception {
        // Arrange
        when(interactor.ejecutar()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtAsesor()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void debeRetornar401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar();
    }
}
