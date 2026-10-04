package com.arquisoft.fichas.infrastructure.revisionitem.query.primaryadapter.web;

import com.arquisoft.fichas.application.revisionitem.query.primaryport.interactor.ConsultarRevisionesItemEstudianteInteractor;
import com.arquisoft.fichas.application.revisionitem.query.primaryport.model.ConsultarRevisionesItemEstudianteQuery;
import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarRevisionesItemEstudianteController.class)
@Import({GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarRevisionesItemEstudianteControllerTest.TestSecurityConfig.class})
class ConsultarRevisionesItemEstudianteControllerTest {

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
    private ConsultarRevisionesItemEstudianteInteractor consultarRevisionesItemEstudianteInteractor;

    private static final UUID ESTUDIANTE_ID = UUID.randomUUID();

    @Test
    void debeRetornar200ConPagina_cuandoConsultaValida() throws Exception {
        // Arrange
        var revision = new RevisionItemReadModel(
                UUID.randomUUID(), UUID.randomUUID(), "EN_PROGRESO", "En Progreso", Instant.now());
        when(consultarRevisionesItemEstudianteInteractor.ejecutar(any(ConsultarRevisionesItemEstudianteQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(revision), 0, 10, 1L));

        String body = """
                {
                  "pagina": 0,
                  "tamanio": 10
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/revisiones-item/estudiante")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.REVISION_ITEM_ESTUDIANTE_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].estadoRevision").value("EN_PROGRESO"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void debeRetornar200ConPaginaVacia_cuandoEstudianteSinRevisionesVisibles() throws Exception {
        // Arrange
        when(consultarRevisionesItemEstudianteInteractor.ejecutar(any(ConsultarRevisionesItemEstudianteQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        String body = """
                {
                  "pagina": 0,
                  "tamanio": 10
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/revisiones-item/estudiante")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.REVISION_ITEM_ESTUDIANTE_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void debeRetornar400_cuandoElArbolDeFiltrosReferenciaUnCampoNoDeclarado() throws Exception {
        // Arrange — "tituloProyecto" no está declarado en RevisionItemEstudianteCriteria.Campo, así
        // que el Mapper (dentro del Interactor real, mockeado aquí) rechaza la construcción del Criteria.
        when(consultarRevisionesItemEstudianteInteractor.ejecutar(any(ConsultarRevisionesItemEstudianteQuery.class)))
                .thenThrow(new FiltroException(
                        "campo no permitido: tituloProyecto", "fichas.consulta.campo-no-permitido"));

        String body = """
                {
                  "pagina": 0,
                  "tamanio": 10,
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "tituloProyecto",
                    "operador": "ES",
                    "valor": "cualquiera"
                  }
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/revisiones-item/estudiante")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.REVISION_ITEM_ESTUDIANTE_VIEW))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/revisiones-item/estudiante")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoSinClientRoleRevisionItemEstudianteView() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/revisiones-item/estudiante")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority("otro:permiso"))))
                .andExpect(status().isForbidden());
    }
}
