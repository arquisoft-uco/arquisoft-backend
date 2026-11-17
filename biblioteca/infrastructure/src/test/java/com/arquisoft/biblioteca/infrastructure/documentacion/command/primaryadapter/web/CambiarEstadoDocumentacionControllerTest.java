package com.arquisoft.biblioteca.infrastructure.documentacion.command.primaryadapter.web;

import com.arquisoft.biblioteca.application.documentacion.command.primaryport.interactor.CambiarEstadoDocumentacionInteractor;
import com.arquisoft.biblioteca.application.documentacion.command.primaryport.model.CambiarEstadoDocumentacionCommand;
import com.arquisoft.biblioteca.domain.documentacion.exception.EstadoDocumentacionSinCambioException;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CambiarEstadoDocumentacionController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, CambiarEstadoDocumentacionControllerTest.TestSecurityConfig.class})
class CambiarEstadoDocumentacionControllerTest {

    private static final String RUTA = "/biblioteca/documentaciones/{id}/estado";

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
    private CambiarEstadoDocumentacionInteractor cambiarEstadoDocumentacionInteractor;

    @Test
    void debeResponder204SinCuerpoYPasarElComando_cuandoLaPeticionEsValida() throws Exception {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(patch(RUTA, documentacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"PUBLICADO\"}"))
                .andExpect(status().isNoContent())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEmpty());

        var captor = ArgumentCaptor.forClass(CambiarEstadoDocumentacionCommand.class);
        verify(cambiarEstadoDocumentacionInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().documentacion()).isEqualTo(documentacion);
        assertThat(captor.getValue().estado()).isEqualTo(EstadoDocumentacion.PUBLICADO.getId());
    }

    @Test
    void debeResponder400SinInvocarElInteractor_cuandoElEstadoEstaAusenteOEnBlanco() throws Exception {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(patch(RUTA, documentacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"  \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field")
                        .value(BibliotecaFields.Documentacion.ESTADO));
        mockMvc.perform(patch(RUTA, documentacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
        verify(cambiarEstadoDocumentacionInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder400SinInvocarElInteractor_cuandoElIdentificadorEstaMalFormado() throws Exception {
        // Arrange
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(patch(RUTA, "abc")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"PUBLICADO\"}"))
                .andExpect(status().isBadRequest());
        verify(cambiarEstadoDocumentacionInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401SinInvocarElInteractor_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"PUBLICADO\"}"))
                .andExpect(status().isUnauthorized());
        verify(cambiarEstadoDocumentacionInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder422ConElCodigoDeSinCambio_cuandoLaReglaDeDominioRechaza() throws Exception {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        doThrow(new EstadoDocumentacionSinCambioException(documentacion, EstadoDocumentacion.PUBLICADO))
                .when(cambiarEstadoDocumentacionInteractor).ejecutar(any());
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(patch(RUTA, documentacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"PUBLICADO\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(BibliotecaCodes.Documentacion.ESTADO_SIN_CAMBIO));
    }
}
