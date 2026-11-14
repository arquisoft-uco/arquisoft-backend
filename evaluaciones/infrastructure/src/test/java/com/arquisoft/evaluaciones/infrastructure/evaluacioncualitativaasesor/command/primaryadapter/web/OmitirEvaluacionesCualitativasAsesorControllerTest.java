package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.interactor.OmitirEvaluacionesCualitativasAsesorInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.OmitirEvaluacionesCualitativasAsesorCommand;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.OmisionEvaluacionCualitativaAsesorFinalizadaException;
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

@WebMvcTest(OmitirEvaluacionesCualitativasAsesorController.class)
@Import({
        IdentidadActualTestConfig.class,
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        OmitirEvaluacionesCualitativasAsesorControllerTest.TestSecurityConfig.class
})
class OmitirEvaluacionesCualitativasAsesorControllerTest {

    private static final UUID EVALUACION_ASESOR_ID = UtilUUID.generarNuevoUUID();
    private static final String RUTA = "/evaluaciones/evaluaciones-asesor/" + EVALUACION_ASESOR_ID + "/cualitativas";

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
    private OmitirEvaluacionesCualitativasAsesorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDelAsesor(String sub) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(sub));
    }

    @Test
    void debeResponder204_cuandoItemsRepetidosEnQuery() throws Exception {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var item1 = UtilUUID.generarNuevoUUID();
        var item2 = UtilUUID.generarNuevoUUID();
        var captor = ArgumentCaptor.forClass(OmitirEvaluacionesCualitativasAsesorCommand.class);

        // Act
        mockMvc.perform(delete(RUTA)
                        .param("item", item1.toString(), item2.toString())
                        .with(jwtDelAsesor(asesor.toString())))
                .andExpect(status().isNoContent())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());

        // Assert
        verify(interactor).ejecutar(captor.capture());
        var command = captor.getValue();
        assertThat(command.evaluacionAsesor()).isEqualTo(EVALUACION_ASESOR_ID);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.items()).containsExactly(item1, item2);
    }

    @Test
    void debeResponder400_cuandoSinItem() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA)
                        .with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors[0].field")
                        .value(EvaluacionesFields.OmisionEvaluacionesCualitativasAsesor.ITEMS));
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder400ConFieldErrors_cuandoItemNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA)
                        .param("item", UtilUUID.generarNuevoUUID().toString(), "no-es-un-uuid")
                        .with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors[0].field")
                        .value(EvaluacionesFields.OmisionEvaluacionesCualitativasAsesor.ITEMS + "[1]"));
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA).param("item", UtilUUID.generarNuevoUUID().toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder422_cuandoInteractorLanzaDomainException() throws Exception {
        // Arrange
        doThrow(new OmisionEvaluacionCualitativaAsesorFinalizadaException(EVALUACION_ASESOR_ID))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA)
                        .param("item", UtilUUID.generarNuevoUUID().toString())
                        .with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EvaluacionesCodes.EvaluacionCualitativaAsesor.OMISION_EVALUACION_ASESOR_FINALIZADA));
    }
}
