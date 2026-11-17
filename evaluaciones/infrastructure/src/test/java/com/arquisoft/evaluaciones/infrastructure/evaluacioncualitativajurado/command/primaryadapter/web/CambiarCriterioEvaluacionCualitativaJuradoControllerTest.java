package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.interactor.CambiarCriterioEvaluacionCualitativaJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.CambiarCriterioEvaluacionCualitativaJuradoCommand;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.CriterioEvaluacionCualitativaJuradoSinCambioException;
import com.arquisoft.shared.logger.AppLoggerConfig;
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

@WebMvcTest(CambiarCriterioEvaluacionCualitativaJuradoController.class)
@Import({
        IdentidadActualTestConfig.class,
        AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        CambiarCriterioEvaluacionCualitativaJuradoControllerTest.TestSecurityConfig.class
})
class CambiarCriterioEvaluacionCualitativaJuradoControllerTest {

    private static final UUID EVALUACION_ID = UtilUUID.generarNuevoUUID();
    private static final UUID JURADO_ID = UtilUUID.generarNuevoUUID();
    private static final UUID CRITERIO_ID = UtilUUID.generarNuevoUUID();
    private static final String RUTA = "/evaluaciones/evaluaciones-cualitativas-jurado/" + EVALUACION_ID + "/criterio";
    private static final String BODY_VALIDO = """
            { "criterio": "%s" }
            """.formatted(CRITERIO_ID);

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
    private CambiarCriterioEvaluacionCualitativaJuradoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDelJurado() {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(token -> token.subject(JURADO_ID.toString()));
    }

    @Test
    void debeResponder204SinCuerpo_cuandoElCambioEsExitoso() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .with(jwtDelJurado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isNoContent())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());

        var captor = ArgumentCaptor.forClass(CambiarCriterioEvaluacionCualitativaJuradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().evaluacionCualitativaJurado()).isEqualTo(EVALUACION_ID);
        assertThat(captor.getValue().criterio()).isEqualTo(CRITERIO_ID);
        assertThat(captor.getValue().jurado()).isEqualTo(JURADO_ID);
    }

    @Test
    void debeResponder400ConElCampo_cuandoElCriterioEstaEnBlancoOEsInvalido() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .with(jwtDelJurado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("criterio"));
        mockMvc.perform(patch(RUTA)
                        .with(jwtDelJurado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"criterio\": \"no-es-un-uuid\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("criterio"));
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder422_cuandoUnaReglaDeDominioRechaza() throws Exception {
        // Arrange
        doThrow(new CriterioEvaluacionCualitativaJuradoSinCambioException(EVALUACION_ID, CRITERIO_ID))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .with(jwtDelJurado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity());
    }
}
