package com.arquisoft.artefactos.infrastructure.versionartefacto.command.primaryadapter.web;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.GenerarVersionArtefactoInteractor;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.GenerarVersionArtefactoCommand;
import com.arquisoft.artefactos.application.versionartefacto.command.result.VersionArtefactoGeneradaResult;
import com.arquisoft.artefactos.domain.versionartefacto.exception.CargaPendienteNoEncontradaException;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenerarVersionArtefactoController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, GenerarVersionArtefactoControllerTest.TestSecurityConfig.class})
class GenerarVersionArtefactoControllerTest {

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
    private GenerarVersionArtefactoInteractor interactor;

    private static RequestPostProcessor tokenEstudiante() {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(ESTUDIANTE_ID.toString()));
    }

    private static String ruta(Object artefacto) {
        return "/artefactos/" + artefacto + "/versiones";
    }

    @Test
    void debeRetornar201ConIdYVersion_yUsarElEstudianteDelToken() throws Exception {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var carga = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(new VersionArtefactoGeneradaResult(id, 3));

        // Act & Assert
        mockMvc.perform(post(ruta(artefacto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"carga\": \"%s\" }".formatted(carga))
                        .with(tokenEstudiante()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.version").value(3));
        var captor = ArgumentCaptor.forClass(GenerarVersionArtefactoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().carga()).isEqualTo(carga);
        assertThat(captor.getValue().artefacto()).isEqualTo(artefacto);
        assertThat(captor.getValue().solicitante()).isEqualTo(ESTUDIANTE_ID);
    }

    @Test
    void debeRetornar400_yNoInvocarElInteractor_cuandoLaCargaNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(post(ruta(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"carga\": \"no-es-uuid\" }")
                        .with(tokenEstudiante()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == '%s')]",
                        ArtefactosFields.VersionArtefacto.CARGA).exists());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoLaCargaNoExiste() throws Exception {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenThrow(new CargaPendienteNoEncontradaException(carga));

        // Act & Assert
        mockMvc.perform(post(ruta(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"carga\": \"%s\" }".formatted(carga))
                        .with(tokenEstudiante()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ArtefactosCodes.VersionArtefacto.CARGA_NO_ENCONTRADA));
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
