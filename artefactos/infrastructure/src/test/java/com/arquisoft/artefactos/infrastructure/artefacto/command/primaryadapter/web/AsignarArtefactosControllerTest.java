package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.AsignarArtefactosInteractor;
import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.AsignarArtefactosCommand;
import com.arquisoft.artefactos.application.artefacto.command.result.ArtefactosAsignadosResult;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AsignarArtefactosController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, AsignarArtefactosControllerTest.TestSecurityConfig.class})
class AsignarArtefactosControllerTest {

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
    private AsignarArtefactosInteractor asignarArtefactosInteractor;

    private static RequestPostProcessor tokenAsesor() {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(ASESOR_ID.toString()));
    }

    @Test
    void debeRetornar201ConLosIds_yAsignarConElAsesorDelToken() throws Exception {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var primera = UtilUUID.generarNuevoUUID();
        var segunda = UtilUUID.generarNuevoUUID();
        var primerArtefacto = UtilUUID.generarNuevoUUID();
        var segundoArtefacto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        when(asignarArtefactosInteractor.ejecutar(any()))
                .thenReturn(new ArtefactosAsignadosResult(List.of(primerArtefacto, segundoArtefacto)));
        var body = """
                {
                  "proyectoGrado": "%s",
                  "versionesRepositorioArtefacto": ["%s", "%s"],
                  "estudiantes": ["%s"]
                }
                """.formatted(proyecto, primera, segunda, estudiante);

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(tokenAsesor()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ids[0]").value(primerArtefacto.toString()))
                .andExpect(jsonPath("$.ids[1]").value(segundoArtefacto.toString()));

        var captor = ArgumentCaptor.forClass(AsignarArtefactosCommand.class);
        verify(asignarArtefactosInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().asesor()).isEqualTo(ASESOR_ID);
        assertThat(captor.getValue().versionesRepositorioArtefacto()).containsExactly(primera, segunda);
        assertThat(captor.getValue().estudiantes()).containsExactly(estudiante);
    }

    @Test
    void debeRetornar400_yNoInvocarElInteractor_cuandoUnEstudianteNoEsUuid() throws Exception {
        // Arrange
        var body = """
                {
                  "proyectoGrado": "%s",
                  "versionesRepositorioArtefacto": ["%s"],
                  "estudiantes": ["no-es-uuid"]
                }
                """.formatted(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(tokenAsesor()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == '%s')]",
                        ArtefactosFields.EstudianteArtefacto.ESTUDIANTES).exists());
        verify(asignarArtefactosInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar400ConFieldErrors_yNoInvocarElInteractor_cuandoElBodyEsInvalido() throws Exception {
        // Arrange
        var body = """
                {
                  "proyectoGrado": "no-es-uuid",
                  "versionesRepositorioArtefacto": []
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
                        ArtefactosFields.Artefacto.VERSIONES_REPOSITORIO_ARTEFACTO).exists());
        verify(asignarArtefactosInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoUnaReglaDeDominioRechaza() throws Exception {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(asignarArtefactosInteractor.ejecutar(any())).thenThrow(new ProyectoGradoNoEncontradoException(proyecto));
        var body = """
                {
                  "proyectoGrado": "%s",
                  "versionesRepositorioArtefacto": ["%s"]
                }
                """.formatted(proyecto, UtilUUID.generarNuevoUUID());

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
