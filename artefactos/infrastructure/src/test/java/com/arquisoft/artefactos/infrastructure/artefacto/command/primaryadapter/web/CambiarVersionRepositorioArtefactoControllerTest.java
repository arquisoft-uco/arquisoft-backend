package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.CambiarVersionRepositorioArtefactoInteractor;
import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.CambiarVersionRepositorioArtefactoCommand;
import com.arquisoft.artefactos.domain.artefacto.exception.CambioVersionRepositorioNoPermitidoException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CambiarVersionRepositorioArtefactoController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, CambiarVersionRepositorioArtefactoControllerTest.TestSecurityConfig.class})
class CambiarVersionRepositorioArtefactoControllerTest {

    private static final String RUTA = "/artefactos/{artefactoId}/version-repositorio";
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
    private CambiarVersionRepositorioArtefactoInteractor cambiarVersionRepositorioArtefactoInteractor;

    private static RequestPostProcessor tokenAsesor() {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(LLAMANTE_ID.toString()));
    }

    private static String cuerpo(String version) {
        return "{\"versionRepositorioArtefacto\":\"" + version + "\"}";
    }

    @Test
    void debeRetornar204SinCuerpo_yDelegarConElAsesorDelToken_cuandoEsValido() throws Exception {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(patch(RUTA, artefacto).with(tokenAsesor())
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo(version.toString())))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        var captor = ArgumentCaptor.forClass(CambiarVersionRepositorioArtefactoCommand.class);
        verify(cambiarVersionRepositorioArtefactoInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().artefacto()).isEqualTo(artefacto);
        assertThat(captor.getValue().asesor()).isEqualTo(LLAMANTE_ID);
        assertThat(captor.getValue().versionRepositorioArtefacto()).isEqualTo(version);
    }

    @Test
    void debeRetornar400_yNoInvocarElInteractor_cuandoLaVersionNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID()).with(tokenAsesor())
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo("no-es-uuid")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isNotEmpty());
        verify(cambiarVersionRepositorioArtefactoInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoUnaReglaDeDominioRechaza() throws Exception {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        doThrow(new CambioVersionRepositorioNoPermitidoException("Formato A", artefacto, "RESUELTA"))
                .when(cambiarVersionRepositorioArtefactoInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, artefacto).with(tokenAsesor())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(ArtefactosCodes.Artefacto.CAMBIO_VERSION_REPOSITORIO_NO_PERMITIDO));
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnauthorized());
    }
}
