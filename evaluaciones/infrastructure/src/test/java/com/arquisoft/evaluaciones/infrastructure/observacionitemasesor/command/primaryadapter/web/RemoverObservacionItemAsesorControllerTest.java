package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.interactor.RemoverObservacionItemAsesorInteractor;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model.RemoverObservacionItemAsesorCommand;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.RemocionObservacionItemAsesorFinalizadaException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RemoverObservacionItemAsesorController.class)
@Import({
        IdentidadActualTestConfig.class,
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        RemoverObservacionItemAsesorControllerTest.TestSecurityConfig.class
})
class RemoverObservacionItemAsesorControllerTest {

    private static final UUID OBSERVACION_ID = UtilUUID.generarNuevoUUID();
    private static final String RUTA = "/evaluaciones/observaciones-item-asesor/" + OBSERVACION_ID;

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
    private RemoverObservacionItemAsesorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDelAsesor(String sub) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(sub));
    }

    @Test
    void debeResponder204_cuandoRemueve() throws Exception {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var captor = ArgumentCaptor.forClass(RemoverObservacionItemAsesorCommand.class);

        // Act
        mockMvc.perform(delete(RUTA).with(jwtDelAsesor(asesor.toString())))
                .andExpect(status().isNoContent())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());

        // Assert
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().observacion()).isEqualTo(OBSERVACION_ID);
        assertThat(captor.getValue().asesor()).isEqualTo(asesor);
    }

    @Test
    void debeResponder400_cuandoIdInvalido() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/evaluaciones/observaciones-item-asesor/no-es-uuid")
                        .with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA)).andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder422_cuandoReglaLanza() throws Exception {
        // Arrange
        doThrow(new RemocionObservacionItemAsesorFinalizadaException(UtilUUID.generarNuevoUUID()))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA).with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EvaluacionesCodes.ObservacionItemAsesor.REMOCION_EVALUACION_FINALIZADA));
    }
}
