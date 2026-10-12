package com.arquisoft.artefactos.infrastructure.estadorevisionasesor.query.primaryadapter.web;

import com.arquisoft.artefactos.application.estadorevisionasesor.query.primaryport.interactor.ConsultarEstadosRevisionAsesorInteractor;
import com.arquisoft.artefactos.application.estadorevisionasesor.query.readmodel.EstadoRevisionAsesorReadModel;
import com.arquisoft.artefactos.infrastructure.security.ArtefactosAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEstadosRevisionAsesorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEstadosRevisionAsesorControllerTest.TestSecurityConfig.class})
class ConsultarEstadosRevisionAsesorControllerTest {

    private static final String RUTA = "/artefactos/estados-revision-asesor";

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
                    .accessDeniedHandler((req, res, e) -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarEstadosRevisionAsesorInteractor interactor;

    @Test
    void debe200ConLaLista_cuandoTieneElClientRole() throws Exception {
        // Arrange
        when(interactor.ejecutar()).thenReturn(List.of(
                new EstadoRevisionAsesorReadModel("PENDIENTE", "Pendiente", "Nueva"),
                new EstadoRevisionAsesorReadModel("EN_PROGRESO", "En progreso", "Ajustes iniciados")));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt()
                        .authorities(new SimpleGrantedAuthority(ArtefactosAuthorities.ESTADO_REVISION_ASESOR_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].nombre").value("Pendiente"))
                .andExpect(jsonPath("$[0].descripcion").value("Nueva"))
                .andExpect(jsonPath("$[1].id").value("EN_PROGRESO"));
    }

    @Test
    void debe200ConListaVacia_cuandoNoHayEstados() throws Exception {
        // Arrange
        when(interactor.ejecutar()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt()
                        .authorities(new SimpleGrantedAuthority(ArtefactosAuthorities.ESTADO_REVISION_ASESOR_VIEW))))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debe401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());

        verify(interactor, never()).ejecutar();
    }

    @Test
    void debe403_cuandoClientRoleInsuficiente() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt()
                        .authorities(new SimpleGrantedAuthority("otro-permiso"))))
                .andExpect(status().isForbidden());

        verify(interactor, never()).ejecutar();
    }
}
