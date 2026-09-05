package com.arquisoft.fichas.infrastructure.estadoevaluacion.query.primaryadapter.web;

import com.arquisoft.fichas.application.estadoevaluacion.query.primaryport.interactor.ConsultarEstadosEvaluacionInteractor;
import com.arquisoft.fichas.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
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

@WebMvcTest(ConsultarEstadosEvaluacionController.class)
@Import({GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEstadosEvaluacionControllerTest.TestSecurityConfig.class})
class ConsultarEstadosEvaluacionControllerTest {

    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity(prePostEnabled = true)
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((req, res, e) -> res.sendError(401, "Unauthorized"))
                    .accessDeniedHandler((req, res, e)    -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarEstadosEvaluacionInteractor consultarEstadosEvaluacionInteractor;

    private static final String RUTA = "/fichas-perfil/estados-evaluacion";

    private static List<EstadoEvaluacionReadModel> catalogoCompleto() {
        return List.of(
                new EstadoEvaluacionReadModel("EN_EVALUACION", "En Evaluacion", "En evaluacion por el comite."),
                new EstadoEvaluacionReadModel("APROBADA", "Aprobada", "Paso por revision y fue aprobada."),
                new EstadoEvaluacionReadModel("APROBADA_CON_OBSERVACIONES", "Aprobada Con Observaciones",
                        "Fue aprobada con observaciones."),
                new EstadoEvaluacionReadModel("NO_APROBADA", "No Aprobada", "No fue aprobada."),
                new EstadoEvaluacionReadModel("DESCARTADA", "Descartada", "Fue descartada.")
        );
    }

    @Test
    void debe200ConLosCincoEstados_cuandoTieneElClientRoleCorrecto() throws Exception {
        // Arrange
        when(consultarEstadosEvaluacionInteractor.ejecutar()).thenReturn(catalogoCompleto());

        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_EVALUACION_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].id").value("EN_EVALUACION"))
                .andExpect(jsonPath("$[0].nombre").value("En Evaluacion"))
                .andExpect(jsonPath("$[0].descripcion").value("En evaluacion por el comite."));
    }

    @Test
    void debe200ConListaVacia_cuandoNoHayEstados() throws Exception {
        // Arrange
        when(consultarEstadosEvaluacionInteractor.ejecutar()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_EVALUACION_VIEW))))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debeSerializarElResponseDTO_cuandoRetornaResultados() throws Exception {
        // Arrange
        List<EstadoEvaluacionReadModel> estados = List.of(
                new EstadoEvaluacionReadModel("DESCARTADA", "Descartada", "Fue descartada."),
                new EstadoEvaluacionReadModel("NO_APROBADA", "No Aprobada", "No fue aprobada.")
        );
        when(consultarEstadosEvaluacionInteractor.ejecutar()).thenReturn(estados);

        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_EVALUACION_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("DESCARTADA"))
                .andExpect(jsonPath("$[0].nombre").value("Descartada"))
                .andExpect(jsonPath("$[0].descripcion").value("Fue descartada."))
                .andExpect(jsonPath("$[1].id").value("NO_APROBADA"));
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoClientRoleInsuficiente() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("otro-permiso"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeInvocarInteractorUnaVez_cuandoEndpointEsLlamado() throws Exception {
        // Arrange
        when(consultarEstadosEvaluacionInteractor.ejecutar()).thenReturn(catalogoCompleto());

        // Act
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_EVALUACION_VIEW))))
                .andExpect(status().isOk());

        // Assert
        verify(consultarEstadosEvaluacionInteractor, times(1)).ejecutar();
    }
}
