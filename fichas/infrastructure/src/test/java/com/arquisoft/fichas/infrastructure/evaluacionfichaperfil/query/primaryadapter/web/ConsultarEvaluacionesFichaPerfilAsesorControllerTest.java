package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor.ConsultarEvaluacionesFichaPerfilAsesorInteractor;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilAsesorQuery;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEvaluacionesFichaPerfilAsesorController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEvaluacionesFichaPerfilAsesorControllerTest.TestSecurityConfig.class})
class ConsultarEvaluacionesFichaPerfilAsesorControllerTest {

    @TestConfiguration
    @EnableWebSecurity
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

    private static final String RUTA = "/fichas-perfil/evaluaciones/asesor";

    private static final UUID ASESOR_ID = UtilUUID.generarNuevoUUID();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarEvaluacionesFichaPerfilAsesorInteractor consultarEvaluacionesFichaPerfilAsesorInteractor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConRoles(
            String subject) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject(subject));
    }

    @Test
    void debeRetornar200ConLaPaginaYElRepresentante_cuandoNoHayBody() throws Exception {
        // Arrange
        var evaluacionId = UtilUUID.generarNuevoUUID();
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var representanteId = UtilUUID.generarNuevoUUID();
        var evaluacion = new EvaluacionFichaPerfilAsesorReadModel(
                evaluacionId, fichaPerfil, "Sistema de gestion", Instant.parse("2026-09-07T14:03:00Z"),
                "APROBADA", "Aprobada", new RepresentanteComiteReadModel(representanteId, "María Gómez"));
        when(consultarEvaluacionesFichaPerfilAsesorInteractor.ejecutar(
                any(ConsultarEvaluacionesFichaPerfilAsesorQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(evaluacion), 0, 10, 1L));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtConRoles(ASESOR_ID.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(evaluacionId.toString()))
                .andExpect(jsonPath("$.content[0].fichaPerfil").value(fichaPerfil.toString()))
                .andExpect(jsonPath("$.content[0].tituloProyecto").value("Sistema de gestion"))
                .andExpect(jsonPath("$.content[0].estadoEvaluacion").value("APROBADA"))
                .andExpect(jsonPath("$.content[0].estadoEvaluacionNombre").value("Aprobada"))
                .andExpect(jsonPath("$.content[0].representanteComite.id").value(representanteId.toString()))
                .andExpect(jsonPath("$.content[0].representanteComite.nombre").value("María Gómez"));
    }

    @Test
    void debeTomarElAsesorDelSubjectDelJwt_noDelBody_cuandoElBodyTraeOtroAsesorFicha() throws Exception {
        // Arrange
        when(consultarEvaluacionesFichaPerfilAsesorInteractor.ejecutar(
                any(ConsultarEvaluacionesFichaPerfilAsesorQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));
        var otroAsesorEnElBody = UtilUUID.generarNuevoUUID();
        var body = """
                {
                  "pagina": 0,
                  "tamanio": 10,
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "asesorFicha",
                    "operador": "ES",
                    "valor": "%s"
                  }
                }
                """.formatted(otroAsesorEnElBody);

        // Act
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(jwtConRoles(ASESOR_ID.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarEvaluacionesFichaPerfilAsesorQuery.class);
        verify(consultarEvaluacionesFichaPerfilAsesorInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().asesorFicha()).isEqualTo(ASESOR_ID);
    }

    @Test
    void debeRetornar400_cuandoElFiltroEsInvalido() throws Exception {
        // Arrange
        when(consultarEvaluacionesFichaPerfilAsesorInteractor.ejecutar(
                any(ConsultarEvaluacionesFichaPerfilAsesorQuery.class)))
                .thenThrow(new FiltroException("operador no permitido: INVALIDO",
                        "fichas.consulta.operador-no-permitido"));
        var body = """
                {
                  "pagina": 0,
                  "tamanio": 10,
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "estadoEvaluacion",
                    "operador": "INVALIDO",
                    "valor": "APROBADA"
                  }
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(jwtConRoles(ASESOR_ID.toString())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401SinInvocarAlInteractor_cuandoElSubjectDelJwtNoEsUnUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtConRoles("no-es-un-uuid")))
                .andExpect(status().isUnauthorized());

        verify(consultarEvaluacionesFichaPerfilAsesorInteractor, never())
                .ejecutar(any(ConsultarEvaluacionesFichaPerfilAsesorQuery.class));
    }

    @Test
    void debeRetornar401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
