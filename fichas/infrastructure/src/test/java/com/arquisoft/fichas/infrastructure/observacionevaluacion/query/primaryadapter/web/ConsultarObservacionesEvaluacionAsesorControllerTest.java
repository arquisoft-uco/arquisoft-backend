package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.primaryadapter.web;

import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.interactor.ConsultarObservacionesEvaluacionAsesorInteractor;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionAsesorQuery;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.FichasFields;
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
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarObservacionesEvaluacionAsesorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarObservacionesEvaluacionAsesorControllerTest.TestSecurityConfig.class})
class ConsultarObservacionesEvaluacionAsesorControllerTest {

    private static final String RUTA = "/fichas-perfil/evaluaciones/{evaluacionFichaPerfilId}/observaciones/asesor";
    private static final UUID ASESOR_FICHA_ID = UtilUUID.generarNuevoUUID();
    private static final UUID EVALUACION_ID = UtilUUID.generarNuevoUUID();

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
    private ConsultarObservacionesEvaluacionAsesorInteractor consultarObservacionesEvaluacionAsesorInteractor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConRol(
            String subject, String authority) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject(subject))
                .authorities(new SimpleGrantedAuthority(authority));
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtAsesor() {
        return jwtConRol(ASESOR_FICHA_ID.toString(), FichasAuthorities.OBSERVACION_EVALUACION_ASESOR_VIEW);
    }

    @Test
    void debeResponder200ConResponseDTO_cuandoHayObservaciones() throws Exception {
        // Arrange
        var observacionId = UtilUUID.generarNuevoUUID();
        when(consultarObservacionesEvaluacionAsesorInteractor.ejecutar(
                any(ConsultarObservacionesEvaluacionAsesorQuery.class)))
                .thenReturn(List.of(new ObservacionEvaluacionReadModel(
                        observacionId, EVALUACION_ID, "El marco teórico es insuficiente")));

        // Act
        mockMvc.perform(get(RUTA, EVALUACION_ID).with(jwtAsesor()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(observacionId.toString()))
                .andExpect(jsonPath("$[0].evaluacionFichaPerfil").value(EVALUACION_ID.toString()))
                .andExpect(jsonPath("$[0].observacion").value("El marco teórico es insuficiente"));

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarObservacionesEvaluacionAsesorQuery.class);
        verify(consultarObservacionesEvaluacionAsesorInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().evaluacionFichaPerfil()).isEqualTo(EVALUACION_ID);
        assertThat(captor.getValue().asesorFicha()).isEqualTo(ASESOR_FICHA_ID);
    }

    @Test
    void debeResponder200ConListaVacia_cuandoNoHayObservaciones() throws Exception {
        // Arrange
        when(consultarObservacionesEvaluacionAsesorInteractor.ejecutar(
                any(ConsultarObservacionesEvaluacionAsesorQuery.class)))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA, EVALUACION_ID).with(jwtAsesor()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void debeResponder400_cuandoEvaluacionFichaPerfilIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, "no-es-uuid").with(jwtAsesor()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(consultarObservacionesEvaluacionAsesorInteractor);
    }

    @Test
    void debeResponder400ConCampoAsesorFicha_cuandoElSubjectNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, EVALUACION_ID)
                        .with(jwtConRol("no-es-uuid", FichasAuthorities.OBSERVACION_EVALUACION_ASESOR_VIEW)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].field")
                        .value(hasItem(FichasFields.ObservacionEvaluacion.ASESOR_FICHA)));

        verifyNoInteractions(consultarObservacionesEvaluacionAsesorInteractor);
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, EVALUACION_ID))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(consultarObservacionesEvaluacionAsesorInteractor);
    }

    @Test
    void debeResponder403_cuandoTieneSoloElClientRoleDelEstudiante() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, EVALUACION_ID)
                        .with(jwtConRol(ASESOR_FICHA_ID.toString(),
                                FichasAuthorities.OBSERVACION_EVALUACION_ESTUDIANTE_VIEW)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(consultarObservacionesEvaluacionAsesorInteractor);
    }
}
