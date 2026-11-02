package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.interactor.ConsultarEvaluacionAsesorInteractor;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionAsesorQuery;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorReadModel;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.exception.EvaluacionSinEvaluacionAsesorException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEvaluacionAsesorController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarEvaluacionAsesorControllerTest.TestSecurityConfig.class
})
class ConsultarEvaluacionAsesorControllerTest {

    private static final String RUTA = "/evaluaciones/{evaluacionId}/evaluacion-asesor";

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
    private ConsultarEvaluacionAsesorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeResponder200_cuandoExiste() throws Exception {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(ConsultarEvaluacionAsesorQuery.crear(evaluacion))).thenReturn(
                new EvaluacionAsesorReadModel(id,
                        new EvaluacionAsesorReadModel.Asesor(asesor, "Ana Gomez", "ana@uco.edu.co")));

        // Act & Assert
        mockMvc.perform(get(RUTA, evaluacion).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.asesor.id").value(asesor.toString()))
                .andExpect(jsonPath("$.asesor.nombre").value("Ana Gomez"))
                .andExpect(jsonPath("$.asesor.email").value("ana@uco.edu.co"));
    }

    @Test
    void debeResponder400_cuandoElIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, "no-es-uuid").with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder422_cuandoLaEvaluacionNoExiste() throws Exception {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(ConsultarEvaluacionAsesorQuery.crear(evaluacion)))
                .thenThrow(new EvaluacionNoEncontradaException(evaluacion));

        // Act & Assert
        mockMvc.perform(get(RUTA, evaluacion).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(EvaluacionesCodes.Evaluacion.NO_ENCONTRADA));
    }

    @Test
    void debeResponder422_cuandoNoTieneEvaluacionAsesor() throws Exception {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(ConsultarEvaluacionAsesorQuery.crear(evaluacion)))
                .thenThrow(new EvaluacionSinEvaluacionAsesorException(evaluacion));

        // Act & Assert
        mockMvc.perform(get(RUTA, evaluacion).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(EvaluacionesCodes.EvaluacionAsesor.SIN_EVALUACION_ASESOR));
    }
}
