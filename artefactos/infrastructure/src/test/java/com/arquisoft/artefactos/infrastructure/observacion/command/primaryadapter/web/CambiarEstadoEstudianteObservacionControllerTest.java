package com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web;

import com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.CambiarEstadoEstudianteObservacionInteractor;
import com.arquisoft.artefactos.application.observacion.command.primaryport.model.CambiarEstadoEstudianteObservacionCommand;
import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoEncontradaException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
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
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CambiarEstadoEstudianteObservacionController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, CambiarEstadoEstudianteObservacionControllerTest.TestSecurityConfig.class})
class CambiarEstadoEstudianteObservacionControllerTest {

    private static final String RUTA = "/artefactos/observaciones/{observacionId}/estado-estudiante";

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
    private CambiarEstadoEstudianteObservacionInteractor interactor;

    @Test
    void debeResponder204_cuandoValido() throws Exception {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(patch(RUTA, observacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(estudiante.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"SOLUCIONADA\"}"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        var captor = ArgumentCaptor.forClass(CambiarEstadoEstudianteObservacionCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().observacion()).isEqualTo(observacion);
        assertThat(captor.getValue().estado()).isEqualTo("SOLUCIONADA");
        assertThat(captor.getValue().estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeResponder400_cuandoEstadoAusente() throws Exception {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(estudiante.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder400_cuandoElIdDeLaObservacionNoEsUuid() throws Exception {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(patch(RUTA, "no-es-uuid")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(estudiante.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"SOLUCIONADA\"}"))
                .andExpect(status().isBadRequest());

        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_sinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"SOLUCIONADA\"}"))
                .andExpect(status().isUnauthorized());

        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder422_cuandoElInteractorLanzaDomainException() throws Exception {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        doThrow(new ObservacionNoEncontradaException(observacion)).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, observacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(estudiante.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"SOLUCIONADA\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ArtefactosCodes.Observacion.NO_ENCONTRADA));
    }
}
