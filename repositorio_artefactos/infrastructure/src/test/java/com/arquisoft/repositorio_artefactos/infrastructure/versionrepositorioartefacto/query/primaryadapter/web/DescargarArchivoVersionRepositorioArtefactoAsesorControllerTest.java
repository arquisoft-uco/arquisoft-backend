package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.interactor.DescargarArchivoVersionRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.model.DescargarArchivoVersionRepositorioArtefactoQuery;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.EnlaceDescargaVersionReadModel;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.exception.ArchivoVersionNoDisponibleException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
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
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DescargarArchivoVersionRepositorioArtefactoAsesorController.class)
@Import({IdentidadActualTestConfig.class,
        AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        DescargarArchivoVersionRepositorioArtefactoAsesorControllerTest.TestSecurityConfig.class
})
class DescargarArchivoVersionRepositorioArtefactoAsesorControllerTest {

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
    private DescargarArchivoVersionRepositorioArtefactoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static String ruta(Object version) {
        return "/repositorio-artefactos/versiones/asesor/" + version + "/descarga";
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwt() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(UtilUUID.generarNuevoUUID().toString()));
    }

    @Test
    void debeResponder200ConNoStore_cuandoLaVersionExiste() throws Exception {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var expiraEn = Instant.parse("2026-11-02T10:05:00Z");
        when(interactor.ejecutar(any(DescargarArchivoVersionRepositorioArtefactoQuery.class)))
                .thenReturn(new EnlaceDescargaVersionReadModel("https://s3.test/firmada", expiraEn));

        // Act & Assert
        mockMvc.perform(get(ruta(version)).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
                .andExpect(jsonPath("$.url").value("https://s3.test/firmada"))
                .andExpect(jsonPath("$.expiraEn").exists());
        var captor = ArgumentCaptor.forClass(DescargarArchivoVersionRepositorioArtefactoQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().version()).isEqualTo(version);
    }

    @Test
    void debeResponder400_cuandoElIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ruta("no-es-uuid")).with(jwt()))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(interactor);
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ruta(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(interactor);
    }

    @Test
    void debeResponder422_cuandoLaVersionNoExiste() throws Exception {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        doThrow(new VersionRepositorioArtefactoNoEncontradaException(version))
                .when(interactor).ejecutar(any(DescargarArchivoVersionRepositorioArtefactoQuery.class));

        // Act & Assert
        mockMvc.perform(get(ruta(version)).with(jwt()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(RepositorioArtefactosCodes.VersionRepositorioArtefacto.VERSION_NO_ENCONTRADA));
    }

    @Test
    void debeResponder503_cuandoElObjetoNoExisteEnAlmacenamiento() throws Exception {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        doThrow(new ArchivoVersionNoDisponibleException(version, "versiones/" + version))
                .when(interactor).ejecutar(any(DescargarArchivoVersionRepositorioArtefactoQuery.class));

        // Act & Assert
        mockMvc.perform(get(ruta(version)).with(jwt()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.errorCode")
                        .value(RepositorioArtefactosCodes.VersionRepositorioArtefacto.ARCHIVO_NO_DISPONIBLE));
    }
}
