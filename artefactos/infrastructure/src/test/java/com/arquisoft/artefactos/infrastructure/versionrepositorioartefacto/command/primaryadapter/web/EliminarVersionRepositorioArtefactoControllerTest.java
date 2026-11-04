package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.EliminarVersionRepositorioArtefactoInteractor;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model.EliminarVersionRepositorioArtefactoCommand;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoEnUsoException;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EliminarVersionRepositorioArtefactoController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, EliminarVersionRepositorioArtefactoControllerTest.TestSecurityConfig.class})
class EliminarVersionRepositorioArtefactoControllerTest {

    private static final String RUTA = "/artefactos/versiones-repositorio/{versionRepositorioArtefactoId}";
    private static final UUID LLAMANTE_ID = UtilUUID.generarNuevoUUID();

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
    private EliminarVersionRepositorioArtefactoInteractor eliminarVersionRepositorioArtefactoInteractor;

    private static RequestPostProcessor tokenAdministrador() {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(LLAMANTE_ID.toString()));
    }

    @Test
    void debeResponder204SinCuerpo_yDelegarConLaVersionDeLaRuta_cuandoSeElimina() throws Exception {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA, version).with(tokenAdministrador()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        var captor = ArgumentCaptor.forClass(EliminarVersionRepositorioArtefactoCommand.class);
        verify(eliminarVersionRepositorioArtefactoInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().version()).isEqualTo(version);
    }

    @Test
    void debeResponder400_yNoInvocarElInteractor_cuandoElIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, "no-es-uuid").with(tokenAdministrador()))
                .andExpect(status().isBadRequest());
        verify(eliminarVersionRepositorioArtefactoInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
        verify(eliminarVersionRepositorioArtefactoInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder422_cuandoEstaEnUso() throws Exception {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        doThrow(new VersionRepositorioArtefactoEnUsoException("Formato A", 2, version))
                .when(eliminarVersionRepositorioArtefactoInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, version).with(tokenAdministrador()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ArtefactosCodes.VersionRepositorioArtefacto.EN_USO));
    }
}
