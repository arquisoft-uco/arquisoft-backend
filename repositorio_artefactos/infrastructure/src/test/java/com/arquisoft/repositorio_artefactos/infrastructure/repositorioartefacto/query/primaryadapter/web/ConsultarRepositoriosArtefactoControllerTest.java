package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.query.primaryadapter.web;

import com.arquisoft.repositorio_artefactos.application.administrador.query.readmodel.AdministradorReadModel;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.primaryport.interactor.ConsultarRepositoriosArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.readmodel.RepositorioArtefactoReadModel;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarRepositoriosArtefactoController.class)
@Import({IdentidadActualTestConfig.class,
        AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarRepositoriosArtefactoControllerTest.TestSecurityConfig.class
})
class ConsultarRepositoriosArtefactoControllerTest {

    private static final String RUTA = "/repositorio-artefactos/administrador";

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
    private ConsultarRepositoriosArtefactoInteractor interactor;

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
        var administrador = UtilUUID.generarNuevoUUID();
        var repositorio = new RepositorioArtefactoReadModel(id, "Repo", "Descripcion",
                new AdministradorReadModel(administrador, "123", "Ana Perez", "ana@uco.edu.co"));
        when(interactor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(repositorio), 1, 5, 6L));
        var body = """
                {
                  "pagina": 1,
                  "tamanio": 5,
                  "ordenamiento": ["nombreRepositorio:ASC"],
                  "filtros": { "tipo": "PREDICADO", "campo": "descripcion", "operador": "CONTIENE", "valor": "desc" }
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.content[0].nombreRepositorio").value("Repo"))
                .andExpect(jsonPath("$.content[0].administrador.id").value(administrador.toString()))
                .andExpect(jsonPath("$.content[0].administrador.nombre").value("Ana Perez"))
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
