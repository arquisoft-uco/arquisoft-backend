package com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.primaryadapter.web;

import com.arquisoft.biblioteca.application.autordocumentacion.query.primaryport.interactor.ConsultarAutoresDocumentacionInteractor;
import com.arquisoft.biblioteca.application.autordocumentacion.query.primaryport.model.ConsultarAutoresDocumentacionQuery;
import com.arquisoft.biblioteca.application.autordocumentacion.query.readmodel.AutorDocumentacionReadModel;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.shared.web.identidad.IdentidadActualTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarAutoresDocumentacionController.class)
@Import({AppLoggerConfig.class, IdentidadActualTestConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarAutoresDocumentacionControllerTest.TestSecurityConfig.class})
class ConsultarAutoresDocumentacionControllerTest {

    private static final String RUTA = "/biblioteca/documentaciones/{documentacionId}/autores";

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
    private ConsultarAutoresDocumentacionInteractor consultarAutoresDocumentacionInteractor;

    @Test
    void debeRetornar200ConLaLista_cuandoLaDocumentacionTieneAutores() throws Exception {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var autor = UtilUUID.generarNuevoUUID();
        var autores = List.of(
                new AutorDocumentacionReadModel(autor, "20161020123", "Ana Perez", "ana@uco.edu.co"),
                new AutorDocumentacionReadModel(UtilUUID.generarNuevoUUID(), "2", "Beto", "beto@uco.edu.co"));
        when(consultarAutoresDocumentacionInteractor.ejecutar(
                ConsultarAutoresDocumentacionQuery.crear(documentacion))).thenReturn(autores);
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(get(RUTA, documentacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(autor.toString()))
                .andExpect(jsonPath("$[0].identificador").value("20161020123"))
                .andExpect(jsonPath("$[0].nombre").value("Ana Perez"))
                .andExpect(jsonPath("$[0].email").value("ana@uco.edu.co"))
                .andExpect(jsonPath("$[1].nombre").value("Beto"));
        verify(consultarAutoresDocumentacionInteractor).ejecutar(
                ConsultarAutoresDocumentacionQuery.crear(documentacion));
    }

    @Test
    void debeRetornar200ConListaVacia_cuandoNoHayAutores() throws Exception {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        when(consultarAutoresDocumentacionInteractor.ejecutar(
                ConsultarAutoresDocumentacionQuery.crear(documentacion))).thenReturn(List.of());
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(get(RUTA, documentacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void debeRetornar400SinInvocarElInteractor_cuandoElIdNoEsUuid() throws Exception {
        // Arrange
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(get(RUTA, "no-es-uuid")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto))))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(consultarAutoresDocumentacionInteractor);
    }

    @Test
    void debeRetornar401SinInvocarElInteractor_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(consultarAutoresDocumentacionInteractor);
    }
}
