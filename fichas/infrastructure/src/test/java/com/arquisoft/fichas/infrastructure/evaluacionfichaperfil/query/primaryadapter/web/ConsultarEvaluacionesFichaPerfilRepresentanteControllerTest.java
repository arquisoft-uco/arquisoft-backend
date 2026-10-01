package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor.ConsultarEvaluacionesFichaPerfilRepresentanteInteractor;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilRepresentanteQuery;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
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

import java.time.Instant;
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

@WebMvcTest(ConsultarEvaluacionesFichaPerfilRepresentanteController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEvaluacionesFichaPerfilRepresentanteControllerTest.TestSecurityConfig.class})
class ConsultarEvaluacionesFichaPerfilRepresentanteControllerTest {

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
                    .accessDeniedHandler((req, res, e)    -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarEvaluacionesFichaPerfilRepresentanteInteractor consultarEvaluacionesFichaPerfilRepresentanteInteractor;

    private static final UUID REPRESENTANTE_ID = UUID.randomUUID();
    private static final UUID FICHA_ID = UUID.randomUUID();

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConRol(String authority) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject(REPRESENTANTE_ID.toString()))
                .authorities(new SimpleGrantedAuthority(authority));
    }

    @Test
    void debeResponder200ConLista_cuandoHayEvaluaciones() throws Exception {
        // Arrange
        var evaluacionId = UUID.randomUUID();
        when(consultarEvaluacionesFichaPerfilRepresentanteInteractor.ejecutar(
                any(ConsultarEvaluacionesFichaPerfilRepresentanteQuery.class)))
                .thenReturn(List.of(new EvaluacionFichaPerfilReadModel(
                        evaluacionId, FICHA_ID, Instant.parse("2026-01-01T00:00:00Z"),
                        "EN_EVALUACION", "En Evaluación")));

        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/{fichaPerfilId}/evaluaciones/representante", FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.EVALUACION_FICHA_PERFIL_REPRESENTANTE_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(evaluacionId.toString()))
                .andExpect(jsonPath("$[0].fichaPerfilId").value(FICHA_ID.toString()))
                .andExpect(jsonPath("$[0].estadoEvaluacion").value("EN_EVALUACION"))
                .andExpect(jsonPath("$[0].estadoEvaluacionNombre").value("En Evaluación"));
    }

    @Test
    void debeResponder200ConListaVacia_cuandoNoHayEvaluaciones() throws Exception {
        // Arrange
        when(consultarEvaluacionesFichaPerfilRepresentanteInteractor.ejecutar(
                any(ConsultarEvaluacionesFichaPerfilRepresentanteQuery.class)))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/{fichaPerfilId}/evaluaciones/representante", FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.EVALUACION_FICHA_PERFIL_REPRESENTANTE_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void debeResponder400_cuandoFichaPerfilIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/{fichaPerfilId}/evaluaciones/representante", "no-es-uuid")
                        .with(jwtConRol(FichasAuthorities.EVALUACION_FICHA_PERFIL_REPRESENTANTE_VIEW)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(consultarEvaluacionesFichaPerfilRepresentanteInteractor);
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/{fichaPerfilId}/evaluaciones/representante", FICHA_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder403_cuandoElTokenNoTieneElClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/{fichaPerfilId}/evaluaciones/representante", FICHA_ID)
                        .with(jwtConRol("otro:permiso")))
                .andExpect(status().isForbidden());
    }

    @Test
    void debePasarFichaPerfilIdDelPathYSubjectDelJwtAlQuery() throws Exception {
        // Arrange
        when(consultarEvaluacionesFichaPerfilRepresentanteInteractor.ejecutar(
                any(ConsultarEvaluacionesFichaPerfilRepresentanteQuery.class)))
                .thenReturn(List.of());

        // Act
        mockMvc.perform(get("/fichas-perfil/{fichaPerfilId}/evaluaciones/representante", FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.EVALUACION_FICHA_PERFIL_REPRESENTANTE_VIEW)))
                .andExpect(status().isOk());

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarEvaluacionesFichaPerfilRepresentanteQuery.class);
        verify(consultarEvaluacionesFichaPerfilRepresentanteInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().fichaPerfil()).isEqualTo(FICHA_ID);
        assertThat(captor.getValue().representanteComite()).isEqualTo(REPRESENTANTE_ID);
    }
}
