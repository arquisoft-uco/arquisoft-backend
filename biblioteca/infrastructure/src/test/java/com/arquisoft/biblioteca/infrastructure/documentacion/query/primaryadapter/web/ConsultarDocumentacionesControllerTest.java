package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web;

import com.arquisoft.biblioteca.application.documentacion.query.primaryport.interactor.ConsultarDocumentacionesInteractor;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.ConsultarDocumentacionesQuery;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.RolConsultaDocumentacion;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel;
import com.arquisoft.shared.logger.AppLoggerConfig;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarDocumentacionesController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, ConsultarDocumentacionesControllerTest.TestSecurityConfig.class})
class ConsultarDocumentacionesControllerTest {

    private static final String RUTA = "/biblioteca/documentaciones/consultar";

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
    private ConsultarDocumentacionesInteractor consultarDocumentacionesInteractor;

    @Test
    void debeRetornar200ConLaPaginaYPasarLosRolesDelToken_cuandoEstaAutenticado() throws Exception {
        // Arrange
        var fecha = Instant.parse("2026-11-10T10:00:00Z");
        var documentacion = new DocumentacionReadModel(
                UtilUUID.generarNuevoUUID(), "https://biblioteca.test/doc", fecha,
                new DocumentacionReadModel.EntregableProyectoGradoResumen(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 2, "Aprobado"),
                new DocumentacionReadModel.BibliotecarioResumen(UtilUUID.generarNuevoUUID(), "Ana"),
                new DocumentacionReadModel.EstadoDocumentacionResumen("PUBLICADO", "Publicado"));
        when(consultarDocumentacionesInteractor.ejecutar(any(ConsultarDocumentacionesQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(documentacion), 0, 10, 1L));
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pagina\":0,\"tamanio\":10}")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(sujeto).claim("roles", List.of("estudiante")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(documentacion.id().toString()))
                .andExpect(jsonPath("$.content[0].url").value("https://biblioteca.test/doc"))
                .andExpect(jsonPath("$.content[0].bibliotecario.nombre").value("Ana"))
                .andExpect(jsonPath("$.content[0].estadoDocumentacion.id").value("PUBLICADO"))
                .andExpect(jsonPath("$.content[0].entregableProyectoGrado.versionEntregable").value(2))
                .andExpect(jsonPath("$.totalElements").value(1));

        var captor = ArgumentCaptor.forClass(ConsultarDocumentacionesQuery.class);
        verify(consultarDocumentacionesInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().roles()).containsExactly(RolConsultaDocumentacion.ESTUDIANTE);
    }

    @Test
    void debeRetornar200_cuandoLaPeticionNoTraeBody() throws Exception {
        // Arrange
        when(consultarDocumentacionesInteractor.ejecutar(any(ConsultarDocumentacionesQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void debeRetornar400SinInvocarElInteractor_cuandoElOrdenamientoEsInvalido() throws Exception {
        // Arrange
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ordenamiento\":[\"fechaPublicacion:sideways\"]}")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto))))
                .andExpect(status().isBadRequest());
        verify(consultarDocumentacionesInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar401SinInvocarElInteractor_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        verify(consultarDocumentacionesInteractor, never()).ejecutar(any());
    }
}
