package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.primaryadapter.web;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.interactor.RemoverObservacionEvaluacionInteractor;
import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.RemoverObservacionEvaluacionCommand;
import com.arquisoft.fichas.domain.observacionevaluacion.exception.ObservacionEvaluacionNoEncontradaException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RemoverObservacionEvaluacionController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        RemoverObservacionEvaluacionControllerTest.TestSecurityConfig.class})
class RemoverObservacionEvaluacionControllerTest {

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

    private static final String RUTA = "/fichas-perfil/observaciones-evaluacion/{observacionEvaluacionId}";
    private static final UUID OBSERVACION_EVALUACION_ID = UtilUUID.generarNuevoUUID();
    private static final UUID REPRESENTANTE_COMITE_ID = UtilUUID.generarNuevoUUID();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RemoverObservacionEvaluacionInteractor removerObservacionEvaluacionInteractor;

    @Test
    void debeRetornar204SinCuerpoConActorDelJwt_cuandoRemocionExitosa() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, OBSERVACION_EVALUACION_ID)
                        .with(jwtConAuthority(REPRESENTANTE_COMITE_ID.toString())))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(removerObservacionEvaluacionInteractor).ejecutar(
                new RemoverObservacionEvaluacionCommand(OBSERVACION_EVALUACION_ID, REPRESENTANTE_COMITE_ID));
    }

    @Test
    void debeRetornar400_cuandoPathNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, "no-es-uuid")
                        .with(jwtConAuthority(REPRESENTANTE_COMITE_ID.toString())))
                .andExpect(status().isBadRequest());

        verify(removerObservacionEvaluacionInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar401ConFieldError_cuandoSubjectDelJwtNoEsUuid() throws Exception {
        // Act & Assert — Command.crear rechaza el actor nulo antes de llegar al interactor
        mockMvc.perform(delete(RUTA, OBSERVACION_EVALUACION_ID)
                        .with(jwtConAuthority("no-es-uuid")))
                .andExpect(status().isUnauthorized());

        verify(removerObservacionEvaluacionInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, OBSERVACION_EVALUACION_ID))
                .andExpect(status().isUnauthorized());

        verify(removerObservacionEvaluacionInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoLaObservacionNoExiste() throws Exception {
        // Arrange
        doThrow(new ObservacionEvaluacionNoEncontradaException(OBSERVACION_EVALUACION_ID))
                .when(removerObservacionEvaluacionInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, OBSERVACION_EVALUACION_ID)
                        .with(jwtConAuthority(REPRESENTANTE_COMITE_ID.toString())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(FichasCodes.ObservacionEvaluacion.OBSERVACION_EVALUACION_NO_ENCONTRADA));
    }

    private RequestPostProcessor jwtConAuthority(String subject) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject(subject));
    }
}
