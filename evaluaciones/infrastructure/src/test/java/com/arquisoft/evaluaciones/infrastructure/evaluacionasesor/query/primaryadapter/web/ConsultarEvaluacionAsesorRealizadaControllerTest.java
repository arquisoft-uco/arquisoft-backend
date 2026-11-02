package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.interactor.ConsultarEvaluacionAsesorRealizadaInteractor;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionAsesorRealizadaQuery;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorRealizadaReadModel;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoPerteneceAsesorException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilUUID;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEvaluacionAsesorRealizadaController.class)
@Import({
        IdentidadActualTestConfig.class,
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarEvaluacionAsesorRealizadaControllerTest.TestSecurityConfig.class
})
class ConsultarEvaluacionAsesorRealizadaControllerTest {

    private static final String RUTA = "/evaluaciones/evaluaciones-asesor/{evaluacionAsesorId}";

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
    private ConsultarEvaluacionAsesorRealizadaInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDelAsesor(String sub) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(sub));
    }

    @Test
    void debeResponder200_cuandoExisteYEsDelAsesor() throws Exception {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(ConsultarEvaluacionAsesorRealizadaQuery.crear(evaluacionAsesor, asesor)))
                .thenReturn(new EvaluacionAsesorRealizadaReadModel(evaluacionAsesor,
                        new EvaluacionAsesorRealizadaReadModel.Evaluacion(evaluacion,
                                new EvaluacionAsesorRealizadaReadModel.Entregable(entregable, "Robot seguidor", 2),
                                new EvaluacionAsesorRealizadaReadModel.Estado("EN_PROGRESO", "En progreso")),
                        new EvaluacionAsesorRealizadaReadModel.Asesor(asesor, "Ana Gomez", "ana@uco.edu.co")));

        // Act & Assert
        mockMvc.perform(get(RUTA, evaluacionAsesor).with(jwtDelAsesor(asesor.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(evaluacionAsesor.toString()))
                .andExpect(jsonPath("$.evaluacion.id").value(evaluacion.toString()))
                .andExpect(jsonPath("$.evaluacion.entregable.id").value(entregable.toString()))
                .andExpect(jsonPath("$.evaluacion.entregable.proyecto").value("Robot seguidor"))
                .andExpect(jsonPath("$.evaluacion.entregable.version").value(2))
                .andExpect(jsonPath("$.evaluacion.estado.id").value("EN_PROGRESO"))
                .andExpect(jsonPath("$.evaluacion.estado.nombre").value("En progreso"))
                .andExpect(jsonPath("$.asesor.id").value(asesor.toString()))
                .andExpect(jsonPath("$.asesor.nombre").value("Ana Gomez"))
                .andExpect(jsonPath("$.asesor.email").value("ana@uco.edu.co"));
    }

    @Test
    void debeResponder400_cuandoElIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, "no-es-uuid").with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder422_cuandoNoExiste() throws Exception {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any()))
                .thenThrow(new EvaluacionAsesorNoEncontradaException(evaluacionAsesor));

        // Act & Assert
        mockMvc.perform(get(RUTA, evaluacionAsesor).with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_ASESOR_NO_ENCONTRADA));
    }

    @Test
    void debeResponder422_cuandoEsDeOtroAsesor() throws Exception {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any()))
                .thenThrow(new EvaluacionAsesorNoPerteneceAsesorException(evaluacionAsesor));

        // Act & Assert
        mockMvc.perform(get(RUTA, evaluacionAsesor).with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EvaluacionesCodes.EvaluacionCualitativaAsesor.NO_PERTENECE_ASESOR));
    }
}
