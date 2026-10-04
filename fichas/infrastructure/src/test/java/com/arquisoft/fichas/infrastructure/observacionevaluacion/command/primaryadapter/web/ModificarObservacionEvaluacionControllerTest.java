package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.primaryadapter.web;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.interactor.ModificarObservacionEvaluacionInteractor;
import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.ModificarObservacionEvaluacionCommand;
import com.arquisoft.fichas.domain.observacionevaluacion.exception.ObservacionEvaluacionNoEncontradaException;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModificarObservacionEvaluacionController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ModificarObservacionEvaluacionControllerTest.TestSecurityConfig.class})
class ModificarObservacionEvaluacionControllerTest {

    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity(prePostEnabled = true)
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
    private static final String BODY_VALIDO = """
            {"observacion": "Falta delimitar el alcance"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModificarObservacionEvaluacionInteractor modificarObservacionEvaluacionInteractor;

    @Test
    void debe204SinCuerpoYActorDelJwt_cuandoPeticionValida() throws Exception {
        // Act & Assert
        mockMvc.perform(peticionAutorizada("""
                        {"observacion": "  Falta delimitar el alcance  "}
                        """))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        var captor = ArgumentCaptor.forClass(ModificarObservacionEvaluacionCommand.class);
        verify(modificarObservacionEvaluacionInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new ModificarObservacionEvaluacionCommand(
                OBSERVACION_EVALUACION_ID, "Falta delimitar el alcance", REPRESENTANTE_COMITE_ID));
    }

    @Test
    void debe400_cuandoObservacionEnBlanco() throws Exception {
        // Act & Assert — Command.crear rechaza antes de llegar al interactor
        mockMvc.perform(peticionAutorizada("""
                        {"observacion": "   "}
                        """))
                .andExpect(status().isBadRequest());

        verify(modificarObservacionEvaluacionInteractor, never()).ejecutar(any());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, OBSERVACION_EVALUACION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoAuthorityDeAgregarEnVezDeModificar() throws Exception {
        // Act & Assert — el client role de agregar observaciones no habilita modificarlas
        mockMvc.perform(patch(RUTA, OBSERVACION_EVALUACION_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(REPRESENTANTE_COMITE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_EVALUACION_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isForbidden());

        verify(modificarObservacionEvaluacionInteractor, never()).ejecutar(any());
    }

    @Test
    void debe422_cuandoLaObservacionNoExiste() throws Exception {
        // Arrange
        doThrow(new ObservacionEvaluacionNoEncontradaException(OBSERVACION_EVALUACION_ID))
                .when(modificarObservacionEvaluacionInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(peticionAutorizada(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(FichasCodes.ObservacionEvaluacion.OBSERVACION_EVALUACION_NO_ENCONTRADA));
    }

    private MockHttpServletRequestBuilder peticionAutorizada(String body) {
        return patch(RUTA, OBSERVACION_EVALUACION_ID)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(j -> j.subject(REPRESENTANTE_COMITE_ID.toString()))
                        .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_EVALUACION_UPDATE)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }
}
