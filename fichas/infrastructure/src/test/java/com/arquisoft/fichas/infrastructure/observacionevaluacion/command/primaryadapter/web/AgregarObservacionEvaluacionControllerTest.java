package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.primaryadapter.web;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.interactor.AgregarObservacionEvaluacionInteractor;
import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.AgregarObservacionEvaluacionCommand;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.domain.observacionevaluacion.exception.EvaluacionFichaCerradaException;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgregarObservacionEvaluacionController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        AgregarObservacionEvaluacionControllerTest.TestSecurityConfig.class})
class AgregarObservacionEvaluacionControllerTest {

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

    private static final String RUTA = "/fichas-perfil/evaluaciones/{evaluacionFichaPerfilId}/observaciones";
    private static final UUID EVALUACION_FICHA_PERFIL_ID = UtilUUID.generarNuevoUUID();
    private static final UUID REPRESENTANTE_COMITE_ID = UtilUUID.generarNuevoUUID();
    private static final String BODY_VALIDO = """
            {"observacion": "El marco teórico es insuficiente"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgregarObservacionEvaluacionInteractor agregarObservacionEvaluacionInteractor;

    @Test
    void debe201ConIdYActorDelJwt_cuandoPeticionValida() throws Exception {
        // Arrange
        var observacionEvaluacionId = UtilUUID.generarNuevoUUID();
        when(agregarObservacionEvaluacionInteractor.ejecutar(any())).thenReturn(observacionEvaluacionId);

        // Act & Assert
        mockMvc.perform(peticionAutorizada("""
                        {"observacion": "  El marco teórico es insuficiente  "}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(observacionEvaluacionId.toString()));

        var captor = ArgumentCaptor.forClass(AgregarObservacionEvaluacionCommand.class);
        verify(agregarObservacionEvaluacionInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new AgregarObservacionEvaluacionCommand(
                EVALUACION_FICHA_PERFIL_ID, "El marco teórico es insuficiente", REPRESENTANTE_COMITE_ID));
    }

    @Test
    void debe400_cuandoObservacionEnBlanco() throws Exception {
        // Act & Assert — Command.crear rechaza antes de llegar al interactor
        mockMvc.perform(peticionAutorizada("""
                        {"observacion": "   "}
                        """))
                .andExpect(status().isBadRequest());

        verify(agregarObservacionEvaluacionInteractor, never()).ejecutar(any());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, EVALUACION_FICHA_PERFIL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoAuthorityDeOtroEndpoint() throws Exception {
        // Act & Assert — el client role de observar un ítem no habilita observar una evaluación
        mockMvc.perform(post(RUTA, EVALUACION_FICHA_PERFIL_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(REPRESENTANTE_COMITE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_ITEM_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe422_cuandoLaEvaluacionNoEsPropia() throws Exception {
        // Arrange
        when(agregarObservacionEvaluacionInteractor.ejecutar(any()))
                .thenThrow(new EvaluacionFichaNoPropiaException(EVALUACION_FICHA_PERFIL_ID));

        // Act & Assert
        mockMvc.perform(peticionAutorizada(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.EstadoEvaluacionFicha.EVALUACION_NO_PROPIA));
    }

    @Test
    void debe422_cuandoLaEvaluacionEstaCerrada() throws Exception {
        // Arrange
        when(agregarObservacionEvaluacionInteractor.ejecutar(any()))
                .thenThrow(new EvaluacionFichaCerradaException(
                        EVALUACION_FICHA_PERFIL_ID, EstadoEvaluacion.APROBADA.getId()));

        // Act & Assert
        mockMvc.perform(peticionAutorizada(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.ObservacionEvaluacion.EVALUACION_CERRADA));
    }

    private MockHttpServletRequestBuilder peticionAutorizada(String body) {
        return post(RUTA, EVALUACION_FICHA_PERFIL_ID)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(j -> j.subject(REPRESENTANTE_COMITE_ID.toString()))
                        .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_EVALUACION_CREATE)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }
}
