package com.arquisoft.artefactos.infrastructure.revisionasesor.query.primaryadapter.web;

import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.interactor.ConsultarRevisionesAsesorEstudianteInteractor;
import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.model.ConsultarRevisionesAsesorEstudianteQuery;
import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorReadModel;
import com.arquisoft.artefactos.infrastructure.security.ArtefactosAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

@WebMvcTest(ConsultarRevisionesAsesorEstudianteController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarRevisionesAsesorEstudianteControllerTest.TestSecurityConfig.class})
class ConsultarRevisionesAsesorEstudianteControllerTest {

    private static final String RUTA = "/artefactos/revisiones-asesor/estudiante";

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
    private ConsultarRevisionesAsesorEstudianteInteractor consultarRevisionesAsesorEstudianteInteractor;

    private static final UUID ESTUDIANTE_ID = UUID.randomUUID();

    private static org.springframework.test.web.servlet.request.RequestPostProcessor tokenEstudiante(String sub) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject(sub))
                .authorities(new SimpleGrantedAuthority(ArtefactosAuthorities.REVISION_ASESOR_ESTUDIANTE_VIEW));
    }

    @Test
    void debeRetornar200ConResponseDTO_cuandoConsultaValida() throws Exception {
        // Arrange
        var revision = new RevisionAsesorReadModel(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 2, "PENDIENTE", "Pendiente");
        when(consultarRevisionesAsesorEstudianteInteractor.ejecutar(any(ConsultarRevisionesAsesorEstudianteQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(revision), 0, 10, 1L));
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
                .andExpect(jsonPath("$.content[0].id").value(revision.id().toString()))
                .andExpect(jsonPath("$.content[0].versionArtefacto").value(revision.versionArtefacto().toString()))
                .andExpect(jsonPath("$.content[0].artefacto").value(revision.artefacto().toString()))
                .andExpect(jsonPath("$.content[0].version").value(2))
                .andExpect(jsonPath("$.content[0].estadoRevisionAsesor").value("PENDIENTE"))
                .andExpect(jsonPath("$.content[0].estadoRevisionAsesorNombre").value("Pendiente"))
                .andExpect(jsonPath("$.totalElements").value(1));

        var captor = ArgumentCaptor.forClass(ConsultarRevisionesAsesorEstudianteQuery.class);
        verify(consultarRevisionesAsesorEstudianteInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().estudiante()).isEqualTo(ESTUDIANTE_ID);
    }

    @Test
    void debeRetornar200_cuandoLaPeticionNoTraeBody() throws Exception {
        // Arrange
        when(consultarRevisionesAsesorEstudianteInteractor.ejecutar(any(ConsultarRevisionesAsesorEstudianteQuery.class)))
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
        when(consultarRevisionesAsesorEstudianteInteractor.ejecutar(any(ConsultarRevisionesAsesorEstudianteQuery.class)))
                .thenThrow(new FiltroException("campo no permitido: artefacto", "artefactos.consulta.campo-no-permitido"));
        var body = """
                {
                  "pagina": 0,
                  "tamanio": 10,
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "artefacto",
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
    void debeRetornar400YNoConsultar_cuandoElSubDelTokenNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(tokenEstudiante("no-es-un-uuid")))
                .andExpect(status().isBadRequest());

        verify(consultarRevisionesAsesorEstudianteInteractor, never())
                .ejecutar(any(ConsultarRevisionesAsesorEstudianteQuery.class));
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoSinClientRoleRevisionAsesorEstudianteView() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority("otro:permiso"))))
                .andExpect(status().isForbidden());
    }
}
