package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.SolicitarCargaVersionRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model.SolicitarCargaVersionRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.CargaVersionRepositorioArtefactoResult;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.exception.RepositorioArtefactoNoEncontradoException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.minio.exception.MinioOperationException;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SolicitarCargaVersionRepositorioArtefactoController.class)
@Import({IdentidadActualTestConfig.class,
        AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        SolicitarCargaVersionRepositorioArtefactoControllerTest.TestSecurityConfig.class
})
class SolicitarCargaVersionRepositorioArtefactoControllerTest {

    private static final String BODY_VALIDO = """
            { "nombreArchivo": "formato.pdf", "tipoContenido": "application/pdf", "tamanoBytes": 2048 }
            """;

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
    private SolicitarCargaVersionRepositorioArtefactoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static String ruta(Object repositorio) {
        return "/repositorio-artefactos/" + repositorio + "/versiones/cargas";
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermiso(String subject) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(subject));
    }

    @Test
    void debeResponder200ConElFormulario_cuandoLaPeticionEsValida() throws Exception {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        var carga = UtilUUID.generarNuevoUUID();
        var expiraEn = Instant.parse("2026-10-15T20:00:00Z");
        when(interactor.ejecutar(any())).thenReturn(new CargaVersionRepositorioArtefactoResult(
                carga, "http://localhost:9000/repositorio-artefactos", Map.of("key", "pendientes/" + carga),
                expiraEn));

        // Act & Assert
        mockMvc.perform(post(ruta(repositorio))
                        .with(jwtConPermiso(solicitante.toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carga").value(carga.toString()))
                .andExpect(jsonPath("$.url").value("http://localhost:9000/repositorio-artefactos"))
                .andExpect(jsonPath("$.campos.key").value("pendientes/" + carga))
                .andExpect(jsonPath("$.expiraEn").exists());
        var captor = ArgumentCaptor.forClass(SolicitarCargaVersionRepositorioArtefactoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().repositorio()).isEqualTo(repositorio);
        assertThat(captor.getValue().solicitante()).isEqualTo(solicitante);
        assertThat(captor.getValue().tamanoBytes()).isEqualTo(2048L);
    }

    @Test
    void debeResponder400_cuandoTamanoNulo() throws Exception {
        // Act & Assert
        mockMvc.perform(post(ruta(UtilUUID.generarNuevoUUID()))
                        .with(jwtConPermiso(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "nombreArchivo": "formato.pdf", "tipoContenido": "application/pdf" }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1));
        verifyNoInteractions(interactor);
    }

    @Test
    void debeResponder401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(ruta(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder422_cuandoRepositorioNoExiste() throws Exception {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        doThrow(new RepositorioArtefactoNoEncontradoException(repositorio)).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(ruta(repositorio))
                        .with(jwtConPermiso(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(RepositorioArtefactosCodes.RepositorioArtefacto.NO_ENCONTRADO));
    }

    @Test
    void debeResponder503_cuandoMinioFalla() throws Exception {
        // Arrange
        doThrow(MinioOperationException.formularioCargaFallido(new RuntimeException()))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(ruta(UtilUUID.generarNuevoUUID()))
                        .with(jwtConPermiso(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.errorCode").value(AppCodes.Minio.FORMULARIO_CARGA_FALLIDO));
    }
}
