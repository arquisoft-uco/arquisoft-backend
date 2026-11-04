package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.interactor.ConsultarVersionesRepositorioArtefactoAsesorInteractor;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.RepositorioDeVersionReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoAsesorReadModel;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarVersionesRepositorioArtefactoAsesorController.class)
@Import({IdentidadActualTestConfig.class,
        AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarVersionesRepositorioArtefactoAsesorControllerTest.TestSecurityConfig.class
})
class ConsultarVersionesRepositorioArtefactoAsesorControllerTest {

    private static final String RUTA = "/repositorio-artefactos/versiones/asesor";

    @TestConfiguration
    @EnableWebSecurity
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
    private ConsultarVersionesRepositorioArtefactoAsesorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwt() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(UtilUUID.generarNuevoUUID().toString()));
    }

    @Test
    void debeRetornar200ConPagina_cuandoElCuerpoEsValido() throws Exception {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        var version = new VersionRepositorioArtefactoAsesorReadModel(id, "Formato A", 2,
                Instant.parse("2026-10-20T10:00:00Z"), "application/pdf", 2048L, "formato.pdf", null,
                new RepositorioDeVersionReadModel(repositorio, "Repo", "Descripcion"));
        when(interactor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(version), 1, 5, 6L));
        var body = """
                {
                  "pagina": 1,
                  "tamanio": 5,
                  "ordenamiento": ["nombreRepositorio:ASC"],
                  "filtros": { "tipo": "PREDICADO", "campo": "nombreDocumento", "operador": "CONTIENE", "valor": "for" }
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.content[0].nombreDocumento").value("Formato A"))
                .andExpect(jsonPath("$.content[0].numeroVersion").value(2))
                .andExpect(jsonPath("$.content[0].hashSha256").doesNotExist())
                .andExpect(jsonPath("$.content[0].uriArchivo").doesNotExist())
                .andExpect(jsonPath("$.content[0].repositorioArtefacto.id").value(repositorio.toString()))
                .andExpect(jsonPath("$.content[0].repositorioArtefacto.nombreRepositorio").value("Repo"))
                .andExpect(jsonPath("$.totalElements").value(6));
        var captor = ArgumentCaptor.forClass(ConsultaCriteriaQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().pagina()).isEqualTo(1);
        assertThat(captor.getValue().tamanio()).isEqualTo(5);
        assertThat(captor.getValue().ordenamiento()).hasSize(1);
        assertThat(captor.getValue().raiz()).isNotNull();
    }

    @Test
    void debeRetornar200_cuandoNoHayCuerpo() throws Exception {
        // Arrange
        when(interactor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
        var captor = ArgumentCaptor.forClass(ConsultaCriteriaQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().pagina()).isZero();
        assertThat(captor.getValue().tamanio()).isEqualTo(10);
    }

    @Test
    void debeRetornar400_cuandoElFiltroEsInvalido() throws Exception {
        // Arrange
        when(interactor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenThrow(new FiltroException("campo de filtro no permitido: campoInventado",
                        "repositorio_artefactos.consulta.campo-filtro-no-permitido"));
        var body = """
                { "filtros": { "tipo": "PREDICADO", "campo": "campoInventado", "operador": "ES", "valor": "x" } }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(interactor);
    }
}
