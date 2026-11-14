package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.interactor.RegistrarObservacionItemAsesorInteractor;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model.RegistrarObservacionItemAsesorCommand;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionFinalizadaException;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.DescripcionObservacionItemAsesorDuplicadaException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrarObservacionItemAsesorController.class)
@Import({
        IdentidadActualTestConfig.class,
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        RegistrarObservacionItemAsesorControllerTest.TestSecurityConfig.class
})
class RegistrarObservacionItemAsesorControllerTest {

    private static final UUID EVALUACION_CUANTITATIVA_ASESOR = UtilUUID.generarNuevoUUID();
    private static final String RUTA =
            "/evaluaciones/evaluaciones-cuantitativas-asesor/" + EVALUACION_CUANTITATIVA_ASESOR + "/observaciones";
    private static final String BODY_VALIDO = """
            { "descripcion": "Sustenta el puntaje otorgado" }
            """;

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
    private RegistrarObservacionItemAsesorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDelAsesor(String sub) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(sub));
    }

    @Test
    void debeRetornar201ConId_yUsarElAsesorDeLaIdentidad() throws Exception {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        var captor = ArgumentCaptor.forClass(RegistrarObservacionItemAsesorCommand.class);
        when(interactor.ejecutar(any())).thenReturn(id);

        // Act
        mockMvc.perform(post(RUTA)
                        .with(jwtDelAsesor(asesor.toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()));

        // Assert
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().asesor()).isEqualTo(asesor);
        assertThat(captor.getValue().evaluacionCuantitativaAsesor()).isEqualTo(EVALUACION_CUANTITATIVA_ASESOR);
        assertThat(captor.getValue().descripcion()).isEqualTo("Sustenta el puntaje otorgado");
    }

    @Test
    void debeRetornar400_cuandoDescripcionEnBlanco() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"descripcion\": \" \" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1));
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar422_cuandoLaDescripcionYaExiste() throws Exception {
        // Arrange
        doThrow(new DescripcionObservacionItemAsesorDuplicadaException(EVALUACION_CUANTITATIVA_ASESOR))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EvaluacionesCodes.ObservacionItemAsesor.DESCRIPCION_DUPLICADA));
    }

    @Test
    void debeRetornar422_cuandoLaEvaluacionEstaFinalizada() throws Exception {
        // Arrange
        doThrow(new EvaluacionFinalizadaException()).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(EvaluacionesCodes.Evaluacion.ESTADO_FINALIZADA));
    }
}
