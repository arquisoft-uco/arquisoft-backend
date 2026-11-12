package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.interactor.ConsultarMapaRutaProyectoGradoAsesorInteractor;
import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapaRutaProyectoGradoAsesorQuery;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaEstudianteReadModel;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.shared.web.identidad.IdentidadActualTestConfig;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarMapaRutaProyectoGradoAsesorController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarMapaRutaProyectoGradoAsesorControllerTest.TestSecurityConfig.class})
class ConsultarMapaRutaProyectoGradoAsesorControllerTest {

    private static final String RUTA = "/mapas-ruta/proyectos-grado/{proyectoGradoId}/asesor";

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
    private ConsultarMapaRutaProyectoGradoAsesorInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtAsesor() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(UtilUUID.generarNuevoUUID().toString()));
    }

    @Test
    void debeRetornar200ConElDto_cuandoElProyectoTieneMapa() throws Exception {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var idMapa = UtilUUID.generarNuevoUUID();
        var readModel = new MapaRutaEstudianteReadModel(idMapa, proyectoGrado, "Sistema de gestion",
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));
        when(interactor.ejecutar(any())).thenReturn(Optional.of(readModel));

        // Act
        mockMvc.perform(get(RUTA, proyectoGrado).with(jwtAsesor()))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idMapa.toString()))
                .andExpect(jsonPath("$.proyectoGrado").value(proyectoGrado.toString()))
                .andExpect(jsonPath("$.tituloProyecto").value("Sistema de gestion"))
                .andExpect(jsonPath("$.fechaInicio").value("2026-10-01"))
                .andExpect(jsonPath("$.fechaFin").value("2026-12-01"));
        var captor = ArgumentCaptor.forClass(ConsultarMapaRutaProyectoGradoAsesorQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyectoGrado);
    }

    @Test
    void debeRetornar404SinCuerpo_cuandoElProyectoNoTieneMapa() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID()).with(jwtAsesor()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    void debeRetornar400SinInvocarElInteractor_cuandoElProyectoGradoEstaMalFormado() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, "no-es-un-uuid").with(jwtAsesor()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar401_cuandoNoEstaAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }
}
