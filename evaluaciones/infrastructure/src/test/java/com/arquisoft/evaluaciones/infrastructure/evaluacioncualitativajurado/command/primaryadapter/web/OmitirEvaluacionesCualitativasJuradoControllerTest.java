package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.interactor.OmitirEvaluacionesCualitativasJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.OmitirEvaluacionesCualitativasJuradoCommand;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

@WebMvcTest(OmitirEvaluacionesCualitativasJuradoController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        OmitirEvaluacionesCualitativasJuradoControllerTest.TestSecurityConfig.class
})
class OmitirEvaluacionesCualitativasJuradoControllerTest {

    private static final UUID EVALUACION_JURADO_ID = UUID.randomUUID();
    private static final String RUTA = "/evaluaciones/evaluaciones-jurado/" + EVALUACION_JURADO_ID + "/cualitativas";
    private static final UUID EVALUACION_A = UUID.randomUUID();
    private static final UUID EVALUACION_B = UUID.randomUUID();
    private static final String BODY_VALIDO = """
            { "evaluaciones": ["%s", "%s"] }
            """.formatted(EVALUACION_A, EVALUACION_B);

    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity(prePostEnabled = true)
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
    private OmitirEvaluacionesCualitativasJuradoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar204SinCuerpo_cuandoElDeleteLlevaElCuerpoValido() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isNoContent())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());

        var captor = ArgumentCaptor.forClass(OmitirEvaluacionesCualitativasJuradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().evaluacionJurado()).isEqualTo(EVALUACION_JURADO_ID);
        assertThat(captor.getValue().evaluaciones()).containsExactly(EVALUACION_A, EVALUACION_B);
    }

    @Test
    void debeRetornar400ConCampoIndexado_cuandoUnIdDelCuerpoNoEsUuid() throws Exception {
        // Arrange
        var body = """
                { "evaluaciones": ["no-es-un-uuid"] }
                """;

        // Act & Assert
        mockMvc.perform(delete(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("evaluaciones[0]"));
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar400LoteVacio_cuandoElCuerpoNoTraeEvaluacionesOLaListaEstaVacia() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("evaluaciones"));
        mockMvc.perform(delete(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"evaluaciones\": [] }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("evaluaciones"));
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar401_cuandoNoEstaAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar403_cuandoNoTieneElClientRoleDeOmision() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(
                                        EvaluacionesAuthorities.EVALUACION_CUALITATIVA_JURADO_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isForbidden());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoLaEvaluacionDeJuradoEstaFinalizada() throws Exception {
        // Arrange
        doThrow(new EvaluacionJuradoFinalizadaException(EVALUACION_JURADO_ID)).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity());
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermiso() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(
                        EvaluacionesAuthorities.EVALUACION_CUALITATIVA_JURADO_DELETE));
    }
}
