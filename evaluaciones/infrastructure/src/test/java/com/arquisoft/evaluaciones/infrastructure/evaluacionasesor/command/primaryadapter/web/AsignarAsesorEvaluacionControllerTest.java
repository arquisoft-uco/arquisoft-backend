package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.primaryport.interactor.AsignarAsesorEvaluacionInteractor;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.primaryport.model.AsignarAsesorEvaluacionCommand;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.exception.EvaluacionAsesorConEvaluacionesException;
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

@WebMvcTest(AsignarAsesorEvaluacionController.class)
@Import({
        IdentidadActualTestConfig.class,
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        AsignarAsesorEvaluacionControllerTest.TestSecurityConfig.class
})
class AsignarAsesorEvaluacionControllerTest {

    private static final UUID EVALUACION_ID = UtilUUID.generarNuevoUUID();
    private static final String RUTA = "/evaluaciones/" + EVALUACION_ID + "/asesor";

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
    private AsignarAsesorEvaluacionInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDelCoordinador() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(UtilUUID.generarNuevoUUID().toString()));
    }

    @Test
    void debeResponder204_cuandoLaSolicitudEsValida() throws Exception {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var captor = ArgumentCaptor.forClass(AsignarAsesorEvaluacionCommand.class);

        // Act
        mockMvc.perform(patch(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"asesor\":\"" + asesor + "\"}")
                        .with(jwtDelCoordinador()))
                .andExpect(status().isNoContent())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());

        // Assert
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().evaluacion()).isEqualTo(EVALUACION_ID);
        assertThat(captor.getValue().asesor()).isEqualTo(asesor);
    }

    @Test
    void debeResponder400ConFieldErrors_cuandoAsesorAusenteOInvalido() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .with(jwtDelCoordinador()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors[0].field").value(EvaluacionesFields.EvaluacionAsesor.ASESOR));
        mockMvc.perform(patch(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"asesor\":\"no-es-uuid\"}")
                        .with(jwtDelCoordinador()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value(EvaluacionesFields.EvaluacionAsesor.ASESOR));
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"asesor\":\"" + UtilUUID.generarNuevoUUID() + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder422_cuandoElInteractorLanzaDomainException() throws Exception {
        // Arrange
        doThrow(new EvaluacionAsesorConEvaluacionesException(EVALUACION_ID, UtilUUID.generarNuevoUUID()))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"asesor\":\"" + UtilUUID.generarNuevoUUID() + "\"}")
                        .with(jwtDelCoordinador()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(EvaluacionesCodes.EvaluacionAsesor.CON_EVALUACIONES));
    }
}
