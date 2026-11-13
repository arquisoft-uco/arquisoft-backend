package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web;

import com.arquisoft.biblioteca.application.documentacion.query.primaryport.interactor.ConsultarEnlaceDocumentacionInteractor;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.ConsultarEnlaceDocumentacionQuery;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDocumentacionReadModel;
import com.arquisoft.biblioteca.domain.documentacion.exception.DocumentacionNoCompartibleException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEnlaceDocumentacionController.class)
@Import({AppLoggerConfig.class, IdentidadActualTestConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, ConsultarEnlaceDocumentacionControllerTest.TestSecurityConfig.class})
class ConsultarEnlaceDocumentacionControllerTest {

    private static final String RUTA = "/biblioteca/documentaciones/{documentacionId}/enlace";

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
    private ConsultarEnlaceDocumentacionInteractor consultarEnlaceDocumentacionInteractor;

    @Test
    void debeRetornar200ConElEnlaceSinUrlDelArchivo_cuandoLaDocumentacionEstaPublicada() throws Exception {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var readModel = new EnlaceDocumentacionReadModel(
                "https://front.test/doc/" + documentacion, documentacion,
                new DocumentacionReadModel.EntregableProyectoGradoResumen(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 2, "Aprobado"),
                new DocumentacionReadModel.BibliotecarioResumen(UtilUUID.generarNuevoUUID(), "Ana"),
                new DocumentacionReadModel.EstadoDocumentacionResumen("PUBLICADO", "Publicado"));
        when(consultarEnlaceDocumentacionInteractor.ejecutar(
                ConsultarEnlaceDocumentacionQuery.crear(documentacion))).thenReturn(readModel);
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(get(RUTA, documentacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enlace").value("https://front.test/doc/" + documentacion))
                .andExpect(jsonPath("$.id").value(documentacion.toString()))
                .andExpect(jsonPath("$.bibliotecario.nombre").value("Ana"))
                .andExpect(jsonPath("$.estadoDocumentacion.id").value("PUBLICADO"))
                .andExpect(jsonPath("$.entregableProyectoGrado.versionEntregable").value(2))
                .andExpect(jsonPath("$.url").doesNotExist())
                .andExpect(jsonPath("$.fechaPublicacion").doesNotExist());
        verify(consultarEnlaceDocumentacionInteractor).ejecutar(
                ConsultarEnlaceDocumentacionQuery.crear(documentacion));
    }

    @Test
    void debeRetornar400SinInvocarElInteractor_cuandoElIdNoEsUuid() throws Exception {
        // Arrange
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(get(RUTA, "no-es-uuid")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto))))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(consultarEnlaceDocumentacionInteractor);
    }

    @Test
    void debeRetornar401SinInvocarElInteractor_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(consultarEnlaceDocumentacionInteractor);
    }

    @Test
    void debeRetornar422ConElCodigoNoCompartible_cuandoLaDocumentacionNoSePuedeCompartir() throws Exception {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        when(consultarEnlaceDocumentacionInteractor.ejecutar(
                ConsultarEnlaceDocumentacionQuery.crear(documentacion)))
                .thenThrow(new DocumentacionNoCompartibleException(documentacion));
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(get(RUTA, documentacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto))))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.errorCode").value(BibliotecaCodes.Documentacion.NO_COMPARTIBLE));
    }
}
