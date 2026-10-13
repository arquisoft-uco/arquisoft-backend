package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor.ConsultarEvaluacionesFichaPerfilEstudianteInteractor;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilEstudianteQuery;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
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

@WebMvcTest(ConsultarEvaluacionesFichaPerfilEstudianteController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEvaluacionesFichaPerfilEstudianteControllerTest.TestSecurityConfig.class})
class ConsultarEvaluacionesFichaPerfilEstudianteControllerTest {

    private static final String RUTA = "/fichas-perfil/{fichaPerfilId}/evaluaciones/estudiante";
    private static final UUID ESTUDIANTE_ID = UtilUUID.generarNuevoUUID();
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
    private ConsultarEvaluacionesFichaPerfilEstudianteInteractor consultarEvaluacionesFichaPerfilEstudianteInteractor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConRol(String authority) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                .authorities(new SimpleGrantedAuthority(authority));
    }

    @Test
    void debeRetornar200ConResponseDTO_cuandoTieneRol() throws Exception {
        // Arrange
        var evaluacionId = UtilUUID.generarNuevoUUID();
        var representanteId = UtilUUID.generarNuevoUUID();
        when(consultarEvaluacionesFichaPerfilEstudianteInteractor.ejecutar(
                any(ConsultarEvaluacionesFichaPerfilEstudianteQuery.class)))
                .thenReturn(List.of(new EvaluacionFichaPerfilEstudianteReadModel(
                        evaluacionId, FICHA_ID, Instant.parse("2026-09-20T15:04:05Z"),
                        "APROBADA", "Aprobada",
                        new RepresentanteComiteReadModel(representanteId, "María Gómez"))));

        // Act
        mockMvc.perform(get(RUTA, FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.EVALUACION_FICHA_PERFIL_ESTUDIANTE_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(evaluacionId.toString()))
                .andExpect(jsonPath("$[0].fichaPerfil").value(FICHA_ID.toString()))
                .andExpect(jsonPath("$[0].estadoEvaluacion").value("APROBADA"))
                .andExpect(jsonPath("$[0].estadoEvaluacionNombre").value("Aprobada"))
                .andExpect(jsonPath("$[0].representanteComite.id").value(representanteId.toString()))
                .andExpect(jsonPath("$[0].representanteComite.nombre").value("María Gómez"))
                .andExpect(jsonPath("$[0].representanteComite.email").doesNotExist());

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarEvaluacionesFichaPerfilEstudianteQuery.class);
        verify(consultarEvaluacionesFichaPerfilEstudianteInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().fichaPerfil()).isEqualTo(FICHA_ID);
        assertThat(captor.getValue().estudiante()).isEqualTo(ESTUDIANTE_ID);
    }

    @Test
    void debeRetornar400_cuandoFichaPerfilIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, "no-es-uuid")
                        .with(jwtConRol(FichasAuthorities.EVALUACION_FICHA_PERFIL_ESTUDIANTE_VIEW)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(consultarEvaluacionesFichaPerfilEstudianteInteractor);
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, FICHA_ID))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(consultarEvaluacionesFichaPerfilEstudianteInteractor);
    }

    @Test
    void debeRetornar403_cuandoRolDeRepresentante() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.EVALUACION_FICHA_PERFIL_REPRESENTANTE_VIEW)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(consultarEvaluacionesFichaPerfilEstudianteInteractor);
    }
}
