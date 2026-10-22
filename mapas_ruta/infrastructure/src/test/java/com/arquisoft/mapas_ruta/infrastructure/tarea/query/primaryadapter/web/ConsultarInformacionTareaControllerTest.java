package com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.interactor.ConsultarInformacionTareaInteractor;
import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarInformacionTareaQuery;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.mapas_ruta.infrastructure.security.MapasRutaAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

@WebMvcTest(ConsultarInformacionTareaController.class)
@Import({AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarInformacionTareaControllerTest.TestSecurityConfig.class})
class ConsultarInformacionTareaControllerTest {

    private static final String RUTA = "/tareas/{tareaId}";

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
    private ConsultarInformacionTareaInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtEstudiante() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(MapasRutaAuthorities.TAREA_VIEW));
    }

    @Test
    void debeRetornar200ConElDto_cuandoLaTareaExiste() throws Exception {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var mapa = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var readModel = new TareaReadModel(tarea, "Redactar capitulo", mapa, LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 1), "NUEVA", "Nueva", asesor, "Ana Perez", "ana@uco.edu.co");
        when(interactor.ejecutar(any())).thenReturn(Optional.of(readModel));

        // Act
        mockMvc.perform(get(RUTA, tarea).with(jwtEstudiante()))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tarea.toString()))
                .andExpect(jsonPath("$.descripcion").value("Redactar capitulo"))
                .andExpect(jsonPath("$.mapaRuta").value(mapa.toString()))
                .andExpect(jsonPath("$.fechaInicioMapaRuta").value("2026-10-01"))
                .andExpect(jsonPath("$.fechaFinMapaRuta").value("2026-12-01"))
                .andExpect(jsonPath("$.estadoItem").value("NUEVA"))
                .andExpect(jsonPath("$.nombreEstadoItem").value("Nueva"))
                .andExpect(jsonPath("$.asesor").value(asesor.toString()))
                .andExpect(jsonPath("$.nombreAsesor").value("Ana Perez"))
                .andExpect(jsonPath("$.emailAsesor").value("ana@uco.edu.co"));
        var captor = ArgumentCaptor.forClass(ConsultarInformacionTareaQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().tarea()).isEqualTo(tarea);
    }

    @Test
    void debeRetornar404SinCuerpo_cuandoLaTareaNoExiste() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID()).with(jwtEstudiante()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    void debeRetornar400SinInvocarElInteractor_cuandoLaTareaNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, "no-es-un-uuid").with(jwtEstudiante()))
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

    @Test
    void debeRetornar403_cuandoSoloTieneElClientRoleDeCrearTarea() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(MapasRutaAuthorities.TAREA_CREATE))))
                .andExpect(status().isForbidden());
        verify(interactor, never()).ejecutar(any());
    }
}
