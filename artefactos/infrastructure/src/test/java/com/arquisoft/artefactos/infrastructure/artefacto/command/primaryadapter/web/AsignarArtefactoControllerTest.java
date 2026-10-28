package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.AsignarArtefactoInteractor;
import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.AsignarArtefactoCommand;
import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
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

@WebMvcTest(AsignarArtefactoController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, AsignarArtefactoControllerTest.TestSecurityConfig.class})
class AsignarArtefactoControllerTest {

    private static final String RUTA = "/artefactos";
    private static final UUID ASESOR_ID = UtilUUID.generarNuevoUUID();

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
    private AsignarArtefactoInteractor asignarArtefactoInteractor;

    private static RequestPostProcessor tokenAsesor() {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(ASESOR_ID.toString()));
    }

    @Test
    void debeResponder201ConElId() throws Exception {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var primera = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        when(asignarArtefactoInteractor.ejecutar(any()))
                .thenReturn(artefacto);
        var body = """
                {
                  "proyectoGrado": "%s",
                  "versionRepositorioArtefacto": "%s",
                  "estudiantes": ["%s"]
                }
                """.formatted(proyecto, primera, estudiante);

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(tokenAsesor()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(artefacto.toString()));

        var captor = ArgumentCaptor.forClass(AsignarArtefactoCommand.class);
        verify(asignarArtefactoInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().asesor()).isEqualTo(ASESOR_ID);
        assertThat(captor.getValue().versionRepositorioArtefacto()).isEqualTo(primera);
        assertThat(captor.getValue().estudiantes()).containsExactly(estudiante);
    }

    @Test
    void debeRetornar400ConFieldErrors_yNoInvocarElInteractor_cuandoElBodyEsInvalido() throws Exception {
        // Arrange
        var body = """
                {
                  "proyectoGrado": "no-es-uuid",
                  "versionRepositorioArtefacto": "no-es-uuid",
                  "estudiantes": ["x"]
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(tokenAsesor()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == '%s')]",
                        ArtefactosFields.Artefacto.PROYECTO_GRADO).exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == '%s')]",
                        ArtefactosFields.Artefacto.VERSION_REPOSITORIO_ARTEFACTO).exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == '%s')]",
                        ArtefactosFields.EstudianteArtefacto.ESTUDIANTES).exists());
        verify(asignarArtefactoInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoUnaReglaDeDominioRechaza() throws Exception {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(asignarArtefactoInteractor.ejecutar(any())).thenThrow(new ProyectoGradoNoEncontradoException(proyecto));
        var body = """
                {
                  "proyectoGrado": "%s",
                  "versionRepositorioArtefacto": "%s",
                  "estudiantes": ["%s"]
                }
                """.formatted(proyecto, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(tokenAsesor()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ArtefactosCodes.ProyectoGrado.NO_ENCONTRADO));
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
