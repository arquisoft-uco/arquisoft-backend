package com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.primaryadapter.web;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.interactor.RemoverEstudianteArtefactoInteractor;
import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model.RemoverEstudianteArtefactoCommand;
import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudianteNoVinculadoArtefactoException;
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

@WebMvcTest(RemoverEstudianteArtefactoController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, RemoverEstudianteArtefactoControllerTest.TestSecurityConfig.class})
class RemoverEstudianteArtefactoControllerTest {

    private static final String RUTA = "/artefactos/{artefactoId}/estudiantes/{estudianteId}";
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
    private RemoverEstudianteArtefactoInteractor removerEstudianteArtefactoInteractor;

    private static RequestPostProcessor tokenAsesor() {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(LLAMANTE_ID.toString()));
    }

    @Test
    void debeRetornar204SinCuerpo_yDelegarConLosTresIdentificadores_cuandoEsValido() throws Exception {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA, artefacto, estudiante).with(tokenAsesor()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        var captor = ArgumentCaptor.forClass(RemoverEstudianteArtefactoCommand.class);
        verify(removerEstudianteArtefactoInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().artefacto()).isEqualTo(artefacto);
        assertThat(captor.getValue().estudiante()).isEqualTo(estudiante);
        assertThat(captor.getValue().asesor()).isEqualTo(LLAMANTE_ID);
    }

    @Test
    void debeRetornar400_yNoInvocarElInteractor_cuandoUnIdentificadorEsMalformado() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID(), "no-es-uuid").with(tokenAsesor()))
                .andExpect(status().isBadRequest());
        verify(removerEstudianteArtefactoInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoUnaReglaDeDominioRechaza() throws Exception {
        // Arrange
        doThrow(new EstudianteNoVinculadoArtefactoException(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Laura Gomez"))
                .when(removerEstudianteArtefactoInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()).with(tokenAsesor()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_NO_VINCULADO_ARTEFACTO));
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
    }
}
