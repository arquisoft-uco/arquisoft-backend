package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.interactor.RemoverObservacionItemJuradoInteractor;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.RemoverObservacionItemJuradoCommand;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.ObservacionItemJuradoNoEncontradaException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RemoverObservacionItemJuradoController.class)
@Import({
        AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class,
        RemoverObservacionItemJuradoControllerTest.TestSecurityConfig.class
})
class RemoverObservacionItemJuradoControllerTest {

    private static final String RUTA = "/evaluaciones/observaciones-item-jurado/{observacionId}";

    @TestConfiguration
    @EnableWebSecurity
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
    private RemoverObservacionItemJuradoInteractor interactor;

    @Test
    void debeRetornar204SinCuerpoYDelegarCommand_cuandoPeticionEsValida() throws Exception {
        // Arrange
        var observacionId = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA, observacionId).with(jwtConPermiso()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(interactor).ejecutar(RemoverObservacionItemJuradoCommand.crear(observacionId));
    }

    @Test
    void debeRetornar400YNoInvocarInteractor_cuandoObservacionIdNoEsUuid() throws Exception {
        // Arrange
        var idInvalido = "no-es-un-uuid";

        // Act & Assert
        mockMvc.perform(delete(RUTA, idInvalido).with(jwtConPermiso()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar401YNoInvocarInteractor_cuandoNoEstaAutenticado() throws Exception {
        // Arrange
        var observacionId = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA, observacionId))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoLaObservacionNoExiste() throws Exception {
        // Arrange
        var observacionId = UtilUUID.generarNuevoUUID();
        doThrow(new ObservacionItemJuradoNoEncontradaException(observacionId))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, observacionId).with(jwtConPermiso()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(EvaluacionesCodes.ObservacionItemJurado.NO_ENCONTRADA));
    }

    @Test
    void debeRetornar422_cuandoLaEvaluacionJuradoEstaFinalizada() throws Exception {
        // Arrange
        var observacionId = UtilUUID.generarNuevoUUID();
        doThrow(new EvaluacionJuradoFinalizadaException(UtilUUID.generarNuevoUUID()))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, observacionId).with(jwtConPermiso()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EvaluacionesCodes.ObservacionItemJurado.EVALUACION_JURADO_FINALIZADA));
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermiso() {
        return SecurityMockMvcRequestPostProcessors.jwt();
    }
}
