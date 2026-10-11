package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.primaryadapter.web;

import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.interactor.ConsultarObservacionesEvaluacionCoordinadorInteractor;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionCoordinadorQuery;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarObservacionesEvaluacionCoordinadorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarObservacionesEvaluacionCoordinadorControllerTest.TestSecurityConfig.class})
class ConsultarObservacionesEvaluacionCoordinadorControllerTest {

    private static final String RUTA = "/fichas-perfil/{fichaPerfilId}/observaciones-evaluacion/coordinador";
    private static final UUID FICHA_ID = UtilUUID.generarNuevoUUID();

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

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarObservacionesEvaluacionCoordinadorInteractor consultarObservacionesEvaluacionCoordinadorInteractor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConRol(String authority) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(authority));
    }

    @Test
    void debeResponder200ConLista_cuandoTieneElRol() throws Exception {
        // Arrange
        var observacionId = UtilUUID.generarNuevoUUID();
        var evaluacionId = UtilUUID.generarNuevoUUID();
        when(consultarObservacionesEvaluacionCoordinadorInteractor.ejecutar(
                any(ConsultarObservacionesEvaluacionCoordinadorQuery.class)))
                .thenReturn(List.of(new ObservacionEvaluacionReadModel(
                        observacionId, evaluacionId, "Falta delimitar el alcance")));

        // Act
        mockMvc.perform(get(RUTA, FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.OBSERVACION_EVALUACION_COORDINADOR_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(observacionId.toString()))
                .andExpect(jsonPath("$[0].evaluacionFichaPerfil").value(evaluacionId.toString()))
                .andExpect(jsonPath("$[0].observacion").value("Falta delimitar el alcance"));

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarObservacionesEvaluacionCoordinadorQuery.class);
        verify(consultarObservacionesEvaluacionCoordinadorInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().fichaPerfil()).isEqualTo(FICHA_ID);
    }

    @Test
    void debeResponder200Vacia_cuandoNoHayObservaciones() throws Exception {
        // Arrange
        when(consultarObservacionesEvaluacionCoordinadorInteractor.ejecutar(
                any(ConsultarObservacionesEvaluacionCoordinadorQuery.class)))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA, FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.OBSERVACION_EVALUACION_COORDINADOR_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void debeResponder400_cuandoFichaNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, "no-es-uuid")
                        .with(jwtConRol(FichasAuthorities.OBSERVACION_EVALUACION_COORDINADOR_VIEW)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(consultarObservacionesEvaluacionCoordinadorInteractor);
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, FICHA_ID))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(consultarObservacionesEvaluacionCoordinadorInteractor);
    }

    @Test
    void debeResponder403_cuandoTieneSoloElRolDelAsesor() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.OBSERVACION_EVALUACION_ASESOR_VIEW)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(consultarObservacionesEvaluacionCoordinadorInteractor);
    }
}
