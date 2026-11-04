package com.arquisoft.artefactos.infrastructure.observacion.query.primaryadapter.web;

import com.arquisoft.artefactos.application.observacion.query.primaryport.interactor.ConsultarObservacionesRevisionEstudianteInteractor;
import com.arquisoft.artefactos.application.observacion.query.primaryport.model.ConsultarObservacionesRevisionEstudianteQuery;
import com.arquisoft.artefactos.application.observacion.query.readmodel.ObservacionReadModel;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarObservacionesRevisionEstudianteController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, ConsultarObservacionesRevisionEstudianteControllerTest.TestSecurityConfig.class})
class ConsultarObservacionesRevisionEstudianteControllerTest {

    private static final String RUTA = "/revisiones-asesor/{id}/observaciones/estudiante";

    private static final UUID ESTUDIANTE_ID = UtilUUID.generarNuevoUUID();

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
                    .accessDeniedHandler((req, res, e)    -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarObservacionesRevisionEstudianteInteractor consultarObservacionesRevisionEstudianteInteractor;

    private static org.springframework.test.web.servlet.request.RequestPostProcessor tokenEstudiante() {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(ESTUDIANTE_ID.toString()));
    }

    @Test
    void debe200ConLaLista_cuandoHayObservaciones() throws Exception {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var observacion = new ObservacionReadModel(
                UtilUUID.generarNuevoUUID(), revision, "Corregir", "NUEVA", "Nueva");
        when(consultarObservacionesRevisionEstudianteInteractor.ejecutar(
                any(ConsultarObservacionesRevisionEstudianteQuery.class)))
                .thenReturn(List.of(observacion));

        // Act & Assert
        mockMvc.perform(get(RUTA, revision).with(tokenEstudiante()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(observacion.id().toString()))
                .andExpect(jsonPath("$[0].revisionAsesor").value(revision.toString()))
                .andExpect(jsonPath("$[0].descripcion").value("Corregir"))
                .andExpect(jsonPath("$[0].estado").value("NUEVA"))
                .andExpect(jsonPath("$[0].estadoNombre").value("Nueva"));

        var captor = ArgumentCaptor.forClass(ConsultarObservacionesRevisionEstudianteQuery.class);
        verify(consultarObservacionesRevisionEstudianteInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().revisionAsesor()).isEqualTo(revision);
        assertThat(captor.getValue().estudiante()).isEqualTo(ESTUDIANTE_ID);
    }

    @Test
    void debe200ConListaVacia_cuandoNoHay() throws Exception {
        // Arrange
        when(consultarObservacionesRevisionEstudianteInteractor.ejecutar(
                any(ConsultarObservacionesRevisionEstudianteQuery.class)))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID()).with(tokenEstudiante()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void debe400_cuandoRevisionAsesorIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, "no-es-uuid").with(tokenEstudiante()))
                .andExpect(status().isBadRequest());

        verify(consultarObservacionesRevisionEstudianteInteractor, never()).ejecutar(any());
    }

    @Test
    void debe401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());

        verify(consultarObservacionesRevisionEstudianteInteractor, never()).ejecutar(any());
    }
}
