package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.interactor.RemoverJuradosEvaluacionInteractor;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.model.RemoverJuradosEvaluacionCommand;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosConEvaluacionesException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
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

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RemoverJuradosEvaluacionController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        RemoverJuradosEvaluacionControllerTest.TestSecurityConfig.class
})
class RemoverJuradosEvaluacionControllerTest {

    private static final UUID EVALUACION = UtilUUID.generarNuevoUUID();
    private static final UUID JURADO = UtilUUID.generarNuevoUUID();
    private static final String RUTA = "/evaluaciones/" + EVALUACION + "/jurados";
    private static final String BODY_VALIDO = """
            { "jurados": ["%s"] }
            """.formatted(JURADO);

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
    private RemoverJuradosEvaluacionInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar204SinCuerpo_cuandoPeticionEsValida() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isNoContent());
        var captor = ArgumentCaptor.forClass(RemoverJuradosEvaluacionCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().evaluacion()).isEqualTo(EVALUACION);
        assertThat(captor.getValue().jurados()).containsExactly(JURADO);
    }

    @Test
    void debeRetornar400LoteVacio_cuandoLosJuradosNoLlegan() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("jurados"));
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar400ConCampoIndexado_cuandoUnJuradoNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "jurados": ["%s", "no-es-un-uuid"] }
                                """.formatted(JURADO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("jurados[1]"));
    }

    @Test
    void debeRetornar401_cuandoNoEstaAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar422_cuandoElInteractorLanzaUnaReglaDeDominio() throws Exception {
        // Arrange
        doThrow(new JuradosConEvaluacionesException(Set.of(JURADO)))
                .when(interactor).ejecutar(any(RemoverJuradosEvaluacionCommand.class));

        // Act & Assert
        mockMvc.perform(delete(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(EvaluacionesCodes.EvaluacionJurado.JURADOS_CON_EVALUACIONES));
    }
}
