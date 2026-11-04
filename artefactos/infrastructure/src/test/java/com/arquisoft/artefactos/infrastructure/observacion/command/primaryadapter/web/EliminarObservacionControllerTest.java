package com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web;

import com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.EliminarObservacionInteractor;
import com.arquisoft.artefactos.application.observacion.command.primaryport.model.EliminarObservacionCommand;
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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EliminarObservacionController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, EliminarObservacionControllerTest.TestSecurityConfig.class})
class EliminarObservacionControllerTest {

    private static final String RUTA = "/artefactos/observaciones/{observacionId}";
    private static final UUID LLAMANTE_ID = UtilUUID.generarNuevoUUID();

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
    private EliminarObservacionInteractor eliminarObservacionInteractor;

    private static RequestPostProcessor tokenAsesor() {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(LLAMANTE_ID.toString()));
    }

    @Test
    void debeRetornar204_yDelegarConRutaEIdentidad_cuandoEsValido() throws Exception {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA, observacion).with(tokenAsesor()))
                .andExpect(status().isNoContent());

        var captor = ArgumentCaptor.forClass(EliminarObservacionCommand.class);
        verify(eliminarObservacionInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().observacion()).isEqualTo(observacion);
        assertThat(captor.getValue().asesor()).isEqualTo(LLAMANTE_ID);
    }

    @Test
    void debeRetornar400_yNoInvocarElInteractor_cuandoObservacionIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, "no-es-uuid").with(tokenAsesor()))
                .andExpect(status().isBadRequest());
        verify(eliminarObservacionInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoElInteractorLanzaUnaExcepcionDeDominio() throws Exception {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        doThrow(new ObservacionNoEncontradaException(observacion))
                .when(eliminarObservacionInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, observacion).with(tokenAsesor()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ArtefactosCodes.Observacion.NO_ENCONTRADA));
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
        verify(eliminarObservacionInteractor, never()).ejecutar(any());
    }
}
