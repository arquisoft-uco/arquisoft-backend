package com.arquisoft.artefactos.infrastructure.versionartefacto.command.primaryadapter.web;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.SolicitarCargaVersionArtefactoInteractor;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.SolicitarCargaVersionArtefactoCommand;
import com.arquisoft.artefactos.application.versionartefacto.command.result.CargaVersionArtefactoResult;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
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

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SolicitarCargaVersionArtefactoController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, SolicitarCargaVersionArtefactoControllerTest.TestSecurityConfig.class})
class SolicitarCargaVersionArtefactoControllerTest {

    private static final UUID ESTUDIANTE_ID = UtilUUID.generarNuevoUUID();

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
                            .accessDeniedHandler((req, res, e) -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SolicitarCargaVersionArtefactoInteractor interactor;

    private static RequestPostProcessor tokenEstudiante() {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(ESTUDIANTE_ID.toString()));
    }

    private static String ruta(Object artefacto) {
        return "/artefactos/" + artefacto + "/versiones/cargas";
    }

    @Test
    void debeRetornar200ConElFormulario_yUsarElEstudianteDelToken() throws Exception {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var carga = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(new CargaVersionArtefactoResult(
                carga, "http://minio/bucket", Map.of("key", "valor"), Instant.parse("2026-10-26T10:00:00Z")));
        var body = """
                { "nombreArchivo": "informe.pdf", "tipoContenido": "application/pdf", "tamanoBytes": 2048 }
                """;

        // Act & Assert
        mockMvc.perform(post(ruta(artefacto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(tokenEstudiante()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carga").value(carga.toString()))
                .andExpect(jsonPath("$.url").value("http://minio/bucket"))
                .andExpect(jsonPath("$.campos.key").value("valor"))
                .andExpect(jsonPath("$.expiraEn").exists());
        var captor = ArgumentCaptor.forClass(SolicitarCargaVersionArtefactoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().artefacto()).isEqualTo(artefacto);
        assertThat(captor.getValue().solicitante()).isEqualTo(ESTUDIANTE_ID);
        assertThat(captor.getValue().nombreArchivo()).isEqualTo("informe.pdf");
        assertThat(captor.getValue().tamanoBytes()).isEqualTo(2048L);
    }

    @Test
    void debeRetornar400ConFieldErrors_yNoInvocarElInteractor_cuandoElBodyEsInvalido() throws Exception {
        // Arrange
        var body = """
                { "nombreArchivo": " ", "tipoContenido": " " }
                """;

        // Act & Assert
        mockMvc.perform(post(ruta(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(tokenEstudiante()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == '%s')]",
                        ArtefactosFields.VersionArtefacto.NOMBRE_ARCHIVO).exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == '%s')]",
                        ArtefactosFields.VersionArtefacto.TAMANO_BYTES).exists());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoUnaReglaDeDominioRechaza() throws Exception {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenThrow(new ArtefactoNoEncontradoException(artefacto));
        var body = """
                { "nombreArchivo": "informe.pdf", "tipoContenido": "application/pdf", "tamanoBytes": 10 }
                """;

        // Act & Assert
        mockMvc.perform(post(ruta(artefacto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(tokenEstudiante()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ArtefactosCodes.Artefacto.NO_ENCONTRADO));
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(ruta(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
