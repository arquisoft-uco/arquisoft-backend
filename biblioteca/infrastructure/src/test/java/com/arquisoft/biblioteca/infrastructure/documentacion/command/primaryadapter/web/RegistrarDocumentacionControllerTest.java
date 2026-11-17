package com.arquisoft.biblioteca.infrastructure.documentacion.command.primaryadapter.web;

import com.arquisoft.biblioteca.application.documentacion.command.primaryport.interactor.RegistrarDocumentacionInteractor;
import com.arquisoft.biblioteca.application.documentacion.command.primaryport.model.RegistrarDocumentacionCommand;
import com.arquisoft.biblioteca.domain.documentacion.exception.DocumentacionYaRegistradaException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrarDocumentacionController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, RegistrarDocumentacionControllerTest.TestSecurityConfig.class})
class RegistrarDocumentacionControllerTest {

    private static final String RUTA = "/biblioteca/documentaciones";

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
    private RegistrarDocumentacionInteractor registrarDocumentacionInteractor;

    @Test
    void debeResponder201ConElIdYPasarElComando_cuandoLaPeticionEsValida() throws Exception {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var sujeto = UtilUUID.generarNuevoUUID();
        var idCreado = UtilUUID.generarNuevoUUID();
        when(registrarDocumentacionInteractor.ejecutar(any())).thenReturn(idCreado);

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"entregableProyectoGradoId\":\"" + entregable + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(idCreado.toString()));

        var captor = ArgumentCaptor.forClass(RegistrarDocumentacionCommand.class);
        verify(registrarDocumentacionInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(captor.getValue().bibliotecario()).isEqualTo(sujeto);
    }

    @Test
    void debeResponder400SinInvocarElInteractor_cuandoElCuerpoNoTraeUnUuidValido() throws Exception {
        // Arrange
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"entregableProyectoGradoId\":\"abc\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field")
                        .value(BibliotecaFields.Documentacion.ENTREGABLE_PROYECTO_GRADO));
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
        verify(registrarDocumentacionInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401SinInvocarElInteractor_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"entregableProyectoGradoId\":\"" + UtilUUID.generarNuevoUUID() + "\"}"))
                .andExpect(status().isUnauthorized());
        verify(registrarDocumentacionInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder422ConElCodigoDeYaRegistrada_cuandoLaReglaDeDominioRechaza() throws Exception {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        doThrow(new DocumentacionYaRegistradaException(entregable))
                .when(registrarDocumentacionInteractor).ejecutar(any());
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"entregableProyectoGradoId\":\"" + entregable + "\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(BibliotecaCodes.Documentacion.YA_REGISTRADA));
    }
}
