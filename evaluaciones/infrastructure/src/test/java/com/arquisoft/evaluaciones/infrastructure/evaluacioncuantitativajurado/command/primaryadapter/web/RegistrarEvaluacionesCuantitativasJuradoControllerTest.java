package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.interactor.RegistrarEvaluacionesCuantitativasJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.RegistrarEvaluacionesCuantitativasJuradoCommand;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoEncontradaException;
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
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrarEvaluacionesCuantitativasJuradoController.class)
@Import({IdentidadActualTestConfig.class,
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        RegistrarEvaluacionesCuantitativasJuradoControllerTest.TestSecurityConfig.class
})
class RegistrarEvaluacionesCuantitativasJuradoControllerTest {

    private static final UUID EVALUACION_JURADO_ID = UtilUUID.generarNuevoUUID();
    private static final UUID ITEM_ID = UtilUUID.generarNuevoUUID();
    private static final String RUTA = "/evaluaciones/evaluaciones-jurado/" + EVALUACION_JURADO_ID + "/cuantitativas";
    private static final String BODY_VALIDO = """
            { "evaluaciones": [ { "item": "%s", "puntaje": 250 } ] }
            """.formatted(ITEM_ID);

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
    private RegistrarEvaluacionesCuantitativasJuradoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar201SinCuerpoYEnviarLaIdentidadComoJurado_cuandoPeticionEsValida() throws Exception {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtDe(jurado))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());

        var captor = ArgumentCaptor.forClass(RegistrarEvaluacionesCuantitativasJuradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().evaluacionJurado()).isEqualTo(EVALUACION_JURADO_ID);
        assertThat(captor.getValue().jurado()).isEqualTo(jurado);
        assertThat(captor.getValue().evaluaciones()).containsExactly(
                new RegistrarEvaluacionesCuantitativasJuradoCommand.Par(ITEM_ID, 250));
    }

    @Test
    void debeRetornar400LoteVacio_cuandoElLoteNoLlega() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtDe(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("evaluaciones"));
    }

    @Test
    void debeRetornar400ConCamposIndexados_cuandoUnParEsNulo() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtDe(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"evaluaciones\": [null] }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(2))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("evaluaciones[0].item"))
                .andExpect(jsonPath("$.fieldErrors[1].field").value("evaluaciones[0].puntaje"));
    }

    @Test
    void debeRetornar401_cuandoNoEstaAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar422_cuandoLaEvaluacionDeJuradoNoExiste() throws Exception {
        // Arrange
        doThrow(new EvaluacionJuradoNoEncontradaException(EVALUACION_JURADO_ID))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtDe(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity());
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDe(UUID subject) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(subject.toString()));
    }
}
