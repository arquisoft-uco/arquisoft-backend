package com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.primaryadapter.web;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.interactor.AsignarEstudiantesArtefactoInteractor;
import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model.AsignarEstudiantesArtefactoCommand;
import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudianteYaVinculadoArtefactoException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AsignarEstudiantesArtefactoController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, AsignarEstudiantesArtefactoControllerTest.TestSecurityConfig.class})
class AsignarEstudiantesArtefactoControllerTest {

    private static final String RUTA = "/artefactos/{artefactoId}/estudiantes";
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
    private AsignarEstudiantesArtefactoInteractor asignarEstudiantesArtefactoInteractor;

    private static RequestPostProcessor tokenAsesor() {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(ASESOR_ID.toString()));
    }

    @Test
    void debeRetornar201SinCuerpo_yAsignarConElAsesorDelToken() throws Exception {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        var body = """
                {
                  "estudiantes": ["%s", "%s"]
                }
                """.formatted(primero, segundo);

        // Act & Assert
        mockMvc.perform(post(RUTA, artefacto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(tokenAsesor()))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        var captor = ArgumentCaptor.forClass(AsignarEstudiantesArtefactoCommand.class);
        verify(asignarEstudiantesArtefactoInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().artefacto()).isEqualTo(artefacto);
        assertThat(captor.getValue().asesor()).isEqualTo(ASESOR_ID);
        assertThat(captor.getValue().estudiantes()).containsExactly(primero, segundo);
    }

    @Test
    void debeRetornar400ConFieldErrors_yNoInvocarElInteractor_cuandoLaListaEstaVacia() throws Exception {
        // Arrange
        var body = """
                {
                  "estudiantes": []
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(tokenAsesor()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == '%s')]",
                        ArtefactosFields.EstudianteArtefacto.ESTUDIANTES).exists());
        verify(asignarEstudiantesArtefactoInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoUnaReglaDeDominioRechaza() throws Exception {
        // Arrange
        doThrow(new EstudianteYaVinculadoArtefactoException(UtilUUID.generarNuevoUUID(), "Laura Gomez"))
                .when(asignarEstudiantesArtefactoInteractor).ejecutar(any());
        var body = """
                {
                  "estudiantes": ["%s"]
                }
                """.formatted(UtilUUID.generarNuevoUUID());

        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(tokenAsesor()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_YA_VINCULADO));
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
