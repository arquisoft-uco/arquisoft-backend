package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.interactor.CambiarEstadoEntregableProyectoGradoInteractor;
import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.CambiarEstadoEntregableProyectoGradoCommand;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EntregableProyectoGradoNoEncontradoException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.EntregablesCodes;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CambiarEstadoEntregableProyectoGradoController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, CambiarEstadoEntregableProyectoGradoControllerTest.TestSecurityConfig.class})
class CambiarEstadoEntregableProyectoGradoControllerTest {

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
                            .accessDeniedHandler((req, res, e) -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CambiarEstadoEntregableProyectoGradoInteractor interactor;

    private static RequestPostProcessor tokenAsesor() {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(ASESOR_ID.toString()));
    }

    private static String ruta(Object entregable) {
        return "/entregables-proyecto-grado/" + entregable + "/estado";
    }

    private static String cuerpo(String estado) {
        return "{\"estadoEntregable\": \"" + estado + "\"}";
    }

    @Test
    void debeResponder204YPasarIdYEstadoAlInteractor_cuandoEsValido() throws Exception {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(patch(ruta(entregable)).with(tokenAsesor())
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo("CERRADO")))
                .andExpect(status().isNoContent());
        var captor = ArgumentCaptor.forClass(CambiarEstadoEntregableProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(captor.getValue().estadoEntregable()).isEqualTo("CERRADO");
    }

    @Test
    void debeResponder400YNoInvocarElInteractor_cuandoElIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(ruta("no-es-uuid")).with(tokenAsesor())
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo("CERRADO")))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder400YNoInvocarElInteractor_cuandoElEstadoEstaVacio() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(ruta(UtilUUID.generarNuevoUUID())).with(tokenAsesor())
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo(" ")))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder422ConElCodigoDeError_cuandoElEntregableNoExiste() throws Exception {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        doThrow(new EntregableProyectoGradoNoEncontradoException(entregable)).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(ruta(entregable)).with(tokenAsesor())
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo("CANCELADO")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(EntregablesCodes.EntregableProyectoGrado.NO_ENCONTRADO));
    }

    @Test
    void debeResponder401YNoInvocarElInteractor_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(ruta(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo("CERRADO")))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }
}
