package com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web;

import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.interactor.ConsultarVersionesArtefactoEstudianteInteractor;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionesArtefactoEstudianteQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarVersionesArtefactoEstudianteController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, ConsultarVersionesArtefactoEstudianteControllerTest.TestSecurityConfig.class})
class ConsultarVersionesArtefactoEstudianteControllerTest {

    private static final String RUTA = "/artefactos/versiones/estudiante";

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

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarVersionesArtefactoEstudianteInteractor consultarVersionesArtefactoEstudianteInteractor;

    private static final UUID ESTUDIANTE_ID = UtilUUID.generarNuevoUUID();

    private static RequestPostProcessor tokenEstudiante(String sub) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sub));
    }

    @Test
    void debeRetornar200ConResponseDTO_yConsultarConElSubjectDelToken() throws Exception {
        // Arrange
        var version = new VersionArtefactoReadModel(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 2,
                Instant.parse("2026-10-20T10:00:00Z"), "artefactos/v2.pdf");
        when(consultarVersionesArtefactoEstudianteInteractor.ejecutar(any(ConsultarVersionesArtefactoEstudianteQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(version), 0, 10, 1L));
        var body = """
                {
                  "pagina": 0,
                  "tamanio": 10
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(tokenEstudiante(ESTUDIANTE_ID.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(version.id().toString()))
                .andExpect(jsonPath("$.content[0].artefacto").value(version.artefacto().toString()))
                .andExpect(jsonPath("$.content[0].version").value(2))
                .andExpect(jsonPath("$.content[0].fechaCreacion").exists())
                .andExpect(jsonPath("$.content[0].uriArchivo").value("artefactos/v2.pdf"))
                .andExpect(jsonPath("$.totalElements").value(1));

        var captor = ArgumentCaptor.forClass(ConsultarVersionesArtefactoEstudianteQuery.class);
        verify(consultarVersionesArtefactoEstudianteInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().estudiante()).isEqualTo(ESTUDIANTE_ID);
    }

    @Test
    void debeRetornar200_cuandoLaPeticionNoTraeBody() throws Exception {
        // Arrange
        when(consultarVersionesArtefactoEstudianteInteractor.ejecutar(any(ConsultarVersionesArtefactoEstudianteQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(tokenEstudiante(ESTUDIANTE_ID.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void debeRetornar400_cuandoElArbolDeFiltrosReferenciaUnCampoNoDeclarado() throws Exception {
        // Arrange
        when(consultarVersionesArtefactoEstudianteInteractor.ejecutar(any(ConsultarVersionesArtefactoEstudianteQuery.class)))
                .thenThrow(new FiltroException("campo no permitido: uriArchivo",
                        "artefactos.consulta.campo-no-permitido"));
        var body = """
                {
                  "pagina": 0,
                  "tamanio": 10,
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "uriArchivo",
                    "operador": "ES",
                    "valor": "cualquiera"
                  }
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(tokenEstudiante(ESTUDIANTE_ID.toString())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
