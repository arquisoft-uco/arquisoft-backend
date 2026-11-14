package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.interactor.CambiarCriterioEvaluacionCualitativaAsesorInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.CambiarCriterioEvaluacionCualitativaAsesorCommand;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.CambioCriterioEvaluacionCualitativaAsesorFinalizadaException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CambiarCriterioEvaluacionCualitativaAsesorController.class)
@Import({
        IdentidadActualTestConfig.class,
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        CambiarCriterioEvaluacionCualitativaAsesorControllerTest.TestSecurityConfig.class
})
class CambiarCriterioEvaluacionCualitativaAsesorControllerTest {

    private static final UUID EVALUACION_CUALITATIVA_ID = UtilUUID.generarNuevoUUID();
    private static final String RUTA =
            "/evaluaciones/evaluaciones-cualitativas-asesor/" + EVALUACION_CUALITATIVA_ID + "/criterio";

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
    private CambiarCriterioEvaluacionCualitativaAsesorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDelAsesor(String sub) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(sub));
    }

    private static String cuerpo(String criterio) {
        return "{ \"criterio\": \"" + criterio + "\" }";
    }

    @Test
    void debeResponder204_cuandoEsValido() throws Exception {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        var captor = ArgumentCaptor.forClass(CambiarCriterioEvaluacionCualitativaAsesorCommand.class);

        // Act
        mockMvc.perform(patch(RUTA)
                        .with(jwtDelAsesor(asesor.toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(criterio.toString())))
                .andExpect(status().isNoContent())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());

        // Assert
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().evaluacionCualitativaAsesor()).isEqualTo(EVALUACION_CUALITATIVA_ID);
        assertThat(captor.getValue().asesor()).isEqualTo(asesor);
        assertThat(captor.getValue().criterio()).isEqualTo(criterio);
    }

    @Test
    void debeResponder400_cuandoCriterioInvalido() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo("no-es-un-uuid")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors[0].field")
                        .value(EvaluacionesFields.EvaluacionCualitativaAsesor.CRITERIO));
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder422_cuandoReglaFalla() throws Exception {
        // Arrange
        doThrow(new CambioCriterioEvaluacionCualitativaAsesorFinalizadaException(UtilUUID.generarNuevoUUID()))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(
                        EvaluacionesCodes.EvaluacionCualitativaAsesor.CAMBIO_CRITERIO_EVALUACION_ASESOR_FINALIZADA));
    }
}
