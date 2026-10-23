package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.interactor.RegistrarEvaluacionesCualitativasAsesorInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.RegistrarEvaluacionesCualitativasAsesorCommand;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoPerteneceAsesorException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrarEvaluacionesCualitativasAsesorController.class)
@Import({
        IdentidadActualTestConfig.class,
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        RegistrarEvaluacionesCualitativasAsesorControllerTest.TestSecurityConfig.class
})
class RegistrarEvaluacionesCualitativasAsesorControllerTest {

    private static final UUID EVALUACION_ASESOR_ID = UtilUUID.generarNuevoUUID();
    private static final String RUTA = "/evaluaciones-asesor/" + EVALUACION_ASESOR_ID + "/cualitativas";

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
    private RegistrarEvaluacionesCualitativasAsesorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDelAsesor(String sub) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(sub));
    }

    private static String bodyValido(UUID item, UUID criterio) {
        return """
                { "evaluaciones": [ { "item": "%s", "criterio": "%s" } ] }
                """.formatted(item, criterio);
    }

    @Test
    void debeResponder201_cuandoLoteValido() throws Exception {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        var captor = ArgumentCaptor.forClass(RegistrarEvaluacionesCualitativasAsesorCommand.class);

        // Act
        mockMvc.perform(post(RUTA)
                        .with(jwtDelAsesor(asesor.toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido(item, criterio)))
                .andExpect(status().isCreated())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());

        // Assert
        verify(interactor).ejecutar(captor.capture());
        var command = captor.getValue();
        assertThat(command.evaluacionAsesor()).isEqualTo(EVALUACION_ASESOR_ID);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.evaluaciones()).hasSize(1);
        assertThat(command.evaluaciones().get(0).item()).isEqualTo(item);
        assertThat(command.evaluaciones().get(0).criterio()).isEqualTo(criterio);
    }

    @Test
    void debeResponder400_cuandoLoteVacio() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"evaluaciones\": [] }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("evaluaciones"));
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder400_cuandoItemInvalido() throws Exception {
        // Arrange
        var body = """
                { "evaluaciones": [ { "item": "no-es-uuid", "criterio": "%s" } ] }
                """.formatted(UtilUUID.generarNuevoUUID());

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("evaluaciones[0].item"));
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_sinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder422_cuandoNoPerteneceAlAsesor() throws Exception {
        // Arrange
        doThrow(new EvaluacionAsesorNoPerteneceAsesorException(EVALUACION_ASESOR_ID))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EvaluacionesCodes.EvaluacionCualitativaAsesor.NO_PERTENECE_ASESOR));
    }

    @Test
    void debeResponder422_cuandoEvaluacionFinalizada() throws Exception {
        // Arrange
        doThrow(new EvaluacionFinalizadaException()).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(EvaluacionesCodes.Evaluacion.ESTADO_FINALIZADA));
    }
}
