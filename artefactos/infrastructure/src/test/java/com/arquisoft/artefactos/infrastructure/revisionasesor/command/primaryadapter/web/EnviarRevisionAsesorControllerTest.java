package com.arquisoft.artefactos.infrastructure.revisionasesor.command.primaryadapter.web;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.EnviarRevisionAsesorInteractor;
import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.EnviarRevisionAsesorCommand;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoEnviableException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnviarRevisionAsesorController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, EnviarRevisionAsesorControllerTest.TestSecurityConfig.class})
class EnviarRevisionAsesorControllerTest {

    private static final String RUTA = "/artefactos/revisiones/{revisionAsesorId}/envio";

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
    private EnviarRevisionAsesorInteractor interactor;

    @Test
    void debe204YDelegarConAsesorDelToken_cuandoPeticionValida() throws Exception {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(patch(RUTA, revision)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(asesor.toString()))))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        var captor = ArgumentCaptor.forClass(EnviarRevisionAsesorCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().revisionAsesor()).isEqualTo(revision);
        assertThat(captor.getValue().asesor()).isEqualTo(asesor);
    }

    @Test
    void debe400_cuandoElIdentificadorDeLaRevisionEstaMalFormado() throws Exception {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(patch(RUTA, "no-es-un-uuid")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(asesor.toString()))))
                .andExpect(status().isBadRequest());

        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debe401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());

        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debe422_cuandoLaReglaDeDominioRechaza() throws Exception {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        doThrow(new RevisionAsesorNoEnviableException(revision, EstadoRevisionAsesor.EN_PROGRESO))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, revision)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(asesor.toString()))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ArtefactosCodes.RevisionAsesor.NO_ENVIABLE));
    }
}
