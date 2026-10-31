package com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web;

import com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.RegistrarObservacionInteractor;
import com.arquisoft.artefactos.application.observacion.command.primaryport.model.RegistrarObservacionCommand;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoAdmiteObservacionesException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrarObservacionController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, RegistrarObservacionControllerTest.TestSecurityConfig.class})
class RegistrarObservacionControllerTest {

    private static final String RUTA = "/artefactos/revisiones/{revisionAsesorId}/observaciones";
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
    private RegistrarObservacionInteractor registrarObservacionInteractor;

    private static RequestPostProcessor tokenAsesor() {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(LLAMANTE_ID.toString()));
    }

    @Test
    void debeRetornar201ConElId_yDelegarConRutaBodyEIdentidad_cuandoEsValido() throws Exception {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        when(registrarObservacionInteractor.ejecutar(any())).thenReturn(id);

        // Act & Assert
        mockMvc.perform(post(RUTA, revision)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"descripcion\": \"  Falta bibliografia \" }")
                        .with(tokenAsesor()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()));

        var captor = ArgumentCaptor.forClass(RegistrarObservacionCommand.class);
        verify(registrarObservacionInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().revisionAsesor()).isEqualTo(revision);
        assertThat(captor.getValue().descripcion()).isEqualTo("Falta bibliografia");
        assertThat(captor.getValue().asesor()).isEqualTo(LLAMANTE_ID);
    }

    @Test
    void debeRetornar400ConFieldErrors_yNoInvocarElInteractor_cuandoLaDescripcionEstaEnBlanco() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"descripcion\": \"   \" }")
                        .with(tokenAsesor()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == '%s')]",
                        ArtefactosFields.Observacion.DESCRIPCION).exists());
        verify(registrarObservacionInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar400_yNoInvocarElInteractor_cuandoElIdDeLaRevisionEstaMalFormado() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, "no-es-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"descripcion\": \"Detalle\" }")
                        .with(tokenAsesor()))
                .andExpect(status().isBadRequest());
        verify(registrarObservacionInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoLaRevisionNoAdmiteObservaciones() throws Exception {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        doThrow(new RevisionAsesorNoAdmiteObservacionesException(revision, EstadoRevisionAsesor.CERRADA))
                .when(registrarObservacionInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(RUTA, revision)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"descripcion\": \"Detalle\" }")
                        .with(tokenAsesor()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ArtefactosCodes.RevisionAsesor.NO_ADMITE_OBSERVACIONES));
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"descripcion\": \"Detalle\" }"))
                .andExpect(status().isUnauthorized());
    }
}
