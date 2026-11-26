package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web;

import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.interactor.ConsultarParticipacionesArtefactoInteractor;
import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarParticipacionesArtefactoQuery;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.ParticipacionArtefactoReadModel;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarParticipacionesArtefactoController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, ConsultarParticipacionesArtefactoControllerTest.TestSecurityConfig.class})
class ConsultarParticipacionesArtefactoControllerTest {

    private static final String RUTA = "/artefactos/estudiante/participaciones";

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
                    .accessDeniedHandler((req, res, e) -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarParticipacionesArtefactoInteractor interactor;

    private static RequestPostProcessor tokenEstudiante(String sub) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sub));
    }

    private static ParticipacionArtefactoReadModel readModel(String estadoUltimaRevision) {
        return new ParticipacionArtefactoReadModel(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "Proyecto Alfa", UtilUUID.generarNuevoUUID(), "Informe", 2, "Repo",
                UtilUUID.generarNuevoUUID(), "Ana Asesora", false, estadoUltimaRevision, true, false);
    }

    @Test
    void debeRetornar200ConPagina_yTodosLosCampos() throws Exception {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var readModel = readModel("Pendiente");
        when(interactor.ejecutar(any(ConsultarParticipacionesArtefactoQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(readModel), 0, 10, 1L));
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
                        .with(tokenEstudiante(estudiante.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(readModel.id().toString()))
                .andExpect(jsonPath("$.content[0].proyectoGrado").value(readModel.proyectoGrado().toString()))
                .andExpect(jsonPath("$.content[0].nombreProyecto").value("Proyecto Alfa"))
                .andExpect(jsonPath("$.content[0].versionRepositorioArtefacto")
                        .value(readModel.versionRepositorioArtefacto().toString()))
                .andExpect(jsonPath("$.content[0].nombreDocumento").value("Informe"))
                .andExpect(jsonPath("$.content[0].versionDocumento").value(2))
                .andExpect(jsonPath("$.content[0].nombreRepositorio").value("Repo"))
                .andExpect(jsonPath("$.content[0].asesor").value(readModel.asesor().toString()))
                .andExpect(jsonPath("$.content[0].nombreAsesor").value("Ana Asesora"))
                .andExpect(jsonPath("$.content[0].omitido").value(false))
                .andExpect(jsonPath("$.content[0].estadoUltimaRevision").value("Pendiente"))
                .andExpect(jsonPath("$.content[0].proyectoFinalizado").value(true))
                .andExpect(jsonPath("$.content[0].proyectoCancelado").value(false))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void debeOmitirEstadoUltimaRevision_cuandoEsNulo() throws Exception {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any(ConsultarParticipacionesArtefactoQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(readModel(null)), 0, 10, 1L));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(tokenEstudiante(estudiante.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].estadoUltimaRevision").doesNotExist());
    }

    @Test
    void debeRetornar200_cuandoLaPeticionNoTraeBody() throws Exception {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any(ConsultarParticipacionesArtefactoQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(tokenEstudiante(estudiante.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void debePasarElSubjectDelTokenYElCriterioAlQuery() throws Exception {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any(ConsultarParticipacionesArtefactoQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 2, 5, 0L));
        var body = """
                {
                  "pagina": 2,
                  "tamanio": 5
                }
                """;

        // Act
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(tokenEstudiante(estudiante.toString())))
                .andExpect(status().isOk());

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarParticipacionesArtefactoQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().estudiante()).isEqualTo(estudiante);
        assertThat(captor.getValue().criterio().pagina()).isEqualTo(2);
        assertThat(captor.getValue().criterio().tamanio()).isEqualTo(5);
    }

    @Test
    void debeRetornar400_cuandoElArbolDeFiltrosReferenciaUnCampoNoDeclarado() throws Exception {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any(ConsultarParticipacionesArtefactoQuery.class)))
                .thenThrow(new FiltroException("campo no permitido: asesor",
                        "artefactos.consulta.campo-no-permitido"));
        var body = """
                {
                  "pagina": 0,
                  "tamanio": 10,
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "asesor",
                    "operador": "ES",
                    "valor": "cualquiera"
                  }
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(tokenEstudiante(estudiante.toString())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(interactor, never()).ejecutar(any());
    }
}
