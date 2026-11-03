package com.arquisoft.artefactos.infrastructure.versionartefacto.command.primaryadapter.web;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.EliminarVersionArtefactoInteractor;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.EliminarVersionArtefactoCommand;
import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoConRevisionException;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EliminarVersionArtefactoController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, EliminarVersionArtefactoControllerTest.TestSecurityConfig.class})
class EliminarVersionArtefactoControllerTest {

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
    private EliminarVersionArtefactoInteractor interactor;

    private static RequestPostProcessor tokenEstudiante() {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(ESTUDIANTE_ID.toString()));
    }

    private static String ruta(Object version) {
        return "/artefactos/versiones/" + version;
    }

    @Test
    void debeResponder204_yUsarElEstudianteDelToken_cuandoSeElimina() throws Exception {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(delete(ruta(version)).with(tokenEstudiante()))
                .andExpect(status().isNoContent());
        var captor = ArgumentCaptor.forClass(EliminarVersionArtefactoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().versionArtefacto()).isEqualTo(version);
        assertThat(captor.getValue().solicitante()).isEqualTo(ESTUDIANTE_ID);
    }

    @Test
    void debeResponder400_yNoInvocarElInteractor_cuandoElIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(ruta("no-es-uuid")).with(tokenEstudiante()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(ruta(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder422_cuandoTieneRevision() throws Exception {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        doThrow(new VersionArtefactoConRevisionException(version, 2)).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(ruta(version)).with(tokenEstudiante()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ArtefactosCodes.VersionArtefacto.CON_REVISION));
    }
}
