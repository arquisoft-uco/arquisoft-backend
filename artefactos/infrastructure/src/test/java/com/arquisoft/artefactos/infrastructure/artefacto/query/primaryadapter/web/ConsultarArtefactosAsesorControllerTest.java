package com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web;

import com.arquisoft.artefactos.application.artefacto.query.primaryport.interactor.ConsultarArtefactosAsesorInteractor;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosAsesorQuery;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoReadModel;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarArtefactosAsesorController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, ConsultarArtefactosAsesorControllerTest.TestSecurityConfig.class})
class ConsultarArtefactosAsesorControllerTest {

    private static final String RUTA = "/artefactos/asesor";

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
    private ConsultarArtefactosAsesorInteractor consultarArtefactosAsesorInteractor;

    private static final UUID ASESOR_ID = UtilUUID.generarNuevoUUID();

    private static RequestPostProcessor tokenAsesor(String sub) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sub));
    }

    @Test
    void debeRetornar200ConResponseDTO_yConsultarConElSubjectDelToken() throws Exception {
        // Arrange
        var artefacto = new ArtefactoReadModel(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Proyecto Alfa",
                UtilUUID.generarNuevoUUID(), "Documento", 2, "Repositorio", false);
        when(consultarArtefactosAsesorInteractor.ejecutar(any(ConsultarArtefactosAsesorQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(artefacto), 0, 10, 1L));
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
                        .with(tokenAsesor(ASESOR_ID.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(artefacto.id().toString()))
                .andExpect(jsonPath("$.content[0].proyectoGrado").value(artefacto.proyectoGrado().toString()))
                .andExpect(jsonPath("$.content[0].nombreProyecto").value("Proyecto Alfa"))
                .andExpect(jsonPath("$.content[0].versionRepositorioArtefacto")
                        .value(artefacto.versionRepositorioArtefacto().toString()))
                .andExpect(jsonPath("$.content[0].nombreDocumento").value("Documento"))
                .andExpect(jsonPath("$.content[0].versionDocumento").value(2))
                .andExpect(jsonPath("$.content[0].nombreRepositorio").value("Repositorio"))
                .andExpect(jsonPath("$.totalElements").value(1));

        var captor = ArgumentCaptor.forClass(ConsultarArtefactosAsesorQuery.class);
        verify(consultarArtefactosAsesorInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().asesor()).isEqualTo(ASESOR_ID);
    }

    @Test
    void debeRetornar200_cuandoLaPeticionNoTraeBody() throws Exception {
        // Arrange
        when(consultarArtefactosAsesorInteractor.ejecutar(any(ConsultarArtefactosAsesorQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(tokenAsesor(ASESOR_ID.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void debeRetornar400_cuandoElArbolDeFiltrosReferenciaUnCampoNoDeclarado() throws Exception {
        // Arrange
        when(consultarArtefactosAsesorInteractor.ejecutar(any(ConsultarArtefactosAsesorQuery.class)))
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
                        .with(tokenAsesor(ASESOR_ID.toString())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
